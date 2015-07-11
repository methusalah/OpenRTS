package geometry.geom2d;

import geometry.math.AngleUtil;

/**
 * Facet is a Segment2D that manage the smoothing of its normal for the two vertices
 * Normal is smoothed only if the neighboring facet angle is less than 50 degrees
 * On acute or lone vertices, normal is smoothed accordingly to the other one, if needed, to keep the curve      
 *  
 * @author Benoît
 *
 */
public class Facet extends Segment2D {
	
	private static final double MAX_SMOOTH_ANGLE = AngleUtil.toRadians(50);
	
	Point2D normal;
	private Point2D smoothed0 = null;
	private Point2D smoothed1 = null;
	
	public Facet(Point2D p0, Point2D p1){
		super(p0, p1);
		normal = Point2D.ORIGIN.getTranslation(getAngle()+AngleUtil.RIGHT, 1);
	}
	
	public Facet(Segment2D seg) {
		this(seg.getStart(), seg.getEnd());
	}

	public void smoothNormal(Facet neighbor) {
		Point2D common = getCommonEnd(neighbor);
		if(common == null)
			throw new IllegalArgumentException("given "+this.getClass().getSimpleName()+" have no common point with this.");
		
		if(AngleUtil.getSmallestDifference(normal.getAngle(), neighbor.normal.getAngle()) > MAX_SMOOTH_ANGLE)
			return;
		
		Point2D smoothed = normal.getAddition(neighbor.normal).getNormalized();
		
		if(common.equals(p0))
			if(common.equals(neighbor.p0))				
				smoothed0 = neighbor.smoothed0 = smoothed;
			else
				smoothed0 = neighbor.smoothed1 = smoothed;
		else
			if(common.equals(neighbor.p0))				
				smoothed1 = neighbor.smoothed0 = smoothed;
			else
				smoothed1 = neighbor.smoothed1 = smoothed;
	}
	
	public Facet getRotation(double angle){
		Facet res = new Facet(p0.getRotation(angle), p1.getRotation(angle));
		res.normal = normal.getRotation(angle);
		if(smoothed0 != null)
			res.smoothed0 = smoothed0.getRotation(angle);
		if(smoothed1 != null)
			res.smoothed1 = smoothed1.getRotation(angle);
		return res;
	}
	
	public Facet getTranslation(double x, double y){
		Facet res = new Facet(p0.getAddition(x, y), p1.getAddition(x, y));
		res.normal = normal;
		res.smoothed0 = smoothed0;
		res.smoothed1 = smoothed1;
		return res;
	}
	
	public Point2D getSmoothedNormal0(){
		if(smoothed0 == null)
			if(smoothed1 == null)
				smoothed0 = smoothed1 = normal;
			else {
				double a = AngleUtil.normalize(normal.getAngle()-AngleUtil.getOrientedDifference(normal.getAngle(), smoothed1.getAngle()));
				smoothed0 = Point2D.UNIT_X.getRotation(a);
			}
		return smoothed0;
	}

	public Point2D getSmoothedNormal1(){
		if(smoothed1 == null)
			if(smoothed0 == null)
				smoothed0 = smoothed1 = normal;
			else {
				double a = AngleUtil.normalize(normal.getAngle()-AngleUtil.getOrientedDifference(normal.getAngle(), smoothed0.getAngle()));
				smoothed1 = Point2D.UNIT_X.getRotation(a);
			}
		return smoothed1;
	}
}
