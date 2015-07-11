package geometry.geom2d;

import geometry.collections.EdgeRing;
import geometry.collections.PointRing;
import geometry.math.AngleUtil;
import geometry.math.PrecisionUtil;

public class Rectangle extends Polygon {

	public Rectangle(Polygon other) {
		super(other);
		check();
	}

	public Rectangle(PointRing col) {
		super(col);
		check();
	}

	public Rectangle(EdgeRing col) {
		super(col);
		check();
	}

	public Rectangle(double[][] points) {
		super(points);
		check();
	}
	
	private void check() {
		String valid = "";
		if (points.size() != 4)
			valid += "illegal size ";
		if(!super.isRectangular(PrecisionUtil.APPROX))
			valid += "not quadrilateral ";
		
		if (valid != "")
			throw new RuntimeException("Can't construct invalid " + this.getClass().getName() + " (" + valid + ") : "
					+ this);
	}

	private boolean isQuad() {
		for(Segment2D e : getEdges()) {
			if(!AngleUtil.areSimilar(getEdges().getNext(e).getAngle()-e.getAngle(), AngleUtil.RIGHT))
				return false;
		}
		return true;
	}

	// BEN ici un truc que je comprend pas : j'ai tenté d'overrider la methode
	// getArea() pour un truc plus rapide : longeur * largeur.
	// Sauf que le polygon se check() à la creation, et il calcule notamment
	// son area pour verifier qu'il soit bien counter clockwise.
	// Du coup bon, je stock l'aire une bonne fois pour toute, mais je ne comprend
	// pas du tout pourquoi le check() dans le constructeur de Polygon vient chercher
	// la methode overrid�e de la classe fille Rectangle.
	
	@Override
	public Point2D getCentroid() {
		return getDiagonal().getMiddle();
	}

	private Segment2D getDiagonal() {
		return new Segment2D(points.getFirst(), points.get(2));
	}

	@Override
	public boolean isRectangular(double tolerance) {
		return true;
	}

	@Override
	public double getWidth() {
		return edges.getFirst().getLength();
	}

	@Override
	public double getHeight() {
		return edges.getLast().getLength();
	}

	@Override
	public Rectangle getRotation(double angle) {
		return new Rectangle(super.getRotation(angle));
	}

	@Override
	public Rectangle getRotation(double angle, Point2D pivot) {
		return new Rectangle(super.getRotation(angle, pivot));
	}
	
	
	

}
