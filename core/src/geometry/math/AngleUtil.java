package geometry.math;

import geometry.geom2d.Point2D;

public abstract class AngleUtil {
	public static final double NULL = 0.0;
	public static final double RIGHT = Math.PI/2;
	public static final double FLAT = Math.PI;
	public static final double FULL = FLAT*2;
	public static int COUNTERCLOCKWISE = 1;
	public static int CLOCKWISE = -1;
	public static int NONE = 0;

	public static double normalize(double relativeAngle) {
		while(relativeAngle <= -Math.PI) {
			relativeAngle += Math.PI*2;
		}
		while(relativeAngle > Math.PI) {
			relativeAngle -= Math.PI*2;
		}
		return relativeAngle;
	}

	/**
	 * Returns the orientation of the angle formed by the two edges.
	 *
	 * Note that the result is approximated to 0.001.
	 *
	 * @return negative value equals to turning clockwise,
	 * positive value equals to turning counter clockwise,
	 * zero value equals to collinear.
	 */
	public static int getTurn(Point2D p0, Point2D p1, Point2D q) {
		double turn = p1.getSubtraction(p0).getDeterminant(q.getSubtraction(p1));

		if(turn > PrecisionUtil.APPROX) {
			return COUNTERCLOCKWISE;
		} else if(turn < -PrecisionUtil.APPROX) {
			return CLOCKWISE;
		} else {
			return NONE;
		}
	}

	/**
	 * Computes the unoriented (smallest) difference between two angles.
	 * The angles are assumed to be normalized to the range [-Pi, Pi].
	 * The result will be in the range [0, Pi].
	 *
	 * @param ang1 the angle of one vector (in [-Pi, Pi] )
	 * @param ang2 the angle of the other vector (in range [-Pi, Pi] )
	 * @return the angle (in radians) between the two vectors (in range [0, Pi] )
	 *
	 * Copied from JTS library
	 */
	public static double getSmallestDifference(double ang1, double ang2) {
		double delAngle;
		if (ang1 < ang2) {
			delAngle = ang2 - ang1;
		} else {
			delAngle = ang1 - ang2;
		}

		if (delAngle > Math.PI) {
			delAngle = 2 * Math.PI - delAngle;
		}

		return delAngle;
	}

	/**
	 * return the oriented smallest value between two angles
	 * @param ang1
	 * @param ang2
	 * @return
	 */
	public static double getOrientedDifference(double ang1, double ang2) {
		double na1 = normalize(ang1)+ FLAT*2;
		double na2 = normalize(ang2)+ FLAT*2;

		double diff = Math.abs(na1-na2);
		if(na1 < na2) {
			return normalize(diff);
		} else {
			return normalize(-diff);
		}
	}
	/**
	 * Converts from radians to degrees.
	 * @param radians an angle in radians
	 * @return the angle in degrees
	 *
	 * Copied from JTS library
	 */
	public static double toDegrees(double radians) {
		return (radians * 180) / (Math.PI);
	}

	/**
	 * Converts from degrees to radians.
	 *
	 * @param angleDegrees an angle in degrees
	 * @return the angle in radians
	 *
	 * Copied from JTS library
	 */
	public static double toRadians(double angleDegrees) {
		return (angleDegrees * Math.PI) / 180.0;
	}

	public static boolean areSimilar(double ang1, double ang2) {
		return getSmallestDifference(ang1, ang2) < PrecisionUtil.APPROX;
	}

	/**
	 * get bisector angle between two angles. The order of the arguments matters.
	 * @param ang1
	 * @param ang2
	 * @return
	 */
	public static double getBisector(double ang1, double ang2) {
		return AngleUtil.normalize(ang1+getOrientedDifference(ang1, ang2)/2);
	}

	public static double getChord(double ang) {
		return 2*Math.sin(ang/2);
	}
}
