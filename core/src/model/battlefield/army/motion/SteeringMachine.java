package model.battlefield.army.motion;

import geometry.geom2d.Circle2D;
import geometry.geom2d.Point2D;
import geometry.geom2d.Segment2D;
import geometry.geom2d.intersection.Intersection;
import geometry.geom3d.Point3D;
import geometry.math.AngleUtil;

import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Logger;

import model.battlefield.abstractComps.FieldComp;
import model.battlefield.army.components.Mover;

/**
 * Compute the best motion according to given orders, environment and inner parameters. Each mover has it's own steering machine to compute motion. Before each
 * frame, the steering machine receive many orders to apply, with many goals. For example, a mover may want to proceed to its destination, to separate with
 * neighbors and to avoid a nearly obstacle at the same time. Each goal has a force and the steering machine finds the vector resulting of these goals, forces,
 * mover's mass and elapsed time since last frame. Mover's speed is applied by the collision manager which may want to apply brake.
 *
 * @author Benoît
 */
public class SteeringMachine {
	private static double DESTINATION_REACH_TOLERANCE = 0.3;
	private static double DESTINATION_REACH_SIGNAL_DISTANCE = 2;
	private static double MAX_ANTICIPATION = 2.5;

	private static double FOLLOW_PATH_FORCE = 1;
	private static double ALIGNMENT_FORCE = 0.7;
	private static double SEPARATION_FORCE = 5;
	private static double SEPARATION_FORCE_FOR_FLYING = 0.01;
	private static double COHESION_FORCE = 1;

	final Mover mover;

	Point3D steering = Point3D.ORIGIN;
	public boolean motionIn3D = false; 

	public Point3D separationForce = Point3D.ORIGIN;
	public Point3D cohesionForce = Point3D.ORIGIN;
	public Point3D alignementForce = Point3D.ORIGIN;
	public Point3D queueForce = Point3D.ORIGIN;
	public Point3D destinationForce = Point3D.ORIGIN;
	public Point3D avoidModification = Point3D.ORIGIN;

	public SteeringMachine(Mover m) {
		mover = m;
	}

	private static final Logger logger = Logger.getLogger(Mover.class.getName());
	
	public Motion collectSteering() {
		Motion res = new Motion();
		if(motionIn3D){
			res.setVelocity(steering);
		} else if(!steering.isOrigin()){
			res.setDistance(1);
			res.setAngle(steering.get2D().getAngle());
		}
		steering = Point3D.ORIGIN;
		motionIn3D = false;
		return res;
	}

	public void proceedToDestination() {
		destinationForce = getFollowFlowFieldForce();
		steering = steering.getAddition(destinationForce);
	}

	public void applySeparation(List<Mover> neighbors) {
		separationForce = getSeparationForce(neighbors);
		steering = steering.getAddition(separationForce);
	}

	public void applyCohesion(List<Mover> neighbors) {
		cohesionForce = getCohesionForce(neighbors);
		steering = steering.getAddition(cohesionForce);
	}

	public void applyAlignment(List<Mover> neighbors) {
		alignementForce = getAlignmentForce(neighbors);
		steering = steering.getAddition(alignementForce);
	}

	public void applyQueue(List<Mover> neighbors) {
		queueForce = getQueueForce(neighbors);
		steering = steering.getAddition(queueForce);
	}
	
	
	public void avoidBlockers(List<FieldComp> blockers) {
		Point3D savedSteering = steering;
		modifySteeringToAvoid(blockers);
		if (savedSteering.equals(steering)) {
			avoidModification = Point3D.ORIGIN;
		} else {
			avoidModification = steering;
		}
	}

	public void seek(Point3D target) {
		steering = steering.getAddition(target.getSubtraction(mover.hiker.pos).getNormalized());
	}

	public void seek(Mover m) {
		seek(m.hiker.pos);
	}

	private Point3D getFollowFlowFieldForce() {
		Point2D destination = mover.getDestination().get2D();
		
		if (destination == null) {
			return Point3D.ORIGIN;
		} else if (mover.hiker.getCoord().getDistance(destination) < DESTINATION_REACH_TOLERANCE) {
			mover.setDestinationReached();
			return Point3D.ORIGIN;
		} else {
			Point2D flatForce;
			if (mover.fly()) {
				flatForce = destination.getSubtraction(mover.hiker.getCoord()).getNormalized().getMult(FOLLOW_PATH_FORCE);
			} else {
				flatForce = mover.flowfield.getVector(mover.hiker.getCoord()).getMult(FOLLOW_PATH_FORCE);
			}
			return new Point3D(flatForce, 0);
		}
	}
	private static DecimalFormat df = new DecimalFormat("0.00");

	private Point3D getSeparationForce(List<Mover> neighbors) {
		Point3D res = Point3D.ORIGIN;
		if (neighbors.isEmpty()) {
			return res;
		}

		for (Mover n : neighbors) {
			double neededDistance = n.hiker.getSpacing(mover.hiker) - n.hiker.getDistance(mover.hiker);
			if (neededDistance <= 0) {
				continue;
			}
			if(n.hiker.priority < mover.hiker.priority)
				neededDistance /= 10;

			Point3D sepVector = n.hiker.getVectorTo(mover.hiker).getScaled(neededDistance);
			
			res = res.getAddition(sepVector);
		}
		if (res.isOrigin()) {
			return res;
		}
		if (mover.fly()) {
			return res.getMult(SEPARATION_FORCE_FOR_FLYING);
		}
		return res.getMult(SEPARATION_FORCE);
	}

	private Point3D getCohesionForce(List<Mover> neighbors) {
		Point3D res = Point3D.ORIGIN;
		if (neighbors.isEmpty()) {
			return res;
		}

		for (Mover n : neighbors) {
			res = res.getAddition(n.hiker.pos);
		}
		res = res.getDivision(neighbors.size());
		res = res.getSubtraction(mover.hiker.pos);
		return res.getNormalized().getMult(COHESION_FORCE);
	}

	private Point3D getAlignmentForce(List<Mover> neighbors) {
		Point3D res = Point3D.ORIGIN;
		if (neighbors.isEmpty()) {
			return res;
		}

		for (Mover n : neighbors) {
			res = res.getAddition(n.hiker.pos);
		}
		res = res.getDivision(neighbors.size());
		return res.getNormalized().getMult(ALIGNMENT_FORCE);
	}
	
	private Point3D getQueueForce(List<Mover> neighbors){
		Point3D res = Point3D.ORIGIN;
		if (neighbors.isEmpty() || mover.fly()) {
			return res;
		}
		
		Point2D ahead = mover.hiker.getCoord();//.getTranslation(mover.hiker.getYaw(), mover.hiker.getRadius());

		for (Mover n : neighbors) {
			if(n.hiker.getCoord().getDistance(ahead) <= n.hiker.getSpacing(mover.hiker)){
				res = res.getAddition(0, 0, 1);
				break;
			}
		}
		if (res.isOrigin()) {
			return res;
		}
		return res;
	}

	/**
	 * avoidance is on (x;y) plane only
	 *
	 * @param blockers
	 */
	private void modifySteeringToAvoid(List<FieldComp> blockers) {
		if (steering.isOrigin()) {
			return;
		}

		Segment2D anticipation = new Segment2D(mover.hiker.getCoord(), mover.hiker.getCoord()
				.getTranslation(new Point2D(steering).getAngle(), MAX_ANTICIPATION));

		Point2D intersection = null;
		FieldComp obstacle = null;
		for (FieldComp m : blockers) {
			Intersection i = anticipation.getIntersection(new Circle2D(m.getCoord(), mover.hiker.getSpacing(m)));

			if (i.exist() && (intersection == null || i.getAll().get(0).getDistance(mover.hiker.getCoord()) < intersection.getDistance(mover.hiker.getCoord()))) {
				intersection = i.getAll().get(0);
				obstacle = m;
			}
		}

		if (obstacle == null) {
			return;
		}
		// if we are too close, we do not try to avoid and let the constraint manager manage the collision
		if (obstacle.getDistance(mover.hiker) < obstacle.getSpacing(mover.hiker) * 1.1) {
			return;
		}

		double hypotenuse = mover.hiker.getCoord().getDistance(obstacle.getCoord());
		double oppose = mover.hiker.getSpacing(obstacle);

		double adjacent = Math.sqrt(hypotenuse * hypotenuse - oppose * oppose);
		double avoidanceAngle = Math.atan(oppose / adjacent) * 1.1;

		Point2D toObstacle = obstacle.getCoord().getSubtraction(mover.hiker.getCoord()).getNormalized();

		if (AngleUtil.getOrientedDifference(new Point2D(steering).getAngle(), toObstacle.getAngle()) < 0) {
			steering = new Point3D(toObstacle.getRotation(avoidanceAngle), steering.z);
		} else {
			steering = new Point3D(toObstacle.getRotation(-avoidanceAngle), steering.z);
		}
	}

}
