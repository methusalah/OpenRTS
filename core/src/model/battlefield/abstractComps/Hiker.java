package model.battlefield.abstractComps;

import java.util.logging.Logger;

import view.material.MaterialManager;
import view.math.TranslateUtil;
import view.mesh.Circle;

import com.bulletphysics.linearmath.TransformUtil;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Line;

import event.EventManager;
import event.GenericEvent;
import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import geometry.math.AngleUtil;
import model.battlefield.army.components.Mover;
import model.battlefield.army.motion.Motion;
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
    public final double stationnaryRotationSpeed;
    public final double turningRate;
    public final double mass;
    public final Mover mover;
    
    public double speed = 0;
    public boolean StrongAccelerationAsked = false;
    public boolean StrongDecelerationAsked = false;

    public Hiker(double radius,
    		double maxSpeed,
    		double acceleration,
    		double stationnaryRotationSpeed,
    		double turningRate,
    		double mass, Point3D pos,
    		double yaw,
    		MoverBuilder moverBuilder) {
        super(pos, yaw, radius);
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration;
        this.deceleration = acceleration;
        this.stationnaryRotationSpeed = stationnaryRotationSpeed;
        this.turningRate = turningRate;
        this.mass = mass;
        this.mover = moverBuilder.build(this);
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
    
    public boolean hasMoved(Point3D lastPos, double lastOrientation){
        return lastOrientation != getOrientation() || !lastPos.equals(pos);
    }

    public boolean hasMoved(Point3D lastPos, Point3D lastDirection){
        return !lastDirection.equals(getDirection()) || !lastPos.equals(pos);
    }
    
    public void decelerateStrongly(){
    	StrongDecelerationAsked = true;
    }

    public void accelerateStrongly(){
    	StrongAccelerationAsked = true;
    }
    
    private void incSpeed(double elapsedTime){
    	if(speed < maxSpeed){
			speed += StrongAccelerationAsked?
					acceleration*elapsedTime*10:
					acceleration*elapsedTime;
			speed = Math.min(maxSpeed, speed);
    	}
    }

    private void decSpeed(double elapsedTime){
    	if(speed > 0){
			speed -= StrongDecelerationAsked?
					deceleration*elapsedTime*10:
					deceleration*elapsedTime;
			speed = Math.max(0, speed);
    	}
    }
    
    public void move(Motion motion){
    	if(motion.isEmpty())
    		return;
    	else if(motion.is3D())
    		pos = pos.getAddition(motion.getVelocity());
    	else {
    		if(motion.hasRotation())
    			setOrientation(motion.getAngle());
    		pos = pos.get2D().getTranslation(getOrientation(), motion.getDistance()).get3D(pos.z);
    	}
    }

    public Motion getNearestPossibleMotion(Motion desiredMotion, Point2D destination, double elapsedTime){
		adaptSpeedTo(desiredMotion, destination, elapsedTime);
    	Motion res = new Motion();
		if(desiredMotion.is3D()){
			// massed hiker turns according to its momentum
			Point3D currentMassedVelocity = getDirection().getScaled(mass);
			res.setVelocity(desiredMotion.getVelocity().getAddition(currentMassedVelocity).getScaled(speed*elapsedTime));
		} else if(desiredMotion.hasRotation()){
			double turning = stationnaryRotationSpeed != 0?
					stationnaryRotationSpeed:
					speed*turningRate;
			turning *= elapsedTime;
			// we avoid to rotate more than necessary
			turning = Math.min(turning, AngleUtil.getSmallestDifference(getOrientation(), desiredMotion.getAngle()));
			res.setAngle(getOrientation() + turning * getTurnTo(desiredMotion.getAngle()));
			res.setDistance(speed*elapsedTime);
		} else if(speed > 0){
			res.setAngle(getOrientation());
			res.setDistance(speed*elapsedTime);
		}
		return res;
	}
    
    private void adaptSpeedTo(Motion desiredMotion, Point2D destination, double elapsedTime){
		if(desiredMotion.isEmpty()
//				|| (stationnaryRotationSpeed != 0 && !AngleUtil.areSimilar(yaw, desiredVelocity.get2D().getAngle())) 
				|| willOverstepTarget(destination)
				|| willMissTarget(destination)
				)
			decSpeed(elapsedTime);
		else
			incSpeed(elapsedTime);
    }
    
//    private void adaptRotationSpeedTo(Point3D desiredVelocity, Point2D target, double elapsedTime){
//    	
//		Point2D front = Point2D.ORIGIN.getTranslation(yaw, 1);
//		Point2D velocity = Point2D.ORIGIN.getTranslation(desiredVelocity.get2D().getAngle(), 1);
//		int turn = AngleUtil.getTurn(Point2D.ORIGIN, front, velocity);
//		double diff = AngleUtil.getSmallestDifference(front.getAngle(), velocity.getAngle());
//		
//		if(rotationSpeed == 0)
//			actualRotationDirection = turn;
//
//		if(turn == AngleUtil.NONE || turn != actualRotationDirection || willOverturn(diff))
////		if(turn == AngleUtil.NONE || turn != actualRotationDirection || willOverturn(diff))
//			decRotationSpeed(elapsedTime);
//		else
//			incRotationSpeed(elapsedTime);
//    }
//    
//    private boolean willOverturn(double angleToTurn){
//		double neededAngleToDecelerate = (rotationSpeed*rotationSpeed)/(rotationDeceleration*2);
//		if(angleToTurn <= neededAngleToDecelerate)
//			return true;
//		return false;
//    }
    
    private boolean willOverstepTarget(Point2D target){
		double neededDistanceToDecelerate = (speed*speed)/(deceleration*2);
		if(target != null && getCoord().getDistance(target) <= neededDistanceToDecelerate)
			return true;
		return false;
    	
    }

    private boolean willMissTarget(Point2D destination){
    	if(destination != null && getTurnTo(destination) != AngleUtil.NONE){
	    	double rotationPerimeter = speed*AngleUtil.FULL/stationnaryRotationSpeed;
	    	double rotationRadius = rotationPerimeter/(2*Math.PI);
	    	Point2D rotationAxis = getCoord().getTranslation(getOrientation()+AngleUtil.RIGHT*getTurnTo(destination), rotationRadius);
	    	
//			if(rotationRadius<50){
//				Material m = new Material(MaterialManager.am, "Common/MatDefs/Misc/Unshaded.j3md");
//				m.getAdditionalRenderState().setWireframe(true);
//				m.setColor("Color", ColorRGBA.Red);
//	
//		    	Geometry g = new Geometry();
//		    	g.setMaterial(m);
//		    	g.setMesh(new Circle((float) rotationRadius));
//		    	g.setLocalTranslation(TranslateUtil.toVector3f(rotationAxis.get3D(0.2)));
//				EventManager.post(new GenericEvent(g));
//	    	}
	    	if(destination.getDistance(rotationAxis) <= rotationRadius)
	    		return true;
    	}
    	return false;
    }
    
    private double getTurnTo(double angle){
		double orientedDiff = AngleUtil.getOrientedDifference(getOrientation(), AngleUtil.normalize(angle));
		return Math.signum(orientedDiff);
    }
    private double getTurnTo(Point2D p){
    	return getTurnTo(p.getSubtraction(getCoord()).getAngle());
    }

}
