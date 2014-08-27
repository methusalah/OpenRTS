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
    String precisionType;
    double precision;
    
    public LauncherEffect effect;
    
    public Point3D targetPoint = null;
    Unit target;
    public Unit source;
    
    public String label = "label"+this.toString();
    
    ProjectileActor actor;

    
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

        updateTargetPoint();
        mover.sm.seek(targetPoint);
        
        mover.updatePosition(elapsedTime);
        
        if(mover.hasMoved)
            actor.onMove(true);
        
        testArrival();
    }
    
    private double lastDist = Double.MAX_VALUE;
    private void testArrival(){
        double dist = mover.pos.getDistance(targetPoint);
        double tolerance;
        if(targetPoint.equals(target.getPos()))
            tolerance = target.radius;
        else
            tolerance = 0.1;
            
        if(dist < tolerance || (dist < 1 && dist > lastDist)){
            arrived = true;
            actor.onMove(false);
            actor.onDestroyedEvent();
            actor.destroyAfterActing();
            effect.notifyArrival();
        }
        lastDist = dist;
    }
    
    protected void updateTargetPoint(){
        if(targetPoint == null)
            switch (precisionType) {
                case ProjectileBuilder.PRECISION_CENTER : targetPoint = target.getPos(); break;
                case ProjectileBuilder.PRECISION_IN_RADIUS : targetPoint = getOffset(target.getPos(), target.radius); break;
                case ProjectileBuilder.PRECISION_OTHER : targetPoint = getOffset(target.getPos(), precision); break;
                default : throw new RuntimeException("unknown precision type "+precisionType);
            }
        else if(target != null && precisionType.equals(ProjectileBuilder.PRECISION_CENTER))
            targetPoint = target.getPos();
    }
    
    public Point3D getOffset(Point3D pos, double offset){
        Point2D pos2D = pos.get2D();
        double angle = MyRandom.next()*Angle.FLAT*2;
        double distance = MyRandom.next()*offset;
        pos2D = pos2D.getTranslation(angle, distance);
        return pos2D.get3D(pos.z);
    }

    @Override
    public Point3D getPos(){
        return mover.pos;
    }
}
