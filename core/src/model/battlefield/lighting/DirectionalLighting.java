package model.battlefield.lighting;

import geometry.geom3d.Point3D;
import geometry.math.AngleUtil;

import java.awt.Color;


/**
 *
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
        pitch = AngleUtil.normalize(pitch+val);
    }
    public void changeYaw(double val){
        yaw = AngleUtil.normalize(yaw+val);
    }
    
    
}
