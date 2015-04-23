package geometry.geom2d;

import geometry.geom2d.intersection.Intersection;
import geometry.geom2d.intersection.LineCircleIntersector;

public class Segment2D extends Line2D {

	public Segment2D(Point2D p0, Point2D p1) {
		super(p0, p1);
	}

	public Segment2D(Line2D other) {
		super(other);
	}
	public Segment2D(Segment2D other) {
		super(other);
	}


	public Segment2D getMirror() {
		return new Segment2D(p1, p0);
	}

	public double getLength() {
		return p0.getDistance(p1);
	}

	public Line2D getBiscector() {
		Point2D middlePoint = getMiddle();
		Point2D directionPoint = middlePoint.getTranslation(getAngle() + Math.PI / 2, 1);
		return new Line2D(middlePoint, directionPoint);
	}

	public Point2D getMiddle() {
		return new Point2D(p0.getAddition(p1.getSubtraction(p0).getDivision(2)));
	}

	@Override
	public String toString() {
		return "Segment " + p0 + " - " + p1;
	}

	public Line2D getLine() {
		return new Line2D(p0, p1);
	}

	public Point2D toVector() {
		return p1.getSubtraction(p0);
	}

	public boolean hasPoint(Point2D point) {
		return p0.equals(point) || p1.equals(point);
	}

	@Override
	public Segment2D getTranslation(double angle, double distance) {
		return new Segment2D(p0.getTranslation(angle, distance), p1.getTranslation(angle, distance));
	}

	public double getOrdinate() {
		return p0.y - getSlope() * p0.x;
	}

	public double getShortestDistance(Point2D p) {
		double sqrLength = (p0.x - p1.x) * (p0.x - p1.x) + (p0.y - p1.y) * (p0.y - p1.y); // just to avoid a sqrt
		if (sqrLength == 0)
			return p0.getDistance(p);
		double t = p.getSubtraction(p0).getDotProduct(p1.getSubtraction(p0)) / sqrLength;
		if (t <= 0)
			return p0.getDistance(p);
		if (t >= 1)
			return p1.getDistance(p);
		Point2D proj = p0.getAddition(p1.getSubtraction(p0).getMult(t));
		return p.getDistance(proj);
	}

	public Line2D getRotation(double angle) {
		return new Line2D(p0, p1.getRotation(angle, p0));
	}
        
	public Intersection getIntersection(Circle2D circle){
		LineCircleIntersector it = new LineCircleIntersector(this, circle);
		return it.intersection;
		
	}
	
	public boolean isHorinzontal() {
        return p0.y == p1.y;
    }
    public boolean isVertical() {
        return p0.x == p1.x;
    }

    public boolean containsProjected(Point2D p) {
        double sign1 = p.getSubtraction(p0).getDotProduct(p1.getSubtraction(p0));
        double sign2 = p.getSubtraction(p1).getDotProduct(p1.getSubtraction(p0));
        if(sign1*sign2 > 0)
            return false;
        return true;
    }
    
	/*
	 * Returns true if two points occupy the same coordinate space.
	 */
	public boolean hasCommonEnd(Line2D other) {
		return p0.equals(other.p0) || p0.equals(other.p1) || p1.equals(other.p0) || p1.equals(other.p1);
	}

	public Point2D getCommonEnd(Line2D other) {
		if (p0.equals(other.p0) || p0.equals(other.p1))
			return p0;
		if (p1.equals(other.p0) || p1.equals(other.p1))
			return p1;
		return null;
	}

	public Point2D getOppositeEnd(Point2D end) {
		if (p0 == end)
			return p1;
		if (p1 == end)
			return p0;
		throw new RuntimeException(this.getClass().getName() + " doesn't have such end point.");
	}
	
	@Override
	public Segment2D getTransformed(Transform2D transform) {
		return new Segment2D(super.getTransformed(transform));
	}
	
	@Override
	public Line2D getBoundedRepresentation() {
		return this;
	}
}
