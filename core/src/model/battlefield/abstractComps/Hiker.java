package model.battlefield.abstractComps;

import model.battlefield.army.components.Mover;
import model.battlefield.army.motion.Motion;
import model.builders.entity.MoverBuilder;
import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import geometry.math.AngleUtil;
import geometry.math.RandomUtil;

public abstract class Hiker extends FieldComp {

	public final double maxSpeed;
	public final double acceleration;
	public final double deceleration;
	public final Mover mover;
	public final double priority = RandomUtil.next();  
	
	protected double speed = 0;
	private boolean StrongAccelerationAsked = false;
	private boolean StrongDecelerationAsked = false;
	private double motionOrientation;

	public Hiker(Point3D pos,
			double yaw,
			double radius,
    		double maxSpeed,
    		double acceleration,
    		double deceleration,
    		MoverBuilder moverBuilder) {
		super(pos, yaw, radius);
		this.maxSpeed = maxSpeed;
		this.acceleration = acceleration;
		this.deceleration = deceleration;
		this.mover = moverBuilder.build(this);
	}

	public boolean hasMoved(Point3D lastPos, double lastOrientation) {
	    return lastOrientation != getOrientation() || !lastPos.equals(pos);
	}

	public boolean hasMoved(Point3D lastPos, Point3D lastDirection) {
	    return !lastDirection.equals(getDirection()) || !lastPos.equals(pos);
	}
	
	public boolean isStopped(){
		return speed == 0;
	}

	public void decelerateStrongly() {
		StrongDecelerationAsked = true;
	}

	public void accelerateStrongly() {
		StrongAccelerationAsked = true;
	}

	protected void incSpeed(double elapsedTime) {
		if(speed < maxSpeed){
			speed += StrongAccelerationAsked?
					acceleration*elapsedTime*10:
					acceleration*elapsedTime;
			speed = Math.min(maxSpeed, speed);
		}
	}

	protected void decSpeed(double elapsedTime) {
		if(speed > 0){
			speed -= StrongDecelerationAsked?
					deceleration*elapsedTime*10:
					deceleration*elapsedTime;
			speed = Math.max(0, speed);
		}
	}
	
	public abstract void move(Motion motion);

	public abstract Motion getNearestPossibleMotion(Motion desiredMotion, Point3D destination, double elapsedTime);

	protected abstract void adaptSpeedTo(Motion desiredMotion, Point3D destination, double elapsedTime);
	
	protected abstract boolean willOverstepDestination(Point3D destination);

	protected abstract boolean willMissDestination(Point3D destination);
}