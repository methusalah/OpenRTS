package geometry.geom2d;

import geometry.collections.EdgeRing;
import geometry.collections.PointRing;
import geometry.math.AngleUtil;

import java.util.ArrayList;


/**
 * This class describes an immutable 2D Polygon.
 * 
 * It can't be empty. It must be simple (accepting coincident edges to manage wholes). It must be counter clockwise.
 * 
 * Holes are allowed and must be linked to exterior by two coincident opposite edges with common limits. Collinear
 * consecutive edges are allowed two.
 * 
 * @author methusalah
 * 
 */
public class Polygon {

	public final PointRing points;
	public AlignedBoundingBox aabb = null;
	public OrientedBoundingBox firstEdgeAlignedbb = null;
	public OrientedBoundingBox obb = null;
	public Point2D centroid = null;
	public double area = Double.NaN;
	public PointRing textureMap = null;
	protected EdgeRing edges=null;
	
	public Polygon(Polygon other) {
		points = new PointRing(other.points);
		check();
	}

	public Polygon(PointRing col) {
		this.points = new PointRing(col);
		check();
	}

	public Polygon(EdgeRing col) {
		points = new PointRing();
		edges = new EdgeRing(col);
		if (!edges.loop())
			throw new RuntimeException("Can't construct " + this.getClass().getName()
					+ " because edge list is not valid loop.");

		for (Segment2D edge : edges)
			points.add(edge.getStart());
		check();
	}

	public Polygon(double[][] points) {
		this.points = new PointRing();
		for (int i = 0; i < points.length; i++)
			this.points.add(new Point2D(points[i][0], points[i][1]));
		check();
	}

	private void check() {
		String valid = "";
		if (points.size() < 3)
			valid += "illegal size ";
		if (!isSimple())
			valid += "complex ";
		if (!isCounterClockwise())
			valid += "clockwise";

		if (valid != "")
			throw new RuntimeException("Can't construct invalid " + this.getClass().getName() + " (" + valid + ") : "
					+ this);
	}

	private boolean isCounterClockwise() {
		return getMathArea() < 0;
	}

	// TODO verifier le check
	private boolean isSimple() {
		ArrayList<Segment2D> edges = getEdges();
		for (Segment2D e1 : edges) {
			for (Segment2D e2 : edges) {
				if (e1 == e2)
					continue;
				if (e1.hasCommonEnd(e2))
					continue;
				if (e1.intersectAtSinglePoint(e2)) {
					Point2D intersection = e1.getUniqueIntersection(e2);
					if (e1.hasPoint(intersection) || e2.hasPoint(intersection))
						continue; // TODO à voir si on autorise le cas des edges qui se touchent mais ne se croisent pas
									// (gestion des trous)
					else
						return false;
				}
			}
		}
		return true;
	}

	public EdgeRing getEdges() {
		if(edges == null) {
			edges = new EdgeRing();
			for (int i = 0; i < points.size(); i++)
				edges.add(new Segment2D(points.get(i), points.getNext(i)));
		}
		return edges;
	}

	private double getMathArea() {
		if(Double.isNaN(area)){
			area = 0;
			int j = points.size()-1;
			for (int i = 0; i < points.size(); i++) {
				area += (points.get(j).x + points.get(i).x) * (points.get(j).y - points.get(i).y);
				j = i;
			}
			area *= 0.5;
		}
		return area;
	}
	
	public double getArea() {
		return Math.abs(area);
	}

	public Point2D getCentroid() {
		if(centroid == null) {
			double cx = 0, cy = 0;
			double A = getMathArea();
			int j;
			double factor = 0;
	
			for (int i = 0; i < points.size(); i++) {
				j = (i + 1) % points.size();
				factor = (points.get(i).x * points.get(j).y - points.get(j).x * points.get(i).y);
				cx += (points.get(i).x + points.get(j).x) * factor;
				cy += (points.get(i).y + points.get(j).y) * factor;
			}
	
			A *= 6f;
			factor = 1 / A;
			cx *= factor;
			cy *= factor;
			centroid = new Point2D(-cx, -cy);
		}
		return centroid;
	}

	public int size() {
		return points.size();
	}

	public Point2D get(int i) {
		return points.get(i);
	}

	public Polygon getRotation(double angle) {
		return getRotation(angle, points.getFirst());
	}

	public Polygon getRotation(double angle, Point2D pivot) {
		PointRing res = new PointRing();
		for (Point2D p : points) {
			res.add(p.getRotation(angle, pivot));
		}
		return new Polygon(res);
	}

	public AlignedBoundingBox getBoundingBox() {
		if(aabb == null)
			return new AlignedBoundingBox(points);
		return aabb;
	}
	
	public OrientedBoundingBox getFirstEdgeAlignedBoundingBox(){
		if(firstEdgeAlignedbb == null)
			firstEdgeAlignedbb = new OrientedBoundingBox(points, getEdges().getFirst().getAngle());
		return firstEdgeAlignedbb;
	}
	
	public OrientedBoundingBox getMinimumBoundingBox() {
		if(obb == null) {
			double minArea = Double.POSITIVE_INFINITY;
			for(Segment2D s : getEdges()) {
				OrientedBoundingBox b = new OrientedBoundingBox(points, s.getAngle());
				if(b.getArea() < minArea) {
					minArea = b.getArea();
					obb = b;
				}
			}
			if(obb == null)
				throw new RuntimeException("Unexpected Error.");
		}
		return obb;
	}
	
	public PointRing getTextureMap() {
		if(textureMap == null) {
			double angle = getEdges().getFirst().getAngle();
			Polygon p = getRotation(-angle);
	
			AlignedBoundingBox bb = p.getBoundingBox();
//			double width = bb.width;
//			double height = bb.height;
			double xTranslation = bb.getPoints().getFirst().x;
			double yTranslation = bb.getPoints().getFirst().y;
	
			textureMap = new PointRing();
			for (Point2D point : p.points) {
				double x = point.x;
				double y = point.y;
				// move the origin
				x -= xTranslation;
				y -= yTranslation;
				// normalize to 0,1
	//			x /= width;
	//			y /= height;
				x = 1-x;
				textureMap.add(new Point2D(x, y));
			}
		}
		return textureMap;
	}

	public Segment2D getLongestEdge() {
		EdgeRing edges = getEdges();
		Segment2D longestEdge = null;
		for (Segment2D edge : edges)
			if (longestEdge == null || edge.getLength() > longestEdge.getLength())
				longestEdge = edge;
		return longestEdge;
	}

	public Polygon getResize(double factor) {
		PointRing res = new PointRing();
		Point2D origin = points.getFirst();
		for (Point2D point : points)
			res.add(point.getAddition(origin.getDistance(point) * factor));
		return new Polygon(res);
	}

	public Polygon getTranslation(double angle, double distance) {
		PointRing res = new PointRing();
		for (Point2D point : points)
			res.add(point.getTranslation(angle, distance));
		return new Polygon(res);
	}

	public ArrayList<Polygon> getSplits(Line2D splitLine) {
		return getSplits(points.getFirst(), splitLine);
	}

//	public PolygonList getSplits2(Line2D splitLine) {
//		ArrayList<EdgeRing> firstSide = new ArrayList<EdgeRing>();
//		ArrayList<EdgeRing> secondSide = new ArrayList<EdgeRing>();
//		EdgeRing actual = new EdgeRing();
//		firstSide.add(actual);
//		
//		for(Segment2D e : getEdges()) {
//			if(e.intersectAtSinglePoint(splitLine)) {
//				Point2D it = e.getUniqueIntersection(splitLine);
//				if(it.equals(e.getEnd())) {
//					// we ignore the intersection at end point.
//					// it will be compute with the next edge
//					actual.add(e);
//					continue;
//				}
//				EdgeRing previous = actual;
//				if(firstSide.contains(actual))
//					actual = findCorrespondance(secondSide, it);
//				else
//					actual = findCorrespondance(firstSide, it);
//				
//				if(it.equals(e.getStart())) {
//				} else {
//				}
//
//			}
//		}
//		return null;
//	}
//	private EdgeRing findCorrespondance(ArrayList<EdgeRing> rings, Point2D point) {
//		for(EdgeRing er : rings) {
//			Segment2D e = new Segment2D(er.getFirst().getStart(), er.getLast().getEnd());
//			if(e.getStart().getDistance(point)<=e.getLength() &&
//					e.getEnd().getDistance(point)<=e.getLength())
//				return er;
//		}
//		EdgeRing res = new EdgeRing();
//		rings.add(res);
//		return new EdgeRing();
//	}

	/*
	 * Split the polygon in two, by the given line. Returns a list of polygons. The first is the one who contains the
	 * given point.
	 */
	public ArrayList<Polygon> getSplits(Point2D pointOnLeftSide, Line2D splitLine) {
		PointRing left = new PointRing();
		PointRing right = new PointRing();
		PointRing actual = left;

		// we check if the given point is valid for this polygon
		if (!points.contains(pointOnLeftSide))
			throw new RuntimeException("The specified point can't be found in this polygon.");

		Point2D p = pointOnLeftSide;
		Point2D start = null;

		// TODO, gerer les splits sur un point unique
		for (int i = 0; i < size(); i++) {
			actual.add(p);

			Point2D next = points.getNext(p);
			Segment2D edge = new Segment2D(p, next);
			if (edge.isCollinear(splitLine))
				throw new RuntimeException("Trying to split a polygon with a line collinear to one of its edges : "+splitLine);
			if (edge.intersect(splitLine)) {
				// An intersection point is found
				Point2D intersection = edge.getAnyIntersection(splitLine);

				// We check if the intersection point is the end of the edge.
				// In this case, we continue to the next edge which will have intersection on its start point.
				if (!intersection.equals(edge.getEnd())) {
					// We check if the intersection is on the edge start.
					// In this case, we don't add the point to the actual polygon
					if (!intersection.equals(edge.getStart()))
						actual.add(intersection);
					// then we switch actual polygon
					if (actual == left) {
						// we save the point just before the intersection. after the split, we will shift the left
						// polygon to this point.
						start = p;
						actual = right;
					} else
						actual = left;

					// after switching, we add the intersection to the new actual polygon.
					actual.add(intersection);
				}
			}
			p = next;
		}
		// debug : check if we get only one intersection
		if (actual == right)
			throw new RuntimeException(
					"The polygon have been splitted on an impair number of points, which is not allowed.");

		left.shiftTo(start);

		ArrayList<Polygon> res = new ArrayList<Polygon>();
		res.add(new Polygon(left));
		res.add(new Polygon(right));
		return res;
	}

	@Override
	public String toString() {
		String res = new String();
		res = this.getClass().getName();
		for (Point2D p : points)
			res += p.toString() + "-";
		return res;
	}

	public String toRefactoringString() {
		String res = new String();
		res = "string to copy : 'new double[][]{";
		for (Point2D point : points) {
			res += "{" + point.x + "," + point.y + "}";
			if (point != points.getLast())
				res += ",";
		}
		res += "}'.";
		return res;
	}

	public boolean isRectangular(double tolerance) {
		EdgeRing edges = getEdges();
		for (Segment2D edge : edges) {
			double angle = AngleUtil.getSmallestDifference(edge.getAngle(), edges.getPrevious(edge).getAngle());
			if (angle < AngleUtil.RIGHT - tolerance)
				return false;
			if (angle > AngleUtil.RIGHT + tolerance && angle < AngleUtil.FLAT - tolerance)
				return false;
		}
		return true;
	}

	public boolean isConvex() {
		for (Point2D point : points) {
			int turn = AngleUtil.getTurn(points.getPrevious(point), point, points.getNext(point));
			if (turn == AngleUtil.CLOCKWISE)
				return false;
		}
		return true;
	}

	/*
	 * The width of a polygon is the width of the minimum bounding box aligned with its first edge.
	 */
	public double getWidth() {
		return getFirstEdgeAlignedBoundingBox().width;
	}

	/*
	 * The height of a polygon is the height of the minimum bounding box aligned with its first edge.
	 */
	public double getHeight() {
		return getFirstEdgeAlignedBoundingBox().height;
	}
	
	/*
	 * returns the cumulated lengthes of all edges.
	 */
	public double getLength() {
		double res = 0;
		for(Segment2D s : getEdges())
			res += s.getLength();
		return res;
	}

	public boolean hasFullyInternalDiagonal(Segment2D diagonal) {
		return hasInternalDiagonal(diagonal) && !hasIntersectingDiagonal(diagonal);
	}

	/*
	 * Code from Computational Geometry Library
	 */
	public boolean hasInternalDiagonal(Segment2D diagonal) {
		Point2D sum = diagonal.getEnd();
		Point2D base = diagonal.getStart();
		Point2D previous = points.getPrevious(base);
		Point2D next = points.getNext(base);

		int turn = AngleUtil.getTurn(previous, base, next);

		if (turn == AngleUtil.CLOCKWISE) {
			// At this point, we know that the angle at the base of the diagonal is reflex
			return AngleUtil.getTurn(sum, base, previous) == AngleUtil.CLOCKWISE
					|| AngleUtil.getTurn(sum, base, next) == AngleUtil.COUNTERCLOCKWISE;
		} else if (turn == AngleUtil.COUNTERCLOCKWISE) {
			// At this point, we know that the angle at the base of the diagonal is convex
			return AngleUtil.getTurn(sum, base, previous) == AngleUtil.CLOCKWISE
					&& AngleUtil.getTurn(sum, base, next) == AngleUtil.COUNTERCLOCKWISE;
		} else
			return true;

	}

	public boolean hasIntersectingDiagonal(Segment2D diagonal) {
		for (Segment2D edge : getEdges()) {
			if (edge.hasCommonEnd(diagonal))
				continue;
			if (edge.intersectAtSinglePoint(diagonal)) {
				return true;
				// Point2D intersection = edge.getUniqueIntersection(diagonal);
				// if(diagonal.hasPoint(intersection))
				// continue;
				// else
				// return true;
			}
		}
		return false;
	}

	public Polygon getConcatenation(Point2D point) {
		PointRing points = this.points;
		points.add(point);
		return new Polygon(points);
	}

	public double getShortestDistance(Point2D p) {
		double res = Double.POSITIVE_INFINITY;
		for (Segment2D s : getEdges()) {
			double d = s.getShortestDistance(p);
			if (d < res)
				res = d;
		}
		return res;
	}
	
	public Polygon getSimplified(double threshold) {
		return new Polygon(simplify(points, threshold));
	}
	
	/*
	 * implementation of the Ramer Douglas Peucker algo
	 */
	private PointRing simplify(PointRing points, double threshold) {
		Point2D first = points.get(0);
		Point2D last = points.get(points.size()-1);
		
		if(points.size() < 3)
			return points;

		int index = -1;
		double biggestDist = 0;
		for(int i = 1; i < points.size()-1; i++) {
			double dist = new Segment2D(first, last).getShortestDistance(points.get(i));
			if(dist > biggestDist) {
				biggestDist = dist;
				index = i;
			}
		}
		
		if(biggestDist > threshold) {
			PointRing slice1 = new PointRing();
			PointRing slice2 = new PointRing();
			for(int i = 0; i < points.size()-1; i++) {
				if(i <= index)
					slice1.add(points.get(i));
				if(i >= index)
					slice2.add(points.get(i));
			}
			
			PointRing res1 = simplify(slice1, threshold);
			PointRing res2 = simplify(slice2, threshold);
			
			PointRing res = res1;
			res.remove(res.size()-1);
			res.addAll(res2);
			return res;
		} else {
			PointRing res = new PointRing();
			res.add(first);
			res.add(last);
			return res;
		}
	}

	public double getLengthTo(Point2D p) {
		double res = 0;
		for(Segment2D s : getEdges()) {
			if(s.p0.equals(p))
				break;
			res += s.getLength();
		}
		return res;
	}
	
	public boolean hasInside(Point2D p){
        for(Segment2D s: getEdges())
            if(AngleUtil.getTurn(s.getStart(), s.getEnd(), p) != AngleUtil.COUNTERCLOCKWISE)
                return false;
        return true;
	}
}
