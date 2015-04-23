package geometry.geom2d;

public class Ray2D extends Line2D{

	public Ray2D(Point2D startPoint, double angle) {
		this(startPoint, startPoint.getTranslation(angle, 1));
	}

	public Ray2D(Point2D startPoint, Point2D otherPoint) {
		super(startPoint, otherPoint);
	}
	
	public Point2D getPointAt(double distance){
		return p0.getTranslation(getAngle(), distance);
	}
	
	@Override
	public Line2D getBoundedRepresentation() {
		Point2D bound1 = p1.getTranslation(getAngle(), 1000000);
		return new Line2D(p0, bound1);
	}
}
