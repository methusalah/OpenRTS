/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.lighting;

import geometry3D.Point3D;
import java.awt.Color;
import math.Angle;

/**
 *
 * @author Benoît
 */
public class DirectionalLighting extends Lighting{
    public Point3D direction;

    // yaw is compass, pitch is daytime
    public double yaw = 0;
    public double pitch = 0;
    
    public DirectionalLighting(Color color) {
        super(color);
    }

    public DirectionalLighting(Color color, double yaw, double pitch, double intensity) {
        super(color);
        this.yaw = yaw;
        this.pitch = pitch;
        this.intensity = intensity;
    }

    public void changePitch(double val){
        pitch = Angle.normalize(pitch+val);
    }
    public void changeYaw(double val){
        yaw = Angle.normalize(yaw+val);
    }
    
    
}
