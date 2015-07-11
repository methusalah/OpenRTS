package geometry.geom2d;

import geometry.geom3d.Point3D;
import geometry.math.PrecisionUtil;

import java.text.DecimalFormat;

public class Point2D {
	public static final Point2D ORIGIN = new Point2D(0, 0);
	public static final Point2D UNIT_X = new Point2D(1, 0);
	public static final Point2D UNIT_Y = new Point2D(0, 1);
	public static final Point2D UNIT_XY = new Point2D(1, 1);
	public double x;
	public double y;

	public Point2D(){
		
	}
	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
		check();
	}

	public Point2D(Point2D other) {
		this.x = other.x;
		this.y = other.y;
		check();
	}
        
        public Point2D(Point3D p){
            x = p.x;
            y = p.y;
            check();
        }

	private void check() {
		boolean valid = true;
		if (Double.isNaN(x) || Double.isNaN(y))
			valid = false;
		if (Double.isInfinite(x) || Double.isInfinite(y))
			valid = false;

		if (!valid)
			throw new RuntimeException("Can't construct invalid Point2D : " + this);
	}

	public double getDeterminant(Point2D other) {
		return x * other.y - y * other.x;
	}

	public double getSlope(Point2D other) {
		// add exception throwing
		if (other.x - x == 0)
			return Double.POSITIVE_INFINITY;
		return (other.y - y) / (other.x - x);
	}

	public Point2D getSubtraction(Point2D other) {
		return new Point2D(x - other.x, y - other.y);
	}

	public double getDistance(Point2D other) {
		double dx = x - other.x;
		double dy = y - other.y;
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	public Point2D getTransformed(Transform2D transform){
		return transform.getTransformed(this);
	}

	private static DecimalFormat df = new DecimalFormat("0.00");

	public String toString() {
		return "(" + df.format(x) + ", " + df.format(y) + ")";
	}

	public Point2D getTranslation(double angle, double distance) {
		return new Point2D(x + (Math.cos(angle) * distance), y + (Math.sin(angle) * distance));
	}

	public Point2D getAddition(Point2D other) {
		return getAddition(other.x, other.y);
	}

	public Point2D getAddition(double d) {
		return getAddition(d, d);
	}

	public Point2D getAddition(double x, double y) {
		return new Point2D(this.x + x, this.y + y);
	}

	public double getAngle() {
		return Math.atan2(y, x);
	}

	public boolean equals(Object o) {
		if (!(o instanceof Point2D))
			return false;
		Point2D p = (Point2D) o;
		return Math.abs(x - p.x) < PrecisionUtil.APPROX && Math.abs(y - p.y) < PrecisionUtil.APPROX;
	}

	public double getDotProduct(Point2D other) {
		return (x * other.x) + (y * other.y);
	}

	public Point2D getMult(double factor) {
		return new Point2D(x * factor, y * factor);
	}
	public Point2D getMult(double factorX, double factorY) {
		return new Point2D(x * factorX, y * factorY);
	}

	public Point2D getMult(Point2D other) {
		return new Point2D(x * other.x, y * other.y);
	}

	public Point2D getRotation(double angle, Point2D pivot) {
		double newX = pivot.x + (x - pivot.x) * Math.cos(angle) - (y - pivot.y) * Math.sin(angle);
		double newY = pivot.y + (x - pivot.x) * Math.sin(angle) + (y - pivot.y) * Math.cos(angle);
		return new Point2D(newX, newY);
	}

	public Point2D getRotation(double angle) {
		return getRotation(angle, ORIGIN);
	}
	
	public Point2D getNormalized(){
		double length = getDistance(ORIGIN);
		if(length == 0)
			return this;
		else
			return getDivision(length); 
	}
	
	

	public Point2D getDivision(double factor) {
		return getDivision(factor, factor);
	}
	public Point2D getDivision(Point2D other) {
		return getDivision(other.x, other.y);
	}
	public Point2D getDivision(double factorX, double factorY) {
		return new Point2D(x / factorX, y / factorY);
	}

	
	
	public Line2D getExtrudedLine(double angle) {
		Point2D proj = getTranslation(angle, 1);
		return new Line2D(this, proj);
	}

    public Point2D getNegation() {
        return new Point2D(-x, -y);
    }

    public double getLength() {
        return getDistance(ORIGIN);
    }
    
    public Point2D getTruncation(double val) {
        if(getLength() > val)
            return getNormalized().getMult(val);
        return new Point2D(this);
    }

    public boolean isOrigin() {
        return x==0 && y==0;
    }

    public Point2D getScaled(double scale) {
        return getNormalized().getMult(scale);
    }
    
    public Point3D get3D(double z){
        return new Point3D(x, y, z);
    }
    
    public double getManathanDistance(Point2D other){
        double dx = Math.abs(x - other.x);
        double dy = Math.abs(y - other.y);
        return dx + dy;
    }
}
