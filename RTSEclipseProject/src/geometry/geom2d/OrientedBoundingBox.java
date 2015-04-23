package geometry.geom2d;

import geometry.collections.PointRing;

import java.util.ArrayList;


public class OrientedBoundingBox extends AlignedBoundingBox {
	
	double orientation;

	public OrientedBoundingBox(ArrayList<Point2D> points, double orientation) {
		super();
		ArrayList<Point2D> rotated = new ArrayList<Point2D>();
		for(Point2D p : points)
			rotated.add(p.getRotation(-orientation));
		computeWith(rotated);
		this.orientation = orientation;
	}
	
	public PointRing getPoints() {
		PointRing points = super.getPoints();
		PointRing res = new PointRing();
		for(Point2D p : points)
			res.add(p.getRotation(orientation));
		return res;
	}
	
	public Segment2D getShortestEdge() {
		if(width < height)
			return new Segment2D(getPoints().getFirst(), getPoints().get(1));
		else
			return new Segment2D(getPoints().getFirst(), getPoints().getLast());
	}

	public Segment2D getLongestEdge() {
		if(width > height)
			return new Segment2D(getPoints().getFirst(), getPoints().get(1));
		else
			return new Segment2D(getPoints().getFirst(), getPoints().getLast());
	}
}
