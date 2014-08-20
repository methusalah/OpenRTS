/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data;

import model.army.motion.pathfinding.Path;
import model.army.motion.pathfinding.FlowField;
import geometry.AlignedBoundingBox;
import geometry.BoundingCircle;
import geometry.Point2D;
import geometry3D.Point3D;
import java.util.ArrayList;
import math.Angle;
import model.map.Map;
import model.army.motion.CollisionManager;
import model.army.motion.SteeringMachine;

/**
 *
 * @author Benoît
 */
public class Mover {
    public enum Heightmap {SKY, AIR, GROUND};
    public enum PathfindingMode {FLY, WALK};

    // final 
    Heightmap heightmap;
    PathfindingMode pathfindingMode;

    public final Movable movable;
    final Map map;
    SteeringMachine sm;
    CollisionManager cm;

    // variables
    public Point3D pos = Point3D.ORIGIN;
    public Point3D velocity = Point3D.ORIGIN;
    
    public double orientation = 0;
    public double desiredOrientation = 0;
    
    public boolean hasMoved = false;
    
    public ArrayList<Mover> toAvoid = new ArrayList<>();
    public ArrayList<Mover> toFlockWith = new ArrayList<>();
    
    
    public FlowField flowfield;
    private boolean hasDestination;

    public Mover(Map map, Movable movable, Point3D position){
        this.map = map;
        this.movable = movable;
        pos = position;
        cm = new CollisionManager(this, map);
        sm = new SteeringMachine(this);
    }
    
    public void updatePosition(double elapsedTime) {
        double savedOrientation = orientation;
        Point3D savedPos = new Point3D(pos);
        
        Point3D steering = sm.getSteeringAndReset(elapsedTime);
        cm.applySteering(steering, elapsedTime, toAvoid);
        head(elapsedTime);
        
        hasMoved = savedOrientation != orientation || !savedPos.equals(pos);
        
        if(hasMoved)
            updateElevation();
    }
    
    public double getSpacing(Mover o) {
        return movable.getRadius()+o.movable.getRadius();
    }
    
    public void setDestination(FlowField ff){
        flowfield = ff;
        hasDestination = true;
    }
    
    public void setDestinationReached(){
        hasDestination = false;
    }
    
    public boolean hasDestination(){
        return hasDestination;
    }
    
    public Point2D getDestination(){
        if(flowfield != null)
            return flowfield.destination;
        return null;
    }
    
    public double getDistance(Mover o) {
        return pos.getDistance(o.pos);
    }

    public BoundingCircle getBoundingCircle() {
        return new BoundingCircle(new Point2D(pos), movable.getRadius());
    }

    public boolean collide(ArrayList<AlignedBoundingBox> walls){
        BoundingCircle agentBounds = getBoundingCircle();
        for(AlignedBoundingBox wall : walls)
            if(agentBounds.collide(wall))
                return true;
        return false;
    }
    
    public boolean collide(Mover other){
        return getDistance(other) <= getSpacing(other);
    }
    
    public void head(double elapsedTime) {
        if(!velocity.isOrigin())
            desiredOrientation = velocity.get2D().getAngle();

        double diff = Angle.getOrientedDifference(orientation, desiredOrientation);
        if(diff > 0)
            orientation += Math.min(diff, movable.getRotSpeed()*elapsedTime);
        else
            orientation += Math.max(diff, -movable.getRotSpeed()*elapsedTime);
    }

    public Point3D getVectorTo(Mover o) {
        return o.pos.getSubtraction(pos);
    }
    
    public void separate(){
        sm.applySeparation(toFlockWith);
    }
    
    public void flock(){
        sm.applySeparation(toFlockWith);
//        sm.applyCohesion(neighbors);
//        sm.applyAlignment(neighbors);
    }
    
    public void seek(Mover other){
        flock();
        sm.seek(other);

        ArrayList<Mover> toAvoidExceptTarget = new ArrayList<>(toAvoid);
        toAvoidExceptTarget.remove(other);
        sm.avoidHoldingUnits(toAvoidExceptTarget);
    }

    public void seek(Point3D position){
        flock();
        sm.seek(position);
        sm.avoidHoldingUnits(toAvoid);
    } 
    
    
    public void followPath() {
        flock();
        sm.proceedToDestination();
        sm.avoidHoldingUnits(toAvoid);
    }
    
    void updateElevation(){
        if(heightmap == Heightmap.GROUND)
            pos = new Point3D(pos.x, pos.y, map.getGroundAltitude(pos.get2D())+0.25);
        else if(heightmap == Heightmap.SKY)
            pos = new Point3D(pos.x, pos.y, map.getTile(pos.get2D()).level+3);
            
    }
    
    public boolean fly(){
        return pathfindingMode == PathfindingMode.FLY;
    }
    
    public double getSpeed(){
        return movable.getSpeed();
    }
    
    public Point2D getPos2D(){
        return new Point2D(pos);
    }
}
