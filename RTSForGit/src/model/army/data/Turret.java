/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data;

import math.Angle;
import math.Precision;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class Turret {
    public enum OnIdleBehave {RESET_ON_MOVE, RESET, SPIN, HOLD};
    
    // final
    double speed;
    double idleSpeed;
    OnIdleBehave onIdle;
    public String boneName;
    
    final Unit holder;
    
    public double yaw = 0;
    double desiredYaw = 0;
    
    boolean idle = true;
    
    public boolean hasMoved;
    
    public Turret(Unit holder){
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
        desiredYaw = yaw-holder.getOrientation();
    }
    
    private void reset(){
        setYaw(holder.getOrientation());
    }
}
