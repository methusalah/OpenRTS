/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data;

import geometry.Point2D;
import geometry3D.Point3D;
import math.Angle;
import math.MyRandom;
import model.army.data.actors.ProjectileActor;
import model.army.data.effects.LauncherEffect;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class Projectile extends Movable {

    // final 
    public String modelPath;

    String precisionType;
    double precision;
    
    public LauncherEffect effect;
    
    public Point3D targetPoint = null;
    Unit target;
    public Unit source;
    
    public String label = "label"+this.toString();
    
    ProjectileActor actor;

    
    Point3D offset;
    
    public boolean arrived = false;
    
    public Projectile(LauncherEffect effect, Unit target, Point3D targetPoint){
        this.effect = effect;
        this.target = target;
        this.targetPoint = targetPoint;
        source = effect.source;
    }
    
    public void update(double elapsedTime){
        if(targetPoint == null)
            return;
        
        Point2D targetPos2D = new Point2D(targetPoint.x, targetPoint.y);
        mover.sm.seek(targetPos2D);
        
        mover.updatePosition(elapsedTime);
        
        testArrival();
    }
    
    public double getZ(){
        if(targetPoint == null)
            return source.getMover().z;
        double flightDist = source.getPos().getDistance(new Point2D(targetPoint.x, targetPoint.y));
        double zDiff = target.getMover().z-source.getMover().z;
        double flightDistDone = source.getPos().getDistance(mover.pos);
        double flightRate = flightDistDone/flightDist;
        
        return source.getMover().z+zDiff*flightRate;
    }
    
    public double getZAngle(){
        if(targetPoint == null)
            return 0;
        double flightDist = source.getPos().getDistance(new Point2D(targetPoint.x, targetPoint.y));
        double zDiff = source.getMover().z-target.getMover().z;
        return new Point2D(flightDist, zDiff).getAngle();
    }
    
    private double lastDist = 999;
    private void testArrival(){
        double dist = mover.pos.getDistance(new Point2D(targetPoint.x, targetPoint.y));
        if(dist < 0.05 || dist > lastDist){
            arrived = true;
            actor.interrupt();
            effect.notifyArrival();
        }
        lastDist = dist;
    }
    
    public void updateTargetPoint(){
        switch (precisionType) {
            case ProjectileBuilder.PRECISION_CENTER : targetPoint = target.getPos3D(); break;
            case ProjectileBuilder.PRECISION_IN_RADIUS : targetPoint = getOffset(target.getPos3D(), target.radius); break;
            case ProjectileBuilder.PRECISION_OTHER : targetPoint = getOffset(target.getPos3D(), precision); break;
            default : throw new RuntimeException("unknown precision type "+precisionType);
        }

    }
    
    public Point3D getOffset(Point3D pos, double offset){
        Point2D pos2D = new Point2D(pos.x, pos.y);
        double angle = MyRandom.next()*Angle.FLAT*2;
        double distance = MyRandom.next()*offset;
        pos2D = pos2D.getTranslation(angle, distance);
        return new Point3D(pos2D, pos.z, 1);
    }

    @Override
    public Point3D getPos3D(){
        return new Point3D(mover.pos, getZ(), 1);
    }
}
