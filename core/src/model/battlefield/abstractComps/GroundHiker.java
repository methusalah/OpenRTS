package model.battlefield.abstractComps;

import java.util.logging.Logger;

import view.material.MaterialManager;
import view.math.TranslateUtil;
import view.mesh.Circle;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Line;

import event.EventManager;
import event.GenericEvent;
import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import geometry.math.AngleUtil;
import model.battlefield.army.motion.Motion;
import model.builders.entity.MoverBuilder;

/**
 * A basic abstract entity, containing all needed attributes and methods to set a moving object on the battlefield
 * 
 */
public abstract class GroundHiker extends Hiker{
	private static final Logger logger = Logger.getLogger(GroundHiker.class.getName());
    public final double stationnaryRotationSpeed;
    public final double turningRate;
    public GroundHiker(Point3D pos,
    		double yaw,
    		double radius,
    		double maxSpeed,
    		double acceleration,
    		double deceleration,
    		double stationnaryRotationSpeed,
    		double turningRate,
    		MoverBuilder moverBuilder) {
        super(pos, yaw, radius, maxSpeed, acceleration, deceleration, moverBuilder);
        this.stationnaryRotationSpeed = stationnaryRotationSpeed;
        this.turningRate = turningRate;
    }
    
    @Override
	public void move(Motion motion) {
		if(motion.isEmpty())
			return;
		if(motion.is3D())
			throw new RuntimeException(SpaceHiker.class.getSimpleName()+" requires 2d "+Motion.class.getSimpleName()+".");

		if(motion.hasRotation())
			setOrientation(motion.getAngle());
		pos = pos.get2D().getTranslation(getOrientation(), motion.getDistance()).get3D(pos.z);
	}
	
    @Override
	public Motion getNearestPossibleMotion(Motion desiredMotion, Point3D destination, double elapsedTime) {
		if(desiredMotion.is3D())
			throw new RuntimeException(SpaceHiker.class.getSimpleName()+" requires 2d "+Motion.class.getSimpleName()+".");

		adaptSpeedTo(desiredMotion, destination, elapsedTime);
		Motion res = new Motion();
		
		if(!desiredMotion.isEmpty()){
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
	
    @Override
	protected void adaptSpeedTo(Motion desiredMotion, Point3D destination, double elapsedTime) {
		if(desiredMotion.getDistance() == 0
//				|| (stationnaryRotationSpeed != 0 && !AngleUtil.areSimilar(yaw, desiredVelocity.get2D().getAngle())) 
				|| willOverstepDestination(destination)
				|| willMissDestination(destination)
				)
			decSpeed(elapsedTime);
		else
			incSpeed(elapsedTime);
    }
    
    @Override
    protected boolean willOverstepDestination(Point3D target) {
		double neededDistanceToDecelerate = (speed*speed)/(deceleration*2);
		if(target != null && getPos().getDistance(target) <= neededDistanceToDecelerate)
			return true;
		return false;
		
	}

    @Override
    protected boolean willMissDestination(Point3D destination) {
	    	if(destination != null && getTurnTo(destination) != AngleUtil.NONE){
		    	double rotationPerimeter = speed*AngleUtil.FULL/stationnaryRotationSpeed;
		    	double rotationRadius = rotationPerimeter/(2*Math.PI);
		    	Point2D rotationAxis = getCoord().getTranslation(getOrientation()+AngleUtil.RIGHT*getTurnTo(destination), rotationRadius);
		    	
		    	if(destination.get2D().getDistance(rotationAxis) <= rotationRadius)
		    		return true;
	    	}
	    	return false;
	    }
    
	private double getTurnTo(double angle) {
		double orientedDiff = AngleUtil.getOrientedDifference(getOrientation(), AngleUtil.normalize(angle));
		return Math.signum(orientedDiff);
	}

	private double getTurnTo(Point3D p) {
		return getTurnTo(p.get2D().getSubtraction(getCoord()).getAngle());
	}


}
