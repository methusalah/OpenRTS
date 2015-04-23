package model.battlefield.army.components;

import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import geometry.math.Angle;
import geometry.math.MyRandom;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import model.battlefield.abstractComps.Hiker;
import model.battlefield.actors.ModelActor;
import model.battlefield.army.effects.EffectSource;
import model.battlefield.army.effects.EffectTarget;
import model.builders.MoverBuilder;
import model.builders.actors.ModelActorBuilder;

/**
 * Projectile is a special Hiker that flies to a target accordingly to a flight plan.
 * 
 * Flight plan remains to be implemented. For now a projectile goes as strait as possible
 * to the point, depending on his mass, speed, rotation speed...
 * 
 * 
 * It is defined by XML and is only instanciated by associate builder.
 * 
 * It is launched by a LauncherEffect, and notify it of its arrival.
 * 
 * @author Benoît
 */
public class Projectile extends Hiker {
    public enum PrecisionType{Center, InRadius, Other}
    
    private final PrecisionType precisionType;
    private final double precision;
    private final ModelActor actor;
    private final EffectTarget target;
    public final String label = "label"+this.toString();
    
    public Point3D targetPoint = null;
    public boolean arrived = false;
    ArrayList<ActionListener> listeners = new ArrayList<>();

    public Projectile(double radius,
            double speed,
            double mass,
            EffectSource source,
            MoverBuilder moverBuilder,
            PrecisionType precisionType,
            double precision,
            ModelActorBuilder actorBuilder,
            EffectTarget target,
            Point3D targetPoint) {
        super(radius, speed, mass, source.getPos(), source.getYaw(), moverBuilder);
        this.precisionType = precisionType;
        this.precision = precision;
        this.target = target;
        this.targetPoint = targetPoint;
        actor = actorBuilder.build(this);
        mover.velocity = source.getDirection();
        upDirection = null;
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
        double dist = pos.getDistance(targetPoint);
        double tolerance;
        if(targetPoint.equals(target.getPos()))
            tolerance = target.getRadius();
        else
            tolerance = 0.1;
            
        if(dist < tolerance || (dist < 1 && dist > lastDist)){
            arrived = true;
            actor.onMove(false);
            actor.onDestroyedEvent();
            actor.stopActing();
            notifyListeners();
        }
        lastDist = dist;
    }
    
    public void updateTargetPoint(){
        if(targetPoint == null)
            switch (precisionType) {
                case Center : targetPoint = target.getPos(); break;
                case InRadius : targetPoint = getOffset(target.getPos(), target.getRadius()); break;
                case Other : targetPoint = getOffset(target.getPos(), precision); break;
                default : throw new RuntimeException("unknown precision type "+precisionType);
            }
        else if(target != null && precisionType == PrecisionType.Center)
            targetPoint = target.getPos();
    }
    
    public Point3D getOffset(Point3D pos, double offset){
        Point2D pos2D = pos.get2D();
        double angle = MyRandom.next()*Angle.FLAT*2;
        double distance = MyRandom.next()*offset;
        pos2D = pos2D.getTranslation(angle, distance);
        return pos2D.get3D(pos.z);
    }

    public void addListener(ActionListener l){
        listeners.add(l);
    }
    
    public void notifyListeners(){
        for(ActionListener l : listeners)
            l.actionPerformed(new ActionEvent(this, 0, "arrived"));
    }
    
    public void removeFromBattlefield(){
        arrived = true;
        actor.stopActingAndChildren();
    }
}
