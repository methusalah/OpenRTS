package geometry.geom2d.algorithm;

import geometry.collections.PointRing;
import geometry.geom2d.Line2D;
import geometry.geom2d.Point2D;
import geometry.geom2d.Polygon;
import geometry.geom2d.Rectangle;
import geometry.math.PrecisionUtil;

public class RectangleSplitter extends Splitter {
	private Rectangle r;
	private Rectangle aar;
	private Point2D origin;
	
	/**
	 * Note : the x axis of a rectangle is it's first edge.
	 * @param axis
	 * @param rectangle
	 */
	public RectangleSplitter(String axis, Rectangle rectangle) {
		super();
		r = rectangle;
		if(axis == "x") {
			this.axis = new Line2D(r.getEdges().getFirst());
			totalWidth = r.getWidth();
		} else if(axis == "y") {
			// TODO ici si je ne fait rien, il va couper de haut en bas.
			this.axis = new Line2D(r.getEdges().getLast());
			totalWidth = r.getHeight();
		} else throw new IllegalArgumentException("Invalid axis : "+axis);
		aar = r.getRotation(-this.axis.getAngle());
		origin = r.points.getFirst();
	}

	public RectangleSplitter(String axis, Polygon rectangle) {
		this(axis, (Rectangle)rectangle);
	}

	
	@Override
	public void compute() {
		if(computed)
			return;
		
		fitSplittings();
		for(Splitting s : allSplittings) {
			if(PrecisionUtil.areEquals(s.width, 0))
				continue;
			for(int i = 0; i<s.count; i++) {
				splits.get(s.label).add(getLeftSplit(aar, s.width).getRotation(axis.getAngle(), origin));
				aar = getRightSplit(aar, s.width);
			}
		}
		if(aar != null)
			splits.get("remain").add(aar.getRotation(axis.getAngle(), origin));
		computed = true;
	}

	private Rectangle getLeftSplit(Rectangle rectangle, double distance) {
		PointRing res = new PointRing();
		res.add(rectangle.points.getFirst());
		res.add(rectangle.points.getFirst().getTranslation(0, distance));
		res.add(rectangle.points.getLast().getTranslation(0, distance));
		res.add(rectangle.points.getLast());
		return new Rectangle(res);
	}

	private Rectangle getRightSplit(Rectangle rectangle, double distance) {
		if(PrecisionUtil.areEquals(distance, rectangle.getWidth()))
				return null;
		PointRing res = new PointRing();
		res.add(rectangle.points.getFirst().getTranslation(0, distance));
		res.add(rectangle.points.get(1));
		res.add(rectangle.points.get(2));
		res.add(rectangle.points.getLast().getTranslation(0, distance));
		return new Rectangle(res);
	}
}
