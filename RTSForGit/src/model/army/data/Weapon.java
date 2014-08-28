/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data;

import geometry.Point2D;
import geometry3D.Point3D;
import java.util.ArrayList;
import math.Angle;
import math.Precision;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class Weapon {
    // final
    String UIName;
    public double range;
    double scanRange;
    double period;
    EffectBuilder effectBuilder;
    public String sourceBone;
    public String directionBone;
    boolean allowMovement = false;
    

    final Unit holder;
    protected Turret turret;
    Actor actor;

    // variables
    Point3D pivot;
    Point3D source;
    Point3D vec;
    private Unit target;
    public double lastStrikeTime = 0;
    boolean attacking = false;
    
    protected ArrayList<Unit> onScan = new ArrayList<>();
    protected ArrayList<Unit> atRange = new ArrayList<>();
    
    public Weapon(Unit holder){
        this.holder = holder;
    }
    
    public void update(ArrayList<Unit> enemiesNearby){
        if(sourceBone != null && holder.actor.hasBone()){
            pivot = holder.actor.getBoneCoord(turret.boneName);
            source = holder.actor.getBoneCoord(sourceBone);
            vec = holder.actor.getBoneCoord(directionBone).getSubtraction(source).getNormalized();
        }else{
            pivot = holder.getPos();
            vec = Point2D.ORIGIN.getTranslation(holder.getOrientation(), 1).getNormalized().get3D(0);
        }

        attacking = false;
        onScan.clear();
        atRange.clear();
        for(Unit u : enemiesNearby){
            if(isAtRange(u))
                atRange.add(u);
            if(isAtScanRange(u))
                onScan.add(u);
        }
        chooseTarget();
    }
    
    private void chooseTarget(){
        target = null;
        // search best target at range
        for(Unit u : atRange)
            if(target == null)
                target = u;
            else {
                double healthDiff = u.getHealthRate()-target.getHealthRate();
                if(healthDiff < 0 ||
                        healthDiff < Precision.APPROX && holder.getDistance(u) < holder.getDistance(target))
                    target = u;
            }
        // if no target found, search best target on scan
        if(target == null)
            for(Unit u : onScan)
                target = target==null? u : holder.getNearest(u, target);
    }
    
    public void attack(Unit specificTarget){
        if(!isAtRange(specificTarget))
            throw new RuntimeException("specific target not in range");
        target = specificTarget;
        attack();
    }
    
    public void attack(){
        attacking = true;
        if(target == null)
            throw new RuntimeException("no target");
        
        setDesiredYaw();
        
        if(lastStrikeTime+1000*period < System.currentTimeMillis()){
            if(Angle.getSmallestDifference(getTargetAngle(), getAngle()) < Angle.toRadians(5)){
                if(actor != null)
                    actor.onShootEvent();
                target.ai.registerAsAttacker(holder);
                Effect e = effectBuilder.build(holder, target, null);
                e.setSourcePoint(source, vec);
                e.launch();

                lastStrikeTime = System.currentTimeMillis();
            }
        }
    }
    
    private void setDesiredYaw(){
        double desiredYaw = getTargetAngle();
        if(turret != null)
            turret.setYaw(desiredYaw);
        else
            holder.setYaw(desiredYaw);
    }
    
    private double getTargetAngle(){
        return target.getPos2D().getSubtraction(pivot.get2D()).getAngle();
    }
    
    private double getAngle(){
        return vec.get2D().getAngle();
    }
    
    
    public boolean hasTargetAtRange(Unit specificTarget){
        return isAtRange(specificTarget);
    }
    
    private boolean isAtRange(Unit u){
        return u.getBoundsDistance(holder) <= range;
    }
    
    private boolean isAtScanRange(Unit u){
        return u.getBoundsDistance(holder) <= scanRange;
    }
    
    public boolean isAttacking(){
        return attacking;
    }
    
    public Unit getTarget(){
        return target;
    }
    
    public boolean acquiring(){
        return !atRange.isEmpty();
    }
    
    public boolean scanning(){
        return !onScan.isEmpty();
    }
}
