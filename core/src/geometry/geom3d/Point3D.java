package geometry.geom3d;

import geometry.geom2d.Point2D;
import geometry.math.PrecisionUtil;

import java.text.DecimalFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

public class Point3D {
	public static final Point3D ORIGIN = new Point3D(0, 0, 0);
	public static final Point3D UNIT = new Point3D(1, 1, 1);
	public static final Point3D UNIT_X = new Point3D(1, 0, 0);
	public static final Point3D UNIT_Y = new Point3D(0, 1, 0);
	public static final Point3D UNIT_Z = new Point3D(0, 0, 1);
	private static final DecimalFormat df = new DecimalFormat("0.00");

	@JsonProperty
	public double x, y, z;
	
	public Point3D() {

	}

	public Point3D(Point2D p, double elevation) {
		this(p.x, p.y, elevation);
	}

	public Point3D(Point3D o) {
		this(o.x, o.y, o.z);
	}

	/**
	 * create � new point 3D by : 1- elevating the given point 2D, 2- rotating the given point 2D around Z, 3- rotating the given point 2D in it's own plane.
	 *
	 * @param p
	 * @param val
	 */
	public Point3D(Point2D p, double val, int param) {
		this(p, val, param, Point2D.ORIGIN);
	}

	public Point3D(Point2D p, double val, int param, Point2D pivot) {
		if (param == 1) {
			x = p.x;
			y = p.y;
			z = val;
		} else if (param == 2) {
			x = (p.x - pivot.x) * Math.cos(val) + pivot.x;
			y = (p.x - pivot.x) * Math.sin(val) + pivot.x;
			z = p.y;
		} else if (param == 3) {
			x = p.x * Math.cos(val);
			y = Point2D.ORIGIN.getDistance(p) * Math.sin(val);
			z = p.y * Math.cos(val);
		} else {
			throw new IllegalArgumentException("Invalid param");
		}
		check();
	}

	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		check();
	}

	private void check() {
		boolean valid = true;
		if (Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z)) {
			valid = false;
		}
		if (Double.isInfinite(x) || Double.isInfinite(y) || Double.isInfinite(z)) {
			valid = false;
		}

		if (!valid) {
			throw new RuntimeException("Can't construct invalid " + this.getClass().getSimpleName() + " : " + this);
		}
	}
	
	@JsonIgnore
	public double getDistance(Point3D other) {
		double dx = x - other.x;
		double dy = y - other.y;
		double dz = z - other.z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	@JsonIgnore
	public double getNorm() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	@JsonIgnore
	public Point3D getNormalized() {
		double norm = getNorm();
		if (norm == 0) {
			throw new RuntimeException("Can't normalize a " + this.getClass().getSimpleName() + " of norm = 0 (" + this + ").");
		}
		return getDivision(norm);
	}

	@JsonIgnore
	public Point3D getCross(Point3D o) {
		double resX = ((y * o.z) - (z * o.y));
		double resY = ((z * o.x) - (x * o.z));
		double resZ = ((x * o.y) - (y * o.x));
		return new Point3D(resX, resY, resZ);
	}

	@JsonIgnore
	public Point3D getSubtraction(Point3D o) {
		return new Point3D(x - o.x, y - o.y, z - o.z);
	}

	@JsonIgnore
	public Point3D getDivision(double val) {
		return new Point3D(x / val, y / val, z / val);
	}

	@JsonIgnore
	public Point3D getMult(double val) {
		return new Point3D(x * val, y * val, z * val);
	}

	@JsonIgnore
	public Point3D getMult(double xVal, double yVal, double zVal) {
		return new Point3D(x * xVal, y * yVal, z * zVal);
	}

	@JsonIgnore
	public Point3D getAddition(Point3D o) {
		return new Point3D(x + o.x, y + o.y, z + o.z);
	}

	@JsonIgnore
	public Point3D getAddition(double x, double y, double z) {
		return new Point3D(this.x + x, this.y + y, this.z + z);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Point3D)) {
			return false;
		}
		Point3D p = (Point3D) o;
		return Math.abs(x - p.x) < PrecisionUtil.APPROX && Math.abs(y - p.y) < PrecisionUtil.APPROX && Math.abs(z - p.z) < PrecisionUtil.APPROX;
	}

	@Override
	public String toString() {
		return "(" + df.format(x) + ", " + df.format(y) + ", " + df.format(z) + ")";
	}

	@JsonIgnore
	public boolean isOrigin() {
		return equals(ORIGIN);
	}

	@JsonIgnore
	private double getQuaternion() {
		throw new RuntimeException("bloody quaternions are not implemented.");
	}

	@JsonIgnore
	public Point3D getTruncation(double val) {
		if (getNorm() > val) {
			return getScaled(val);
		}
		return new Point3D(this);
	}

	@JsonIgnore
	public Point3D getScaled(double scale) {
		return getNormalized().getMult(scale);
	}

	@JsonIgnore
	public Point3D getNegation() {
		return new Point3D(-x, -y, -z);
	}

	@JsonIgnore
	public Point2D get2D() {
		return new Point2D(this);
	}

	@JsonIgnore
	public Point3D getRotationAroundZ(double angle) {
		return get2D().getRotation(angle).get3D(z);
	}

	@JsonIgnore
	public Point3D getRotationAroundZ(double angle, Point2D pivot) {
		return get2D().getRotation(angle, pivot).get3D(z);
	}

	@JsonIgnore
	public double getDotProduct(Point3D o) {
		return x * o.x + y * o.y + z * o.z;
	}

	@JsonIgnore
	public double getAngleWith(Point3D o) {
		return Math.acos(getDotProduct(o) / (getNorm() * o.getNorm()));
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

}
