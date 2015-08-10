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

    public Point3D getNearestPossibleVelocity(Point3D desiredVelocity, Point2D target, double elapsedTime){
//		Material m = new Material(MaterialManager.am, "Common/MatDefs/Misc/Unshaded.j3md");
//		m.getAdditionalRenderState().setWireframe(true);
//		m.setColor("Color", ColorRGBA.Red);
//
//    	Geometry g = new Geometry();
//    	g.setMaterial(m);
//    	g.setMesh(new Line(TranslateUtil.toVector3f(getCoord().get3D(0.5)), TranslateUtil.toVector3f(getCoord().getAddition(desiredVelocity.get2D()).get3D(0.5))));
//		EventManager.post(new GenericEvent(g));
    	Point3D res = Point3D.ORIGIN;
		if(!desiredVelocity.isOrigin()){
			double turningAngle;
			// there are three ways to compute the turning
			if(mass != 1){
				// massed hiker turns according to its momentum
				Point3D currentMassedVelocity = getDirection().getScaled(mass);
				res = desiredVelocity.getAddition(currentMassedVelocity).getNormalized();
				turningAngle = getDirection().getAngleWith(res);
			} else {
				if(stationnaryRotationSpeed != 0){
					// stationary rotator turns at a certain speed
					turningAngle = stationnaryRotationSpeed * elapsedTime;
				} else {
					// turner turns proportionally to its speed, at a certain rate
					turningAngle = speed*elapsedTime*turningRate;
				}
				
				// we avoid to rotate more than necessary
				double diff = AngleUtil.getSmallestDifference(getOrientation(), desiredVelocity.get2D().getAngle());
				if(turningAngle > diff)
					turningAngle = diff;
				res = getDirection().getRotationAroundZ(turningAngle*getTurnTo(desiredVelocity));
			}
			adaptSpeedTo(res, target, elapsedTime);
			res = res.getScaled(speed*elapsedTime);
		} else if(speed != 0){
			decSpeed(elapsedTime);
			res = Point2D.ORIGIN.getTranslation(getOrientation(), speed*elapsedTime).get3D(0);
		}
		
		return res;
	}
    
    private void adaptSpeedTo(Point3D desiredVelocity, Point2D target, double elapsedTime){
		if(desiredVelocity.isOrigin()
//				|| (stationnaryRotationSpeed != 0 && !AngleUtil.areSimilar(yaw, desiredVelocity.get2D().getAngle())) 
//				|| (desiredVelocity.z != 0  && speed > maxSpeed*0.6) ||
				|| willOverstepTarget(target)
//				|| willMissTarget(target, desiredVelocity)
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

    private boolean willMissTarget(Point2D target, Point3D steering){
    	double angle = steering.get2D().getAngle();
    	if(target != null && angle != 0){
	    	double rotationPerimeter = speed*AngleUtil.FULL/angle; 
	    	double rotationRadius = rotationPerimeter/(2*Math.PI);
	    	Point2D rotationAxis = getCoord().getTranslation(getOrientation()+AngleUtil.RIGHT*getTurnTo(steering), rotationRadius);
	    	
//	    	if(rotationRadius<50){
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
	    	if(target.getDistance(rotationAxis) <= rotationRadius)
	    		return true;
    	}
    	return false;
    }
    
    private double getTurnTo(Point3D steering){
		double orientedDiff = AngleUtil.getOrientedDifference(getOrientation(), steering.get2D().getAngle());
		return Math.signum(orientedDiff);
    }

}
