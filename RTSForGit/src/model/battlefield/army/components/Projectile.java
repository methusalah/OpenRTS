/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.army.components;

import model.builders.ProjectileBuilder;
import geometry.Point2D;
import geometry3D.Point3D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventListener;
import math.Angle;
import math.MyRandom;
import model.battlefield.actors.ProjectileActor;
import model.battlefield.army.effects.LauncherEffect;
import model.builders.ActorBuilder;
import model.builders.MoverBuilder;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class Projectile extends Movable {
    public enum PrecisionType{Center, InRadius, Other}
    
    private final PrecisionType precisionType;
    private final double precision;
    private final ProjectileActor actor;
    private final Unit target;
    public final String label = "label"+this.toString();
    
    public Point3D targetPoint = null;
    public boolean arrived = false;
    ArrayList<ActionListener> listeners = new ArrayList<>();

    public Projectile(double radius,
            double separationRadius,
            double speed,
            double mass,
            Point3D pos,
            MoverBuilder moverBuilder,
            PrecisionType precisionType,
            double precision,
            ActorBuilder actorBuilder,
            Unit target,
            Point3D targetPoint) {
        super(radius, separationRadius, speed, mass, pos, moverBuilder);
        this.precisionType = precisionType;
        this.precision = precision;
        this.target = target;
        this.targetPoint = targetPoint;
        actor = (ProjectileActor)actorBuilder.build(this);
        updateTargetPoint();
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
            actor.destroy();
            notifyListeners();
        }
        lastDist = dist;
    }
    
    public void updateTargetPoint(){
        if(targetPoint == null)
            switch (precisionType) {
                case Center : targetPoint = target.getPos(); break;
                case InRadius : targetPoint = getOffset(target.getPos(), target.radius); break;
                case Other : targetPoint = getOffset(target.getPos(), precision); break;
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
    
    public void addListener(ActionListener l){
        listeners.add(l);
    }
    
    public void notifyListeners(){
        for(ActionListener l : listeners)
            l.actionPerformed(new ActionEvent(this, 0, "arrived"));
    }
}
