package model.battlefield.abstractComps;

import java.util.logging.Logger;

import geometry.geom3d.Point3D;
import geometry.math.AngleUtil;
import model.battlefield.army.motion.Motion;
import model.builders.entity.MoverBuilder;

/**
 * A basic abstract entity, containing all needed attributes and methods to set a moving object on the battlefield
 * 
 */
public abstract class SpaceHiker extends Hiker{
	private static final Logger logger = Logger.getLogger(SpaceHiker.class.getName());
    public final double mass;
    public SpaceHiker(Point3D pos,
    		double yaw,
    		double radius,
    		double maxSpeed,
    		double acceleration,
    		double deceleration,
    		double mass,
    		MoverBuilder moverBuilder) {
        super(pos, yaw, radius, maxSpeed, acceleration, deceleration, moverBuilder);
        this.mass = mass;
    }
    
    public double getMaxRotationSpeed() {
        return AngleUtil.toRadians(720);
    }

    @Override
	public void move(Motion motion) {
		if(motion.isEmpty())
			return;
		if(!motion.is3D())
			throw new RuntimeException(SpaceHiker.class.getSimpleName()+" requires 3d "+Motion.class.getSimpleName()+" to move.");
			
		pos = pos.getAddition(motion.getVelocity());
		setDirection(motion.getVelocity());
	}
	
    @Override
	public Motion getNearestPossibleMotion(Motion desiredMotion, Point3D destination, double elapsedTime) {
		adaptSpeedTo(desiredMotion, destination, elapsedTime);
		Motion res = new Motion();
		if(!desiredMotion.isEmpty() && !desiredMotion.is3D())
			throw new RuntimeException(SpaceHiker.class.getSimpleName()+" requires 3d "+Motion.class.getSimpleName()+".");

		Point3D currentMassedVelocity = getDirection().getScaled(mass);
		res.setVelocity(desiredMotion.getVelocity().getScaled(elapsedTime).getAddition(currentMassedVelocity).getScaled(speed*elapsedTime));
		return res;
	}
	
    @Override
	protected void adaptSpeedTo(Motion desiredMotion, Point3D destination, double elapsedTime) {
    	if(desiredMotion.getVelocity().getNorm() == 0
    			|| willMissDestination(destination)
    			)
    		decSpeed(elapsedTime);
    	else
			incSpeed(elapsedTime);
    }
    
    @Override
    protected boolean willOverstepDestination(Point3D destination) {
		double neededDistanceToDecelerate = (speed*speed)/(deceleration*2);
		if(destination != null && getPos().getDistance(destination) <= neededDistanceToDecelerate)
			return true;
		return false;
	}

    @Override
    protected boolean willMissDestination(Point3D destination) {
    	Point3D toDestNormalized = destination.getSubtraction(getPos()).getNormalized();
    	double distance = destination.getDistance(getPos());
    	Point3D massedVelocity = getDirection().getScaled(mass);
    	Point3D addition = toDestNormalized.getAddition(massedVelocity);
    	double angle = massedVelocity.getAngleWith(addition);
    	double neededAngle = massedVelocity.getAngleWith(toDestNormalized);
    	if(distance*angle/(speed/20) < neededAngle)
    		return true;
    	else
    		return false;
    }
}