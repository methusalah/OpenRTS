package model.battlefield.army.components;

import java.util.logging.Logger;

import geometry.geom2d.Point2D;
import geometry.math.AngleUtil;

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
	private static final Logger logger = Logger.getLogger(Mover.class.getName());
    public enum OnIdleBehave {RESET_ON_MOVE, RESET, SPIN, HOLD};
    
    // final
    final double speed;
    final double idleSpeed;
    final OnIdleBehave onIdle;
    public final String boneName;
    public final String boneAxis;
    final Unit holder;
    
    public double yaw = 0;
    double desiredYaw = 0;
    
    boolean idle = true;
    
    public boolean hasMoved;

    public Turret(double speed, double idleSpeed, OnIdleBehave onIdle, String boneName, String boneAxis, Unit holder) {
        this.speed = speed;
        this.idleSpeed = idleSpeed;
        this.onIdle = onIdle;
        this.boneName = boneName;
        this.boneAxis = boneAxis;
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
                case SPIN : desiredYaw = yaw+AngleUtil.RIGHT; break;
            }
        }

        if(!AngleUtil.areSimilar(desiredYaw,yaw)){
            double diff = AngleUtil.getOrientedDifference(yaw, desiredYaw);
            if(diff > 0)
                yaw += Math.min(diff, localSpeed*elapsedTime);
            else
                yaw -= Math.min(-diff, localSpeed*elapsedTime);
        } else
            yaw = desiredYaw;
        idle = true;
    }
    
    
    // TODO DRY : repetition with Unit code
    public void head(Point2D target) {
        orient(getAngleTo(target));
    }
    
    public boolean heading(Point2D target, double toleranceInDegrees){
    	return AngleUtil.getSmallestDifference(getAngleTo(target), yaw+holder.getOrientation()) <= AngleUtil.toRadians(toleranceInDegrees);
    }
    
    private double getAngleTo(Point2D p){
    	return p.getSubtraction(holder.actor.getBoneCoord(boneName).get2D()).getAngle();
    }
    
    private void orient(double yaw){
        idle = false;
        desiredYaw = yaw-holder.getOrientation();
    }
    
    private void reset(){
        orient(holder.getOrientation());
    }
}
