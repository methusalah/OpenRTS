package model.battlefield.abstractComps;

import geometry.geom3d.Point3D;
import geometry.math.AngleUtil;
import model.battlefield.army.components.Mover;
import model.builders.entity.MoverBuilder;

/**
 * A basic abstract entity, containing all needed attributes and methods to set a moving object on the battlefield
 * 
 */
public abstract class Hiker extends FieldComp{
    public final double speed;
    public final double mass;
    public final Mover mover;

    public Hiker(double radius, double speed, double mass, Point3D pos, double yaw, MoverBuilder moverBuilder) {
        super(pos, yaw, radius);
        this.speed = speed;
        this.mass = mass;
        this.mover = moverBuilder.build(this);
        mover.desiredYaw = yaw;
    }
    public Hiker(double radius, double speed, double mass, Point3D pos, double yaw, Mover mover) {
        super(pos, yaw, radius);
        this.speed = speed;
        this.mass = mass;
        this.mover = new Mover(mover, this);
        mover.desiredYaw = yaw;
    }
    
    
    public double getSpeed() {
        return speed;
    }

    public double getRotSpeed() {
        return AngleUtil.toRadians(720);
    }

    public double getStationaryRotSpeed() {
        return AngleUtil.toRadians(360);
    }

    public double getMass() {
        return mass;
    }
    
    public boolean hasMoved(Point3D lastPos, double lastYaw){
        return lastYaw != yaw || !lastPos.equals(pos);
    }
    
}
