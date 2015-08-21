package model.battlefield.army.motion;

import geometry.geom2d.AlignedBoundingBox;
import geometry.geom2d.BoundingCircle;
import geometry.geom2d.BoundingShape;
import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import geometry.math.AngleUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;

import model.ModelManager;
import model.battlefield.abstractComps.FieldComp;
import model.battlefield.army.components.Mover;
import model.battlefield.map.Tile;

/**
 * Ensure that motions are done without critical collisions
 *
 * Whenever a mover decide to move, it must ask permission to the collision manager.
 *
 * Some collisions are allowed (flocking movers, immaterial trinkets like grass).
 *
 * There is three types of forbidden collisions :
 *  - with cliffs
 *  - with a blocking field component (enemy, holding ally, building)
 *  - with solid trinket (tree)
 *
 * The role of this class is to find an allowed close motion vector, or to
 * cancel the motion otherwise.
 *
 * At this time, collision Manager is responsible of leading a unit out of a local
 * blockade. This particluar case may be given to a more specialized class, with
 * dedicated algorithms like local pathfinder.
 *
 * @author Benoît
 */
public class CollisionManager {

	private static final Logger logger = Logger.getLogger(CollisionManager.class.getName());
	private static double MAX_ADAPT_TOLERANCE = AngleUtil.toRadians(180);
	private static double ADAPT_TOLERANCE = AngleUtil.toRadians(100);
	private static double ADAPT_TOLERANCE_INCRASE = AngleUtil.toRadians(20);
	private static double ADAPTATION_STEP = AngleUtil.toRadians(1);

	Mover mover;
	List<BoundingShape> solidShapes = new ArrayList<>();
	List<BoundingShape> blockingShapes = new ArrayList<>();

	double tolerance = ADAPT_TOLERANCE;
	boolean directionIsClockwise = true;

	public CollisionManager(Mover m) {
		this.mover = m;
	}

	public Motion correctMotion(Motion motion, double elapsedTime, List<FieldComp> blockers) {
		if(motion.isEmpty())
			return motion;
		
		updateBlockingShapes(blockers);
		updateSolidShapes();
		Motion res = new Motion();
		if(!mover.fly() && collide()){
			// if mover is already colliding something, we separate it
			// this may happen when two units are overlapping while moving, and are asked to hold ground.
			// One will hold, the other one will separate before holding too
			double traveledDistance = motion.getDistance();
			res.setVelocity(getAntiOverlapVector().getScaled(traveledDistance));
		} else {
			res = motion;
			if(!motion.isEmpty() && !mover.fly()){
				if(res.getDistance() > 0){
					Point3D velocity = adaptMotion(motion);  
					res.setDistance(velocity.getNorm());
					res.setAngle(velocity.get2D().getAngle());
				} else
					res.setAngle(motion.getAngle());
			} else {
				double height = mover.hiker.getPos().getAddition(motion.getVelocity()).z;
				double GroundHeight = ModelManager.getBattlefield().getMap().getAltitudeAt(mover.hiker.getCoord());
				if(height < GroundHeight)
					res.setVelocity(Point3D.ORIGIN);
				else
					res.setVelocity(motion.getVelocity());
			}
		}
		return res;
	}

	private Point3D adaptMotion(Motion motion){
		// TODO to be rewritten : adapt motion should work with motion, and not with velocity
		if(motion.is3D())
			throw new RuntimeException("Can't compute collisions for 3d motions.");
		Point3D velocity = Point2D.ORIGIN.getTranslation(motion.getAngle(), motion.getDistance()).get3D(0);
		if(willCollideSolidShapes(velocity)) {
			return findNearestDirection(velocity);
		}

		if(willCollideBlockingShapes(velocity)) {
			if(tolerance == ADAPT_TOLERANCE) {
				return findNearestDirection(velocity);
			} else {
				return followLastDirection(velocity);
			}
		}

		tolerance = Math.max(tolerance-1, ADAPT_TOLERANCE);
		return velocity;
	}

	private void changeDirection(){
		directionIsClockwise = !directionIsClockwise;
		tolerance += ADAPT_TOLERANCE_INCRASE;
		if(tolerance > MAX_ADAPT_TOLERANCE) {
			giveUp();
		}
	}

	private Point3D getAntiOverlapVector(){
		Point3D fleeingVector = Point3D.ORIGIN;
		BoundingShape shape = mover.hiker.getBounds();
		ArrayList<BoundingShape> allObstacles = new ArrayList<>();
		allObstacles.addAll(solidShapes);
		allObstacles.addAll(blockingShapes);
		for(BoundingShape s : allObstacles) {
			if(shape.collide(s)) {
				fleeingVector = fleeingVector.getAddition(shape.getCenter().getSubtraction(s.getCenter()).get3D(0));
			}
		}
		return fleeingVector;
	}

	private Point3D findNearestDirection(Point3D velocity){
		int count = 0;
		Point2D clockwiseTry = new Point2D(velocity);
		Point2D counterclockwiseTry = new Point2D(velocity);
		while(true){
			clockwiseTry = clockwiseTry.getRotation(-ADAPTATION_STEP);
			if(!willCollide(clockwiseTry.get3D(0))){
				directionIsClockwise = true;
				return clockwiseTry.get3D(velocity.z);
			}

			counterclockwiseTry = counterclockwiseTry.getRotation(ADAPTATION_STEP);
			if(!willCollide(counterclockwiseTry.get3D(0))){
				directionIsClockwise = false;
				return counterclockwiseTry.get3D(velocity.z);
			}

			if(count++ > tolerance/ADAPTATION_STEP){
				giveUp();
				return Point3D.ORIGIN;
			}
		}
	}

	private Point3D followLastDirection(Point3D velocity){
		int count = 0;
		Point2D triedVelocity = new Point2D(velocity);
		while(true){
			if(directionIsClockwise) {
				triedVelocity = triedVelocity.getRotation(-ADAPTATION_STEP);
			} else {
				triedVelocity = triedVelocity.getRotation(ADAPTATION_STEP);
			}

			if(!willCollide(triedVelocity.get3D(0))) {
				return triedVelocity.get3D(velocity.z);
			}

			if(count++ > tolerance/ADAPTATION_STEP) {
				changeDirection();
				return Point3D.ORIGIN;
			}
		}
	}

	private void updateSolidShapes(){
		solidShapes.clear();
		for(int x = -2; x<3; x++) {
			for(int y = -2; y<3; y++){
				Point2D tilePos = mover.hiker.getCoord().getAddition(x, y);
				if (!ModelManager.getBattlefield().getMap().isInBounds(tilePos)) {
					continue;
				}
				Tile t = ModelManager.getBattlefield().getMap().get(tilePos);
				if(t.isBlocked()) {
					solidShapes.add(getTileBoundingBox(t));
				}
			}
		}
	}

	private AlignedBoundingBox getTileBoundingBox(Tile t){
		Point2D p = t.getCoord();
		ArrayList<Point2D> points = new ArrayList<>();
		points.add(p);
		points.add(p.getAddition(1, 0));
		points.add(p.getAddition(1, 1));
		points.add(p.getAddition(0, 1));
		return new AlignedBoundingBox(points);
	}

	private void updateBlockingShapes(List<FieldComp> blockers){
		blockingShapes.clear();
		for(FieldComp m : blockers) {
			if(mover.hiker.getDistance(m)<mover.hiker.getSpacing(m)+1) {
				blockingShapes.add(m.getBounds());
			}
		}
	}

	private boolean collide(){
		return willCollide(Point3D.ORIGIN);
	}
	private boolean willCollide(Point3D velocity){
		return willCollideBlockingShapes(velocity) || willCollideSolidShapes(velocity);
	}
	private boolean willCollideSolidShapes(Point3D velocity){
		BoundingShape futurShape = getFuturShape(velocity);
		if (!ModelManager.getBattlefield().getMap().isInBounds(((BoundingCircle) futurShape).center)) {
			return true;
		}
		return futurShape.collide(solidShapes);
	}

	private boolean willCollideBlockingShapes(Point3D velocity) {
		BoundingShape futurShape = getFuturShape(velocity);
		return futurShape.collide(blockingShapes);
	}

	private BoundingShape getFuturShape(Point3D velocity){
		return new BoundingCircle(mover.hiker.pos.getAddition(velocity).get2D(), mover.hiker.getRadius());
	}

	private void giveUp(){
		tolerance = ADAPT_TOLERANCE;
		mover.setDestinationReached();
	}
}
