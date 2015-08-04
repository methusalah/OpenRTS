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
    public final double maxSpeed;
    public final double acceleration;
    public final double maxRotationSpeed;
    public final double rotationAcceleration;
    public final double mass;
    public final Mover mover;
    
    public double speed = 0;
    public double rotationSpeed = 0;

    public Hiker(double radius,
    		double maxSpeed,
    		double acceleration,
    		double maxRotationSpeed,
    		double rotationAcceleration,
    		double mass, Point3D pos,
    		double yaw,
    		MoverBuilder moverBuilder) {
        super(pos, yaw, radius);
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration;
        this.maxRotationSpeed = maxRotationSpeed;
        this.rotationAcceleration = rotationAcceleration;
        this.mass = mass;
        this.mover = moverBuilder.build(this);
        mover.desiredYaw = yaw;
    }
    
    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getMaxRotationSpeed() {
        return AngleUtil.toRadians(720);
    }

    public double getMass() {
        return mass;
    }
    
    public boolean hasMoved(Point3D lastPos, double lastYaw){
        return lastYaw != yaw || !lastPos.equals(pos);
    }
    
    public void incSpeed(double elapsedTime){
		speed += acceleration*elapsedTime;
		speed = Math.min(maxSpeed, speed);
    }

    public void decSpeed(double elapsedTime){
		speed -= acceleration*elapsedTime;
		speed = Math.max(0, speed);
    }

    public void incRotationSpeed(double elapsedTime){
    	rotationSpeed += rotationAcceleration*elapsedTime;
    	rotationSpeed = Math.min(maxRotationSpeed, rotationSpeed);
    }

    public void decRotationSpeed(double elapsedTime){
    	rotationSpeed -= rotationAcceleration*elapsedTime;
    	rotationSpeed = Math.max(0, rotationSpeed);
    }
    
    
    
}
