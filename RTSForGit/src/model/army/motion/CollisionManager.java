/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.motion;

import model.army.data.Mover;
import geometry.AlignedBoundingBox;
import geometry.BoundingCircle;
import geometry.Point2D;
import geometry3D.Point3D;
import java.util.ArrayList;
import math.Angle;
import model.map.Map;
import model.map.Tile;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class CollisionManager {
    private enum CollisionType {NONE, MAP, HOLDING, BOTH};
    
    private static double BRAKING_RATIO = 0.9;
    private static double MAX_ADAPT_TOLERANCE = Angle.toRadians(180);
    private static double ADAPT_TOLERANCE = Angle.toRadians(100);
    private static double ADAPT_TOLERANCE_INCRASE = Angle.toRadians(20);
    private static double ADAPTATION_STEP = Angle.toRadians(1);
    
    Mover agent;
    Map map;
    ArrayList<? extends Mover> holdingNeighbours;
    ArrayList<AlignedBoundingBox> walls;
    
    double tolerance = ADAPT_TOLERANCE;
    double tryDuration = 0;
    double tryStartTime;
    boolean tryClockwise = true;
    
    public CollisionManager(Mover m, Map map){
        this.agent = m;
        this.map = map;
    }
    
    public void applySteering(Point3D steering, double elapsedTime, ArrayList<? extends Mover> holdingNeighbours) {
        double traveledDistance = agent.getSpeed()*elapsedTime;
        if(traveledDistance < 0.001)
            LogUtil.logger.info("very short traveled distance...");
        
        
        this.holdingNeighbours = holdingNeighbours;
        walls = getSurroundingWalls();
        
        if(steering.equals(Point3D.ORIGIN)){
            brake(elapsedTime);
        } else {
            Point3D scaledSteering = steering;//.getScaled(traveledDistance);
            
            if(agent.fly()){
                agent.velocity = agent.velocity.getAddition(scaledSteering).getTruncation(traveledDistance);
            } else {
                scaledSteering = adaptSteering(scaledSteering);
                if(scaledSteering.equals(Point3D.ORIGIN)){
                    manageStuck();
                    agent.velocity = Point3D.ORIGIN;
                } else {
                    Point3D newVelocity = agent.velocity.getAddition(scaledSteering).getTruncation(traveledDistance);
                    if(collideMap(getMovedBound(newVelocity.get2D())))
                        agent.velocity = scaledSteering;
                    else
                        agent.velocity = newVelocity;
                }
            }
            agent.pos = agent.pos.getAddition(agent.velocity);
            
        }
    }
    
    public void brake(double elapsedTime) {
        try {
            Point3D brakeForce = agent.velocity.getNegation().getMult(BRAKING_RATIO);
            brakeForce.getTruncation(elapsedTime);
            agent.velocity = agent.velocity.getAddition(brakeForce);
        } catch(RuntimeException e){
            LogUtil.logger.info("erreur dans le brake : "+agent.velocity+" ; elapsed time : "+elapsedTime);
        }
        
        if(agent.velocity.getNorm()<0.01)
            agent.velocity = Point3D.ORIGIN;
        agent.pos.getAddition(agent.velocity);
    }
    
    private Point3D adaptSteering(Point3D steering){
        CollisionType ct = getCollision(steering.get2D());
        if(ct == CollisionType.NONE){
            tolerance = Math.max(--tolerance, ADAPT_TOLERANCE);
            return steering;
        } else {
            if(tolerance == ADAPT_TOLERANCE || ct == CollisionType.MAP){
                return getValidSteeringAndChooseDirection(steering);
            } else {
                return getValidSteeringWithChosenDirection(steering);
            }
        }
    }
    
    private void manageStuck(){
        tryClockwise = !tryClockwise;
        tolerance += ADAPT_TOLERANCE_INCRASE;
//        LogUtil.logger.info("stuck; try clockwise = "+tryClockwise+"; tolerance = "+tolerance);
        if(tolerance > MAX_ADAPT_TOLERANCE)
            giveUp();
    }
    
    private Point3D getValidSteeringAndChooseDirection(Point3D steering){
        int count = 0;
        Point2D clockwiseTry = new Point2D(steering);
        Point2D counterclockwiseTry = new Point2D(steering);
        while(true){
            clockwiseTry = clockwiseTry.getRotation(-ADAPTATION_STEP);
            if(getCollision(clockwiseTry) == CollisionType.NONE){
                tryClockwise = true;
                return clockwiseTry.get3D(steering.z);
            }

            counterclockwiseTry = counterclockwiseTry.getRotation(ADAPTATION_STEP);
            if(getCollision(counterclockwiseTry) == CollisionType.NONE){
                tryClockwise = false;
                return counterclockwiseTry.get3D(steering.z);
            }

            if(count++ > tolerance/ADAPTATION_STEP){
                return Point3D.ORIGIN;
            }
        }
    }
    
    private Point3D getValidSteeringWithChosenDirection(Point3D steering){
        int count = 0;
        Point2D triedSteering = new Point2D(steering);
        while(true){
            if(tryClockwise)
                triedSteering = triedSteering.getRotation(-ADAPTATION_STEP);
            else
                triedSteering = triedSteering.getRotation(ADAPTATION_STEP);

            if(getCollision(triedSteering) == CollisionType.NONE)
                return triedSteering.get3D(steering.z);

            if(count++ > tolerance/ADAPTATION_STEP) {
                return Point3D.ORIGIN;
            }
        }
    }
    
    private CollisionType getCollision(Point2D steering){
        CollisionType res = CollisionType.NONE;
        if(collideMap(getMovedBound(steering)))
            res = CollisionType.MAP;
        if(collideHolding(steering) != null)
            if(res == CollisionType.MAP)
                res = CollisionType.BOTH;
            else    
                res = CollisionType.HOLDING;
        
        return res;
    }
    
    private ArrayList<AlignedBoundingBox> getSurroundingWalls(){
        ArrayList<AlignedBoundingBox> res = new ArrayList<>();
        for(int x = -2; x<3; x++)
            for(int y = -2; y<3; y++){
                Point2D tilePos = agent.getPos2D().getAddition(x, y);
                if(!map.isInBounds(tilePos))
                    continue;
                Tile t = map.getTile(tilePos);
                if(t.isCliff())
                    res.add(t.getBoundingBox());
            }
        return res;
    }
    
    private boolean collideMap(BoundingCircle circle){
        if(!map.isInBounds(circle.center))
            return true;
        for(AlignedBoundingBox wall : walls)
            if(circle.collide(wall))
                return true;
        return false;
    }
    
    private BoundingCircle getMovedBound(Point2D velocity){
        return new BoundingCircle(agent.getPos2D().getAddition(velocity), agent.movable.getRadius());
    }
    
    private void giveUp(){
//        LogUtil.logger.info("stuck de chez stuck");
        tolerance = ADAPT_TOLERANCE;
        agent.setDestinationReached();
        agent.movable.setStuck();
    }
    
    
    private Mover collideHolding(Point2D velocity) {
        Mover nearest = null;
        for(Mover m : holdingNeighbours)
            if(m != agent && agent.getPos2D().getAddition(velocity).getDistance(m.getPos2D()) <= agent.getSpacing(m))
                if(nearest == null || nearest.getDistance(agent) > m.getDistance(agent))
                    nearest = m;
        return nearest;
    }
}
