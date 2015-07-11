package geometry.geom2d;

import geometry.geom2d.intersection.Intersection;
import geometry.geom2d.intersection.LineCircleIntersector;
import geometry.geom2d.intersection.LineLineIntersector;
import geometry.math.AngleUtil;

import java.util.ArrayList;
import java.util.List;


public class Line2D {
	protected Point2D p0;
	protected Point2D p1;

	public Line2D(Point2D p0, Point2D p1) {
		this.p0 = p0;
		this.p1 = p1;
		check();
	}

	public Line2D(Line2D other) {
		this.p0 = other.p0;
		this.p1 = other.p1;
		check();
	}

	private void check() {
		boolean valid = true;
		if (p0 == null || p1 == null)
			valid = false;
		else if (p0.equals(p1))
			valid = false;

		if (!valid)
			throw new RuntimeException("Can't construct invalid " + this.getClass().getName() + " : " + this);
	}

	/**
	 * Computes the intersection point between this line and an other one.
	 * 
	 * @return the intersection Point2D if exist, or "null" if lines are parallel or collinear.
	 * @param the
	 *            other line.
	 */
	public Point2D getUniqueIntersection(Line2D other) {
		LineLineIntersector intersector = new LineLineIntersector(this, other);
		if (intersector.hasUniqueIntersection())
			return intersector.getIntersection().point;
		return null;
	}
	

	public Point2D getAnyIntersection(Line2D other) {
		LineLineIntersector it = new LineLineIntersector(this, other);
		Intersection i = it.getIntersection();
		if (i.exist())
			if(i.isUnique())
				return i.getUnique();
			else if(i.isZone())
				return i.getZoneCenter();
		return null;
	}
	/**
	 * Check if an intersection point between this line and an other one exists.
	 * 
	 * @return true if line have a single intersection point, false if lines are parallel or collinear.
	 * @param the
	 *            other line.
	 */
	public boolean intersectAtSinglePoint(Line2D other) {
		LineLineIntersector intersector = new LineLineIntersector(this, other);
		return intersector.hasUniqueIntersection();
	}

	/**
	 * Check if an intersection point between this line and an other one exists.
	 * 
	 * @return true if line have one or more intersection points (crossing or collinear), false if lines are parallel.
	 * @param the
	 *            other line.
	 */
	public boolean intersect(Line2D other) {
		LineLineIntersector intersector = new LineLineIntersector(this, other);
		return intersector.hasIntersection();
	}
	
	public boolean intersect(Circle2D circle){
		LineCircleIntersector it = new LineCircleIntersector(this, circle);
		return it.intersection.exist();
	}
	
	public Intersection getIntersection(Circle2D circle){
		LineCircleIntersector it = new LineCircleIntersector(this, circle);
		return it.intersection;
		
	}

	public Point2D getStart() {
		return p0;
	}

	public Point2D getEnd() {
		return p1;
	}

	public double getAngle() {
		return p1.getSubtraction(p0).getAngle();
	}

	public boolean coincide(Line2D other) {
		return p0.equals(other.p0) && p1.equals(other.p1) || p0.equals(other.p1) && p1.equals(other.p0);
	}

	public Line2D getTranslation(double angle, double distance) {
		return new Line2D(p0.getTranslation(angle, distance), p1.getTranslation(angle, distance));
	}

	public boolean isCollinear(Line2D other) {
		return contains(other.p0) && contains(other.p1) && other.contains(p0) && other.contains(p1);
	}

	public boolean isBetweenOrOver(Point2D q0, Point2D q1) {
		double Pq0 = AngleUtil.getTurn(p0, p1, q0);
		double Pq1 = AngleUtil.getTurn(p0, p1, q1);

		return Pq0 <= 0 && Pq1 >= 0 || Pq0 >= 0 && Pq1 <= 0;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Line2D))
			return false;
		Line2D l = (Line2D) o;
		return p0.equals(l.p0) && p1.equals(l.p1);
	}

	@Override
	public String toString() {
		return "Line " + p0 + " - " + p1;
	}

	public double getSlope() {
		return p0.getSlope(p1);
	}

	public boolean contains(Point2D p) {
		return AngleUtil.getTurn(p0, p1, p) == AngleUtil.NONE;
	}

	public List<Point2D> getPoints() {
		List<Point2D> res = new ArrayList<Point2D>();
		res.add(p0);
		res.add(p1);
		return res;
	}

	public double getShortestDistance(Point2D p) {
		double sqrLength = (p0.x - p1.x) * (p0.x - p1.x) + (p0.y - p1.y) * (p0.y - p1.y); // just to avoid a sqrt
		double t = p.getSubtraction(p0).getDotProduct(p1.getSubtraction(p0)) / sqrLength;
		Point2D proj = p0.getAddition(p1.getSubtraction(p0).getMult(t));
		return p.getDistance(proj);
	}
	
	public Line2D getTransformed(Transform2D transform){
		return new Line2D(p0.getTransformed(transform), p1.getTransformed(transform));
	}
	
	/**
	 * returns a finite representation in space
	 * used only for intersections 
	 * @return
	 */
	public Line2D getBoundedRepresentation(){
		Point2D bound0 = p0.getTranslation(getAngle(), -1000000);
		Point2D bound1 = p1.getTranslation(getAngle(), 1000000);
		return new Line2D(bound0, bound1);
	}

}
