/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.army.components;

import model.battlefield.army.motion.pathfinding.FlowField;
import geometry.BoundingCircle;
import geometry.Point2D;
import geometry3D.Point3D;
import java.util.ArrayList;
import math.Angle;
import model.battlefield.map.Map;
import model.battlefield.army.motion.CollisionManager;
import model.battlefield.army.motion.SteeringMachine;

/**
 *
 * @author Benoît
 */
public class Mover {
    public enum Heightmap {SKY, AIR, GROUND};
    public enum PathfindingMode {FLY, WALK};

    // final 
    public final Heightmap heightmap;
    public final PathfindingMode pathfindingMode;

    public final Movable movable;
    final Map map;
    SteeringMachine sm;
    CollisionManager cm;

    // variables
    public Point3D pos = Point3D.ORIGIN;
    public Point3D velocity = Point3D.ORIGIN;
    
    public double yaw = 0;
    public double desiredYaw = 0;
    
    public boolean hasMoved = false;
    
    public ArrayList<Mover> toAvoid = new ArrayList<>();
    public ArrayList<Mover> toFlockWith = new ArrayList<>();
    public ArrayList<Mover> toLetPass = new ArrayList<>();
    
    
    public FlowField flowfield;
    private boolean hasDestination;
    public boolean hasFoundPost;
    public boolean holdPosition = false;
    public boolean tryHold = false;

    public Mover(Heightmap heightmap, PathfindingMode pathfindingMode, Movable movable, Map map, Point3D pos, double yaw) {
        this.heightmap = heightmap;
        this.pathfindingMode = pathfindingMode;
        this.movable = movable;
        this.map = map;
        this.pos = pos;
        this.yaw = yaw;
        cm = new CollisionManager(this, map);
        sm = new SteeringMachine(this);
        updateElevation();
    }
    public Mover(Mover o, Movable movable) {
        this.heightmap = o.heightmap;
        this.pathfindingMode = o.pathfindingMode;
        this.movable = movable;
        this.map = o.map;
        this.pos = o.pos;
        this.yaw = o.yaw;
        cm = new CollisionManager(this, map);
        sm = new SteeringMachine(this);
        updateElevation();
    }
    
    public void updatePosition(double elapsedTime) {
        double lastYaw = yaw;
        Point3D lastPos = new Point3D(pos);
        
        if(!holdPosition){
            Point3D steering = sm.getSteeringAndReset(elapsedTime);
            cm.applySteering(steering, elapsedTime, toAvoid);
        }
        head(elapsedTime);
        
        hasMoved = lastYaw != yaw || !lastPos.equals(pos);
        if(hasMoved)
            updateElevation();
        
        if(hasDestination)
            hasFoundPost = false;
        else {
            hasFoundPost = true;
            for(Mover m : toFlockWith)
                if(m.hasDestination){
                    hasFoundPost = false;
                }
        }
        if(!tryHold)
            holdPosition = false;
    }
    
    public void tryToHoldPositionSoftly(){
        tryHold = true;
        if(fly())
            holdPosition = true;
        else {
            ArrayList<Mover> all = new ArrayList<>();
            all.addAll(toAvoid);
            all.addAll(toFlockWith);
            all.addAll(toLetPass);
            for(Mover m : all)
                if(getBounds().collide(m.getBounds()))
                    return;
            for(Mover m : toFlockWith)
                if(m.tryHold && !m.holdPosition)
                    return;
            holdPosition = true;
        }
    }
    public void tryToHoldPositionHardly(){
        tryHold = true;
        if(fly())
            holdPosition = true;
        else {
            ArrayList<Mover> all = new ArrayList<>();
            all.addAll(toAvoid);
            all.addAll(toFlockWith);
            all.addAll(toLetPass);
            for(Mover m : all)
                if(m.holdPosition && getBounds().collide(m.getBounds()))
                    return;
            holdPosition = true;
        }
    }
    
    public double getSpacing(Mover o) {
        return movable.getRadius()+o.movable.getRadius();
    }
    
    public void setDestination(FlowField ff){
        flowfield = ff;
        hasDestination = true;
        hasFoundPost = false;
    }
    
    public void setDestinationReached(){
        hasDestination = false;
        for(Mover m : toFlockWith)
            if(getDistance(m) < getSpacing(m)+3)
                m.hasDestination = false;
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

    public BoundingCircle getBounds() {
        return new BoundingCircle(new Point2D(pos), movable.getRadius());
    }

    public void head(double elapsedTime) {
        if(!velocity.isOrigin())
            desiredYaw = velocity.get2D().getAngle();

        if(!Angle.areSimilar(desiredYaw, yaw)){
            double diff = Angle.getOrientedDifference(yaw, desiredYaw);
            if(diff > 0)
                yaw += Math.min(diff, movable.getRotSpeed()*elapsedTime);
            else
                yaw -= Math.min(-diff, movable.getRotSpeed()*elapsedTime);
        } else
            yaw = desiredYaw;
    }

    public Point3D getVectorTo(Mover o) {
        return o.pos.getSubtraction(pos);
    }
    
    // TODO ici le toFlockWith perd son sens quand il ne s'agit que de separation.
    public void separate(){
        sm.applySeparation(toLetPass);
    }
    
    public void flock(){
        sm.applySeparation(toFlockWith);
//        sm.applyCohesion(neighbors);
//        sm.applyAlignment(neighbors);
    }
    
    public void seek(Mover target){
        flock();
        separate();
        sm.seek(target);

        ArrayList<Mover> toAvoidExceptTarget = new ArrayList<>(toAvoid);
        toAvoidExceptTarget.remove(target);
        sm.avoidHoldingUnits(toAvoidExceptTarget);
    }

    public void seek(Point3D position){
        flock();
        separate();
        sm.seek(position);
        sm.avoidHoldingUnits(toAvoid);
    }
    
    public void followPath() {
        flock();
        separate();
        sm.proceedToDestination();
        sm.avoidHoldingUnits(toAvoid);
    }
    

    public void followPath(Mover target) {
        flock();
        separate();
        sm.proceedToDestination();

        ArrayList<Mover> toAvoidExceptTarget = new ArrayList<>(toAvoid);
        toAvoidExceptTarget.remove(target);
        sm.avoidHoldingUnits(toAvoidExceptTarget);
    }
    
    private void updateElevation(){
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
    
    public void setPosition(Point2D p){
        velocity = Point3D.ORIGIN;
        pos = p.get3D(0);
        updateElevation();
    }
}
