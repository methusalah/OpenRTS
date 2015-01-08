/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.army.motion;

import model.battlefield.army.components.Mover;
import geometry.Point2D;
import geometry.Segment2D;
import geometry3D.Point3D;
import java.util.ArrayList;
import math.Angle;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class SteeringMachine {
    private static double DESTINATION_REACH_TOLERANCE = 0.3;
    private static double DESTINATION_REACH_SIGNAL_DISTANCE = 2;
    private static double MAX_ANTICIPATION = 2.5;

    private static double FOLLOW_PATH_FORCE = 1;
    private static double ALIGNMENT_FORCE = 0.7;
    private static double SEPARATION_FORCE = 3;
    private static double SEPARATION_FORCE_FOR_FLYING = 0.01;
    private static double COHESION_FORCE = 1;
    
    
    Mover mover = null;
    
    ArrayList<? extends Mover> holdingNeighbours;
    
    Point3D steering = Point3D.ORIGIN;
    
    public Point3D separationForce = Point3D.ORIGIN;
    public Point3D cohesionForce = Point3D.ORIGIN;
    public Point3D alignementForce = Point3D.ORIGIN;
    public Point3D destinationForce = Point3D.ORIGIN;
    public Point3D avoidModification = Point3D.ORIGIN;
    
    public SteeringMachine(Mover m) {
        mover = m;
    }

    public Point3D getSteeringAndReset(double elapsedTime){
        steering = steering.getTruncation(elapsedTime);
        steering = steering.getDivision(mover.movable.getMass());
        Point3D res = new Point3D(steering);
        
        steering = Point3D.ORIGIN;
        return res;
    }
    
    public void proceedToDestination(){
        destinationForce = getFollowFlowFieldForce();
        steering = steering.getAddition(destinationForce);
    }
    
    public void applySeparation(ArrayList<? extends Mover> neighbors){
        separationForce = getSeparationForce(neighbors);
        steering = steering.getAddition(separationForce);
    }
    
    public void applyCohesion(ArrayList<? extends Mover> neighbors){
        cohesionForce = getCohesionForce(neighbors);
        steering = steering.getAddition(cohesionForce);
    }
    
    public void applyAlignment(ArrayList<? extends Mover> neighbors){
        alignementForce = getAlignmentForce(neighbors);
        steering = steering.getAddition(alignementForce);
    }
    
    public void avoidHoldingUnits(ArrayList<? extends Mover> holdingUnits){
        Point3D savedSteering = steering;
        modifySteeringToAvoid(holdingUnits);
        if(savedSteering.equals(steering))
            avoidModification = Point3D.ORIGIN;
        else
            avoidModification = steering;
    }
    
    public void seek(Point3D target){
        steering = steering.getAddition(target.getSubtraction(mover.pos).getNormalized());
    }
    
    public void seek(Mover m ){
        seek(m.pos);
    }
    
    private Point3D getFollowFlowFieldForce(){
        Point2D destination = mover.getDestination();
        if(destination == null)
            return Point3D.ORIGIN;
        else if(mover.getPos2D().getDistance(destination) < DESTINATION_REACH_TOLERANCE){
            mover.setDestinationReached();
            return Point3D.ORIGIN;
        } else{
            Point2D flatForce;
            if(mover.fly())
                flatForce = destination.getSubtraction(mover.getPos2D()).getNormalized().getMult(FOLLOW_PATH_FORCE);
            else
                flatForce = mover.flowfield.getVector(mover.getPos2D()).getMult(FOLLOW_PATH_FORCE);
            return new Point3D(flatForce, 0);
        }
    }

    private Point3D getSeparationForce(ArrayList<? extends Mover> neighbors) {
        Point3D res = Point3D.ORIGIN;
        if(neighbors.isEmpty())
            return res;
        
        for(Mover n : neighbors){
            double neededDistance = n.getSpacing(mover)-n.getDistance(mover);
            if(neededDistance <= 0)
                continue;
            Point3D sepVector = n.getVectorTo(mover).getScaled(neededDistance);
            res = res.getAddition(sepVector);
        }
        if(res.isOrigin())
            return res;
        if(mover.fly())
            return res.getNormalized().getMult(SEPARATION_FORCE_FOR_FLYING);
        else
            return res.getNormalized().getMult(SEPARATION_FORCE);
    }

    private Point3D getCohesionForce(ArrayList<? extends Mover> neighbors) {
        Point3D res = Point3D.ORIGIN;
        if(neighbors.isEmpty())
            return res;
        
        for(Mover n : neighbors)
            res = res.getAddition(n.pos);
        res = res.getDivision(neighbors.size());
        res = res.getSubtraction(mover.pos);
        return res.getNormalized().getMult(COHESION_FORCE);
    }

    private Point3D getAlignmentForce(ArrayList<? extends Mover> neighbors) {
        Point3D res = Point3D.ORIGIN;
        if(neighbors.isEmpty())
            return res;
        
        for(Mover n : neighbors)
            res = res.getAddition(n.velocity);
        res = res.getDivision(neighbors.size());
        return res.getNormalized().getMult(ALIGNMENT_FORCE);
    }

    /**
     * avoidance is on (x;y) plane only
     * @param holdingMovers 
     */
    private void modifySteeringToAvoid(ArrayList<? extends Mover> holdingMovers) {
        if(mover.velocity.equals(Point3D.ORIGIN))
            return;
        
        Segment2D anticipation = new Segment2D(mover.getPos2D(), mover.getPos2D().getTranslation(new Point2D(steering).getAngle(), MAX_ANTICIPATION));
        
        Point2D intersection = null;
        Mover obstacle = null;
        for(Mover m : holdingMovers){
            Point2D i = anticipation.getIntersectionsWithCircle(m.getPos2D(), mover.getSpacing(m)).get(0);
            if(i != null &&
                    (intersection == null || i.getDistance(mover.getPos2D()) < intersection.getDistance(mover.getPos2D()))){
                intersection = i;
                obstacle = m;
            }
        }
        
        if(obstacle == null)
            return;
        // if we are too close, we do not try to avoid and let the constraint manager manage the collision
        if(obstacle.getDistance(mover) < obstacle.getSpacing(mover)*1.1)
            return;
        
        double hypotenuse = mover.getPos2D().getDistance(obstacle.getPos2D());
        double opposé = mover.getSpacing(obstacle);
        
        double adjacent = Math.sqrt(hypotenuse*hypotenuse-opposé*opposé);
        double avoidanceAngle = Math.atan(opposé/adjacent)*1.1;

        Point2D toObstacle = obstacle.getPos2D().getSubtraction(mover.getPos2D()).getNormalized();
        
        if(Angle.getOrientedDifference(new Point2D(steering).getAngle(), toObstacle.getAngle()) < 0)
            steering = new Point3D(toObstacle.getRotation(avoidanceAngle), steering.z);
        else
            steering = new Point3D(toObstacle.getRotation(-avoidanceAngle), steering.z);
    }
    
    
}
