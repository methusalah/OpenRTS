package geometry.geom2d.intersection;

import geometry.geom2d.Line2D;
import geometry.geom2d.Point2D;
import geometry.math.AngleUtil;
import geometry.math.PrecisionUtil;


/**
 * Intersector computes the intersection between two lines defined by four vectors given in parameters. It works in two
 * times : - first, it find a result of the intersection and gives some boolean methods to check it, - then it computes
 * the single intersection point or intersection zone if (and only if) it's requested.
 * 
 * Intersector makes no difference between lines and segments in its intersection calculation, so you must check the
 * booleans before asking for the intersection.
 * 
 * For example, if you gives two segments and ask for the single intersection point, it will return the intersection
 * point of the lines defined by the segments. you must check the intersectSegmentToSegment() method to be sure.
 * 
 * Please note that the intersection calculation is computed with division and is not absolutely precise.
 * 
 * For information, the Angle.getTurn(a, b, c) method uses determinant to check the turning sense.
 * 
 * @author Benoit Dumas
 * @version $Id$
 */
public class LineLineIntersector {

	enum Type {PARALLEL, INTERSECT, COLLINEAR, INVALID}

	private final Line2D line1, line2;
	Point2D p0, p1, q0, q1;

	public Intersection intersection = null;
	private Type result;
	private boolean inPLimits = false;
	private boolean inQLimits = false;

	/**
	 * Constructs a new Intersector with two lines.
	 */
	public LineLineIntersector(Line2D line1, Line2D line2) {
		this.line1 = line1.getBoundedRepresentation();
		this.line2 = line2.getBoundedRepresentation();
		p0 = this.line1.getStart();
		p1 = this.line1.getEnd();
		q0 = this.line2.getStart();
		q1 = this.line2.getEnd();
		
		result = establishIntersectionType();
	}	
	
	public boolean hasIntersection(){
		if(result == Type.INTERSECT || result == Type.COLLINEAR)
			return isInLimit();
		return false;
	}
	public boolean hasUniqueIntersection(){
		if(result == Type.INTERSECT)
			return isInLimit();
		return false;
	}
	
	private boolean isInLimit(){
		return inPLimits && inQLimits;
		
//		Class c1 = line1.getClass();
//		Class c2 = line2.getClass();
//		
//		if(c1 == Line2D.class && c2 == Line2D.class)
//			// line/line
//			return true;
//		else if(c1 == Line2D.class && c2 == Segment2D.class)
//			// line/segment
//			return inQLimits;
//		else if(c1 == Line2D.class && c2 == Ray2D.class)
//			// line/ray
//			return inQLimits;
//		else if(c1 == Segment2D.class && c2 == Line2D.class)
//			// segment/line
//			return inPLimits;
//		else if(c1 == Segment2D.class && c2 == Segment2D.class)
//			// segment/segment
//			return inPLimits && inQLimits;
//		else if(c1 == Segment2D.class && c2 == Ray2D.class)
//			// segment/ray
//			return inPLimits && inQLimits;
//		else if(c1 == Ray2D.class && c2 == Line2D.class)
//			// ray/line
//			return inPLimits;
//		else if(c1 == Ray2D.class && c2 == Segment2D.class)
//			// ray/segment
//			return inPLimits && inQLimits;
//		else if(c1 == Ray2D.class && c2 == Ray2D.class)
//			// ray/ray
//			return inPLimits && inQLimits;
//		return false;
	}
	
	public boolean hasLineToLineIntersection() {
		return result == Type.INTERSECT || result == Type.COLLINEAR;
	}

	public boolean hasUniqueLineToLineIntersection() {
		return result == Type.INTERSECT;
	}

	public boolean hasSegmentToLineIntersection() {
		return result == Type.INTERSECT && inPLimits || result == Type.COLLINEAR && inPLimits;
	}

	public boolean hasUniqueSegmentToLineIntersection() {
		return result == Type.INTERSECT && inPLimits;
	}

	public boolean hasSegmentToSegmentIntersection() {
		return result == Type.INTERSECT && inPLimits && inQLimits || result == Type.COLLINEAR && inPLimits && inQLimits;
	}

	public boolean hasUniqueSegmentToSegmentIntersection() {
		return result == Type.INTERSECT && inPLimits && inQLimits;
	}

	public boolean isCollinear() {
		return result == Type.COLLINEAR;
	}

	/**
	 * This method returns an intersection point only if there is only one.
	 * 
	 * @return single intersection point, or null if none or more than one exists.
	 */
	public Intersection getIntersection() {
		if (intersection == null)
			computeIntersectionPoint();
		return intersection;
	}

	private Type establishIntersectionType() {
		// for each end point, find the side of the other line.
		// if two end points lie on opposite sides of the other line, then the lines are crossing.
		// if all end points lie on opposite sides, then the segments are crossing.

		double Pq0 = AngleUtil.getTurn(p0, p1, q0);
		double Pq1 = AngleUtil.getTurn(p0, p1, q1);

		double Qp0 = AngleUtil.getTurn(q0, q1, p0);
		double Qp1 = AngleUtil.getTurn(q0, q1, p1);

		// check if all turn have none angle. In this case, lines are collinear.
		if (Pq0 == AngleUtil.NONE && Pq1 == AngleUtil.NONE || Qp0 == AngleUtil.NONE && Qp1 == AngleUtil.NONE) {
			// at this point, we know that lines are collinear.
			// we must check if they overlap for segments intersection
			if (q0.getDistance(p0) <= p0.getDistance(p1) && q0.getDistance(p1) <= p0.getDistance(p1)) {
				// then q0 is in P limits and p0 or p1 is in Q limits
				// TODO this check is no sufficient
				inPLimits = true;
				inQLimits = true;
			}
			return Type.COLLINEAR;
		}
		// check if q0 and q1 lie around P AND p0 and p1 lie around Q.
		// in this case, the two segments intersect
		else if (Pq0 * Pq1 <= 0 && Qp0 * Qp1 <= 0) {
			// else if(Pq0 <= 0 && Pq1 >= 0 && Qp0 <= 0 && Qp1 >= 0 ||
			// Pq0 >= 0 && Pq1 <= 0 && Qp0 >= 0 && Qp1 <= 0){

			inPLimits = true;
			inQLimits = true;
			return Type.INTERSECT;
		}

		// At this point, we know that segments are not crossing
		// check if q0 and q1 lie around P or p0 and p1 lie around Q.
		// in this case, a segment cross a line
		else if (Pq0 * Pq1 <= 0) {
			inQLimits = true;
			return Type.INTERSECT;
		} else if (Qp0 * Qp1 <= 0) {
			inPLimits = true;
			return Type.INTERSECT;
		}

		// At this point, we know that each segment lie on one side of the other
		// We now check the slope to know if lines are Type.PARALLEL
		double pSlope = p0.getSlope(p1);
		double qSlope = q0.getSlope(q1);
		if (PrecisionUtil.areEquals(pSlope, qSlope))
			// TODO check the infinity case
			// this test works even if the slopes are "Double.infinity" due to the verticality of the lines and division
			// by 0
			return Type.PARALLEL;
		else
			return Type.INTERSECT;
	}

	
	
	
	
	private void computeIntersectionPoint() {
		intersection = new Intersection();
		if (result == Type.INTERSECT) {
			/*
			 * Single point intersection
			 * 
			 * This calculation method needs divisions, which may cause approximation problems. The intersection point,
			 * once rounded to double precision, may be out the line bounding. If "on-the-line" intersection point is
			 * needed, you will have to use a more robust method.
			 */
			double x;
			double y;

			double pSlope = p0.getSlope(p1);
			double qSlope = q0.getSlope(q1);
			double pOrdinate = p0.y - pSlope * p0.x;
			double qOrdinate = q0.y - qSlope * q0.x;

			// At this point, we already know that pSlope != qSlope (checked in previously launched method)
			// So the divide by 0 case should never happen.
			if(qSlope - pSlope == 0)
				throw new RuntimeException("Division by zero.");

			// We must check if the lines are verticals (infinite slope)
			if (Double.isInfinite(pSlope) && Double.isInfinite(qSlope))
				throw new RuntimeException("The two lines have infinte slope (collinear), not intersecting.");
			
			else if (Double.isInfinite(pSlope)) {
				x = p0.x;
				y = qSlope * x + qOrdinate;
			} else if (Double.isInfinite(qSlope)) {
				x = q0.x;
				y = pSlope * x + pOrdinate;
			} else {
				x = (pOrdinate - qOrdinate) / (qSlope - pSlope);
				y = pSlope * x + pOrdinate;
			}
			intersection.point = new Point2D(x, y);
			
		} else if (result == Type.COLLINEAR) {
			/*
			 * Collinear intersection zone
			 * 
			 * At this point, we have to find the two points that enclose the intersection zone. We use distances to
			 * check if a segment's end is inside the other segment. The single intersection point is set to the middle
			 * of the intersection zone.
			 * 
			 * Note that if P and Q are not overlapping, then there is no intersection zone and all intersection points
			 * are set to "null". For segments, it means that there is no intersection. For lines, it means that
			 * intersection zone is infinite.
			 */
			if (q0.getDistance(p0) <= p0.getDistance(p1) && q0.getDistance(p1) <= p0.getDistance(p1)) {
				// then q0 is in P
				intersection.zoneStart = q0;
				if (q1.getDistance(p0) <= p0.getDistance(p1) && q1.getDistance(p1) <= p0.getDistance(p1)) {
					// then q0 and q1 are both in P
					System.out.println("(collinear) Q is in P");
					intersection.zoneEnd = q1;
				} else // then q0 is in P but q1 is out of P
				if (p0.getDistance(q0) <= q0.getDistance(q1) && q0.getDistance(p1) <= q0.getDistance(q1))
					// then q0 is in P and p0 is in Q
					intersection.zoneEnd = p0;
				else
					intersection.zoneEnd = p1;
			} else // then q0 is out of p
			if (q1.getDistance(p0) <= p0.getDistance(p1) && q1.getDistance(p1) <= p0.getDistance(p1)) {
				// then q0 is out of P and q1 is in P
				intersection.zoneStart = q1;
				if (p0.getDistance(q0) <= q0.getDistance(q1) && q0.getDistance(p1) <= q0.getDistance(q1))
					// then q1 is in P and p0 is in Q
					intersection.zoneEnd = p0;
				else
					intersection.zoneEnd = p1;
			} else { // then q0 and q1 are both out of P
				if (p0.getDistance(q0) <= q0.getDistance(q1) && q0.getDistance(p1) <= q0.getDistance(q1)) {
					// then P is in Q
					System.out.println("(collinear) P is in Q");
					intersection.zoneStart = p0;
					intersection.zoneEnd = p1;
				} else {
					System.out.println(LineLineIntersector.class.getName() + " (collinear) and Q and P are not overlapped.");
					intersection.zoneStart = null;
					intersection.zoneEnd = null;
					intersection = null;
					return;
				}
			}
		} else
			throw new RuntimeException("Intersection point cannot be computed if result is parallel or invalild.");
	}

}
