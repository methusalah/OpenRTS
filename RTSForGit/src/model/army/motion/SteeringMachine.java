/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.motion;

import model.army.data.Mover;
import geometry.Point2D;
import geometry.Segment2D;
import java.util.ArrayList;
import math.Angle;

/**
 *
 * @author Benoît
 */
public class SteeringMachine {
    private static double DESTINATION_REACH_TOLERANCE = 1.4;
    private static double WAYPOINT_WIDTH = 0.3;
    private static double NEIGHBOR_AHEAD_DIST = 0.5;
    private static double NEIGHBOR_AHEAD_RAD = 0.3;
    private static double MAX_ANTICIPATION = 2.5;

    private static double FOLLOW_PATH_FORCE = 1;
    private static double ALIGNMENT_FORCE = 0.7;
    private static double SEPARATION_FORCE = 2.5;
    private static double SEPARATION_FORCE_FOR_FLYING = 0.01;
    private static double COHESION_FORCE = 1;
    
    
    Mover mover = null;
    
    ArrayList<? extends Mover> holdingNeighbours;
    
    Point2D steering = Point2D.ORIGIN;
    
    public Point2D separationForce = Point2D.ORIGIN;
    public Point2D cohesionForce = Point2D.ORIGIN;
    public Point2D alignementForce = Point2D.ORIGIN;
    public Point2D destinationForce = Point2D.ORIGIN;
    public Point2D avoidModification = Point2D.ORIGIN;
    
    public SteeringMachine(Mover m) {
        mover = m;
    }

    public Point2D getSteeringAndReset(double elapsedTime){
        steering = steering.getTruncation(elapsedTime);
        steering = steering.getDivision(mover.movable.getMass());
        Point2D res = new Point2D(steering);
        
        steering = Point2D.ORIGIN;
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
        Point2D savedSteering = steering;
        modifySteeringToAvoid(holdingUnits);
        if(savedSteering.equals(steering))
            avoidModification = Point2D.ORIGIN;
        else
            avoidModification = steering;
    }
    
    public void seek(Point2D target){
        steering = steering.getAddition(target.getSubtraction(mover.pos).getNormalized());
    }
    
    public void seek(Mover m ){
        seek(m.pos);
    }
    
    private Point2D getFollowFlowFieldForce(){
        if(!mover.hasDestination())
            return Point2D.ORIGIN;
        else if(mover.pos.getDistance(mover.getDestination()) < DESTINATION_REACH_TOLERANCE){
            mover.setDestinationReached();
            return Point2D.ORIGIN;
        } else if(mover.fly())
            return mover.flowfield.destination.getSubtraction(mover.pos).getNormalized().getMult(FOLLOW_PATH_FORCE);
        else
            return mover.flowfield.getVector(mover.pos).getMult(FOLLOW_PATH_FORCE);
    }
        
    private Point2D getFollowPathForce(){
        Point2D target = null;
        if(mover.hasDestination()) {
            discardWaypoint();
            if(mover.hasDestination())
                target = mover.path.getFirstWaypoint();
        }
        
        if(target != null)
            return target.getSubtraction(mover.pos).getNormalized().getMult(FOLLOW_PATH_FORCE);
        else
            return Point2D.ORIGIN;
    }
    
    private void discardWaypoint(){
        Point2D w = mover.path.getFirstWaypoint();
        double dist = w.getDistance(mover.pos);
        boolean discard = false;
        
        if(dist <= WAYPOINT_WIDTH)
            discard = true;
        else if(dist <= DESTINATION_REACH_TOLERANCE) {
            if(mover.path.size() == 1) {
                discard = true;
            } else {
                Point2D nextW = mover.path.get(1);
                Point2D previousW = mover.path.getLastDiscarded();
                if(previousW != null && Angle.getTurn(nextW, w, mover.pos) != Angle.getTurn(nextW, w, previousW))
                    discard = true;
            }
        }
        
        if(mover.path.size() > 1 && mover.pos.getDistance(mover.path.get(1)) < dist/2)
            discard = true;
            
        if(discard){
            mover.path.discardFirstWaypoint();
        }
    }
    
    private Point2D getSeparationForce(ArrayList<? extends Mover> neighbors) {
        Point2D res = Point2D.ORIGIN;
        if(neighbors.isEmpty())
            return res;
        
        for(Mover n : neighbors){
            double neededDistance = n.getSpacing(mover)-n.getDistance(mover);
            Point2D sepVector = n.getVectorTo(mover).getScaled(neededDistance);
            res = res.getAddition(sepVector);
        }
        if(mover.fly())
            return res.getNormalized().getMult(SEPARATION_FORCE_FOR_FLYING);
        else
            return res.getNormalized().getMult(SEPARATION_FORCE);
    }

    private Point2D getCohesionForce(ArrayList<? extends Mover> neighbors) {
        if(neighbors.isEmpty())
            return Point2D.ORIGIN;
        Point2D res = Point2D.ORIGIN;
        for(Mover n : neighbors)
            res = res.getAddition(n.pos);
        res = res.getDivision(neighbors.size());
        res = res.getSubtraction(mover.pos);
        return res.getNormalized().getMult(COHESION_FORCE);
    }

    private Point2D getAlignmentForce(ArrayList<? extends Mover> neighbors) {
        if(neighbors.isEmpty())
            return Point2D.ORIGIN;
        Point2D res = Point2D.ORIGIN;
        for(Mover n : neighbors)
            res = res.getAddition(n.velocity);
        res = res.getDivision(neighbors.size());
        return res.getNormalized().getMult(ALIGNMENT_FORCE);
    }

    private Point2D queue(Point2D steering, ArrayList<Mover> neighbors) {
        
        if(neighbors.isEmpty())
            return Point2D.ORIGIN;
        
        boolean neighborAhead = false;
        Point2D ahead = mover.pos.getAddition(mover.velocity.getNormalized().getMult(NEIGHBOR_AHEAD_DIST));
        for(Mover n : neighbors)
            if(n.pos.getDistance(ahead)<NEIGHBOR_AHEAD_RAD)
                neighborAhead = true;
        
        Point2D brake = Point2D.ORIGIN;
        if(neighborAhead) {
//            brake = steering.getNegation().getMult(1);
//            brake = brake.getAddition(velocity.getNegation().getNormalized());
//            brake = brake.getAddition(separate(neighbors).getMult(1));
            mover.velocity = Point2D.ORIGIN;
            return Point2D.ORIGIN;
        }
        
        return brake;
    }
    
    private void modifySteeringToAvoid(ArrayList<? extends Mover> holdingMovers) {
        if(mover.velocity.equals(Point2D.ORIGIN))
            return;
        
//        double futurVelocityAngle = agent.velocity.getAddition(steering.getScaled(agent.velocity.getLength())).getAngle();
        
        Segment2D anticipation = new Segment2D(mover.pos, mover.pos.getTranslation(steering.getAngle(), MAX_ANTICIPATION));
        
        Point2D intersection = null;
        Mover obstacle = null;
        for(Mover m : holdingMovers){
            Point2D i = anticipation.getIntersectionsWithCircle(m.pos, mover.getSpacing(m)).get(0);
            if(i != null &&
                    (intersection == null || i.getDistance(mover.pos) < intersection.getDistance(mover.pos))){
                intersection = i;
                obstacle = m;
            }
        }
        
        if(obstacle == null)
            return;
        // if we are too close, we do not try to avoid and let the constraint manager manage the collision
        if(obstacle.getDistance(mover) < obstacle.getSpacing(mover)*1.1)
            return;
        
        double hypotenuse = mover.pos.getDistance(obstacle.pos);
        double opposé = mover.getSpacing(obstacle);
        
        double adjacent = Math.sqrt(hypotenuse*hypotenuse-opposé*opposé);
        double avoidanceAngle = Math.atan(opposé/adjacent)*1.1;

        Point2D toObstacle = obstacle.pos.getSubtraction(mover.pos).getNormalized();
        
        if(Angle.getOrientedDifference(steering.getAngle(), toObstacle.getAngle()) < 0){
            steering = toObstacle.getRotation(avoidanceAngle);
        }else{
            steering = toObstacle.getRotation(-avoidanceAngle);
        }
    }
    
    
}
