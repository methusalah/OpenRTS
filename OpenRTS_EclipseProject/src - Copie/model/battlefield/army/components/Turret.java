package model.battlefield.army.components;

import geometry.math.Angle;

/**
 * Mobile element of the unit, the turret gives the view all needed information to
 * rotate a bone of a model
 * 
 * Turrets are used attached to weapons and weapons ask for their rotation. But theoretically
 * a turret can be attached without weapon and have its own rotating behavior (radar, head...)
 *   
 * It is defined by XML and is only instanciated by associate builder.
 * 
 */
public class Turret {
    public enum OnIdleBehave {RESET_ON_MOVE, RESET, SPIN, HOLD};
    
    // final
    final double speed;
    final double idleSpeed;
    final OnIdleBehave onIdle;
    public final String boneName;
    final Unit holder;
    
    public double yaw = 0;
    double desiredYaw = 0;
    
    boolean idle = true;
    
    public boolean hasMoved;

    public Turret(double speed, double idleSpeed, OnIdleBehave onIdle, String boneName, Unit holder) {
        this.speed = speed;
        this.idleSpeed = idleSpeed;
        this.onIdle = onIdle;
        this.boneName = boneName;
        this.holder = holder;
    }
    
    public void update(double elapsedTime, boolean holderMove){
        double localSpeed = speed;
        if(idle){
            localSpeed = idleSpeed;
            switch (onIdle){
                case RESET : reset(); break;
                case HOLD : break;
                case RESET_ON_MOVE :
                    if(holderMove)
                        reset();
                    break;
                case SPIN : desiredYaw = yaw+Angle.RIGHT; break;
            }
        }

        if(!Angle.areSimilar(desiredYaw,yaw)){
            double diff = Angle.getOrientedDifference(yaw, desiredYaw);
            if(diff > 0)
                yaw += Math.min(diff, localSpeed*elapsedTime);
            else
                yaw -= Math.min(-diff, localSpeed*elapsedTime);
        } else
            yaw = desiredYaw;
        idle = true;
    }
    
    public void setYaw(double yaw) {
        idle = false;
        desiredYaw = yaw-holder.getYaw();
    }
    
    private void reset(){
        setYaw(holder.getYaw());
    }
}
