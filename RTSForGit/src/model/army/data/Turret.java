/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data;

import math.Angle;

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
    
    public double yaw = 0;
    double targetYaw = 0;
    
    boolean idle = true;
    
    public boolean hasMoved;
    
    public Turret(){
    }
    
    public void update(double elapsedTime, boolean holderMove){
        double localSpeed = speed;
        if(idle){
            localSpeed = idleSpeed;
            switch (onIdle){
                case RESET : setYaw(0); break;
                case HOLD : break;
                case RESET_ON_MOVE : 
                    if(holderMove)
                        targetYaw = 0;
                    break;
                case SPIN : targetYaw = yaw+Angle.RIGHT; break;
            }
        }

        if(targetYaw != yaw){
            double diff = Angle.getOrientedDifference(yaw, targetYaw);
            if(diff > 0)
                yaw += Math.min(diff, localSpeed*elapsedTime);
            else
                yaw += Math.max(diff, -localSpeed*elapsedTime);
        }
        idle = true;
    }
    
    public void setYaw(double yaw) {
        idle = false;
        targetYaw = yaw;
    }
    
//    public void idle(){
//        idle = true;
//        if(onIdle.equals(ONIDLE_RESET))
//            targetYaw = 0;
//    }
}
