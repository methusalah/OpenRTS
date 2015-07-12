package geometry.geom2d;

import geometry.collections.EdgeRing;
import geometry.collections.PointRing;
import geometry.math.PrecisionUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AlignedBoundingBox extends BoundingShape {

	public double maxY;
	public double minY;
	public double maxX;
	public double minX;
	public double height;
	public double width;

	protected AlignedBoundingBox() {

	}

	public AlignedBoundingBox(Point2D corner1, Point2D corner2) {
		ArrayList<Point2D> points = new ArrayList<>();
		points.add(corner1);
		points.add(corner2);
		computeWith(points);
	}

	public AlignedBoundingBox(ArrayList<Point2D> points) {
		computeWith(points);
	}

	protected void computeWith(ArrayList<Point2D> points) {
		maxX = points.get(0).getX();
		minX = points.get(0).getX();
		maxY = points.get(0).getY();
		minY = points.get(0).getY();

		for (Point2D p : points) {
			if (p.getX() < minX) {
				minX = p.getX();
			}
			if (p.getX() > maxX) {
				maxX = p.getX();
			}
			if (p.getY() < minY) {
				minY = p.getY();
			}
			if (p.getY() > maxY) {
				maxY = p.getY();
			}
		}
		height = maxY - minY;
		width = maxX - minX;
	}

	public boolean contains(Segment2D s) {
		if (contains(s.getStart()) && contains(s.getEnd())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean collide(BoundingShape shape) {
		if (shape instanceof BoundingCircle) {
			return ((BoundingCircle) shape).collide(this);
		}
		if (shape instanceof AlignedBoundingBox) {
			return collideABB((AlignedBoundingBox) shape);
		}
		throw new IllegalArgumentException(shape.getClass().getSimpleName() + " is not yet supported.");
	}

	private boolean collideABB(AlignedBoundingBox b) {
		if (shareX(b) && shareY(b)) {
			return true;
		}
		return false;

	}

	public boolean contains(Point2D p) {
		if (shareX(p) && shareY(p)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean shareX(double x) {
		if (x - maxX <= PrecisionUtil.APPROX && x - minX >= -PrecisionUtil.APPROX) {
			return true;
		} else {
			return false;
		}
	}

	private boolean shareY(double y) {
		if (y - maxY <= PrecisionUtil.APPROX && y - minY >= -PrecisionUtil.APPROX) {
			return true;
		} else {
			return false;
		}
	}

	public boolean shareX(Point2D p) {
		return shareX(p.getX());
	}

	public boolean shareY(Point2D p) {
		return shareY(p.getY());
	}

	public boolean shareX(AlignedBoundingBox b) {
		if (shareX(b.maxX) || shareX(b.minX) || b.shareX(maxX) || b.shareX(minX)) {
			return true;
		}
		return false;
	}

	public boolean shareY(AlignedBoundingBox b) {
		if (shareY(b.maxY) || shareY(b.minY) || b.shareY(maxY) || b.shareY(minY)) {
			return true;
		}
		return false;
	}

	private static DecimalFormat df = new DecimalFormat("0.00");

	@Override
	public String toString() {
		return "BoundingBox [xmin=" + df.format(minX) + ", xmax=" + df.format(maxX) + ", ymin=" + df.format(minY) + ", ymax=" + df.format(maxY) + "]";
	}

	public PointRing getPoints() {
		PointRing res = new PointRing();
		res.add(new Point2D(minX, minY));
		res.add(new Point2D(maxX, minY));
		res.add(new Point2D(maxX, maxY));
		res.add(new Point2D(minX, maxY));
		return res;
	}

	public EdgeRing getEdges() {
		EdgeRing res = new EdgeRing();
		for (Point2D p : getPoints()) {
			res.add(new Segment2D(p, getPoints().getNext(p)));
		}
		return res;
	}

	public double getArea() {
		return height * width;
	}

	@Override
	public Point2D getCenter() {
		return new Point2D(minX + width / 2, minY + height / 2);
	}

}
