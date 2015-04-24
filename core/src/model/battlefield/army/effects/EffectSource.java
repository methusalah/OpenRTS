package model.battlefield.army.effects;


import geometry.geom3d.Point3D;
import model.battlefield.army.components.Unit;

/**
 *
 */
public interface EffectSource {
    
    public boolean isStillActiveSource();
    public Point3D getPos();
    public Point3D getDirection();
    public Unit getUnit();
    public double getYaw();
    
}
