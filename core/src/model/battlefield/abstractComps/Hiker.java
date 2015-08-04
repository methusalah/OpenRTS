package model.battlefield.abstractComps;

import java.util.logging.Logger;

import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import geometry.math.AngleUtil;
import model.battlefield.army.components.Mover;
import model.builders.entity.MoverBuilder;

/**
 * A basic abstract entity, containing all needed attributes and methods to set a moving object on the battlefield
 * 
 */
public abstract class Hiker extends FieldComp{
	private static final Logger logger = Logger.getLogger(Hiker.class.getName());
    public final double maxSpeed;
    public final double acceleration;
    public final double deceleration;
    public final double maxRotationSpeed;
    public final double rotationAcceleration;
    public final double mass;
    public final Mover mover;
    
    public double speed = 0;
    public double rotationSpeed = 0;
    private int actualRotationDirection = 0;

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
        this.deceleration = acceleration*10;
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
    	if(speed != maxSpeed){
			speed += acceleration*elapsedTime;
			speed = Math.min(maxSpeed, speed);
    	}
    }

    public void decSpeed(double elapsedTime){
    	if(speed != 0){
			speed -= deceleration*elapsedTime;
			speed = Math.max(0, speed);
    	}
    }

    public void incRotationSpeed(double elapsedTime){
    	if(rotationSpeed != maxRotationSpeed) {
	    	rotationSpeed += rotationAcceleration*elapsedTime;
	    	rotationSpeed = Math.min(maxRotationSpeed, rotationSpeed);
	    	logger.info("rotation speed = "+rotationSpeed);
    	}
    }

    public void decRotationSpeed(double elapsedTime){
    	if(rotationSpeed != 0){
	    	rotationSpeed -= rotationAcceleration*elapsedTime;
	    	rotationSpeed = Math.max(0, rotationSpeed);
    	}
    }
    
    public Point3D getNearestPossibleVelocity(Point3D desiredVelocity, Point2D target, double elapsedTime){
		if(desiredVelocity.isOrigin()){
			decSpeed(elapsedTime);
			decRotationSpeed(elapsedTime);
			return Point3D.ORIGIN;
		}
		// Speed
		// if braking distance is less than the travelling distance, we decelerate
		// else, we accelerate
		double brakingDistance = (speed*speed)/(deceleration*2);
		if(target != null && getCoord().getDistance(target) <= brakingDistance)
			decSpeed(elapsedTime);
		else
			incSpeed(elapsedTime);
		
		// Rotation speed
		Point2D front = Point2D.ORIGIN.getTranslation(yaw, 1);
		Point2D velocity = Point2D.ORIGIN.getTranslation(desiredVelocity.get2D().getAngle(), 1);
		int turn = AngleUtil.getTurn(Point2D.ORIGIN, front, velocity);
		double diff = AngleUtil.getSmallestDifference(front.getAngle(), velocity.getAngle());
		
		if(turn != AngleUtil.NONE){
			if(rotationSpeed == 0)
				actualRotationDirection = turn;
			
			if(actualRotationDirection == turn){
				double brakingAngle = (rotationSpeed*rotationSpeed)/rotationAcceleration*2;
				if(diff < brakingAngle)
					decRotationSpeed(elapsedTime);
				else
					incRotationSpeed(elapsedTime);
			} else
				decRotationSpeed(elapsedTime);
		} else {
			decRotationSpeed(elapsedTime);
		}
    	double angle = yaw + actualRotationDirection * rotationSpeed * elapsedTime;

    	// we check if we are not too fast to reach the target
    	if(target != null && rotationSpeed != 0){
	    	double rotationPerimeter = speed*AngleUtil.FULL/rotationSpeed; 
	    	double rotationRadius = rotationPerimeter/(2*Math.PI);
	    	Point2D rotationAxis = getCoord().getTranslation(AngleUtil.RIGHT*turn, rotationRadius);
	    	if(target.getDistance(rotationAxis) <= rotationRadius)
	    		decSpeed(elapsedTime);
    	}
    	double distance = speed*elapsedTime;
    	return Point2D.ORIGIN.getTranslation(angle, distance).get3D(0);
	}
    
    
    
}
