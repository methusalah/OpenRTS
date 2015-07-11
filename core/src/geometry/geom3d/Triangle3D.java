package geometry.geom3d;

import geometry.geom2d.Point2D;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 *
 */
public class Triangle3D {

	private static final Logger logger = Logger.getLogger(Triangle3D.class.getName());

	public Point3D a;
	public Point3D b;
	public Point3D c;

	public Point3D normal;

	public Triangle3D(Point3D a, Point3D b, Point3D c) {
		this.a = a;
		this.b = b;
		this.c = c;
		try
		{
			normal = b.getSubtraction(a).getCross(c.getSubtraction(a));
			normal = normal.getDivision(normal.getNorm());
		} catch (Exception e) {
			logger.info("error in calculation of normal " + this);

		}
	}

	public boolean shareVert(Triangle3D o){
		return a.equals(o.a) || a.equals(o.b) || a.equals(o.c) ||
				b.equals(o.a) || b.equals(o.b) || b.equals(o.c) ||
				c.equals(o.a) || c.equals(o.b) || c.equals(o.c);
	}

	/**
	 * the plan is defined bye ax+by+cz+d = 0, where (a, b, c) is the normal vector
	 * @param p
	 * @return
	 */
	public Point3D getElevated(Point2D p) {
		double factorD = -(normal.x*a.x+normal.y*a.y+normal.z*a.z);
		double z = -(normal.x*p.x+normal.y*p.y+factorD)/normal.z;
		return new Point3D(p.x, p.y, z);
	}
	public Point3D getElevated(Point3D p) {
		return getElevated(p.get2D());
	}

	public Triangle3D getRotationAroundZ(double angle){
		return new Triangle3D(a.get2D().getRotation(angle).get3D(a.z),
				b.get2D().getRotation(angle).get3D(b.z),
				c.get2D().getRotation(angle).get3D(c.z));
	}

	public Triangle3D getTranslation(double x, double y, double z){
		return new Triangle3D(a.getAddition(x, y, z),
				b.getAddition(x, y, z),
				c.getAddition(x, y, z));
	}
	public Triangle3D getTranslation(Point3D o){
		return new Triangle3D(a.getAddition(o),
				b.getAddition(o),
				c.getAddition(o));
	}

	@Override
	public String toString() {
		return a+"-"+b+"-"+c;
	}

	public ArrayList<Point3D> getCommonPoints(Triangle3D o){
		ArrayList<Point3D> res = new ArrayList<>();
		if(a.equals(o.a) || a.equals(o.b) || a.equals(o.c)) {
			res.add(a);
		}
		if(b.equals(o.a) || b.equals(o.b) || b.equals(o.c)) {
			res.add(b);
		}
		if(c.equals(o.a) || c.equals(o.b) || c.equals(o.c)) {
			res.add(c);
		}
		return res;
	}

	/**
	 * Elevate the given point on Z axis, for it to be on the triangle plane
	 */
	public Point3D getZProjection(Point3D p){
		Point3D A = b.getSubtraction(a);
		Point3D B = c.getSubtraction(a);
		Point3D P = p.getSubtraction(a);
		double subZ = a.z;

		double a = A.x;
		double d = A.y;
		double g = A.z;
		double b = B.x;
		double e = B.y;
		double h = B.z;
		double c = P.x;
		double f = P.y;
		double z = (b*f*g+c*d*h-c*e*g-a*f*h)/(b*d-a*e);


		return p.getAddition(0, 0, z+subZ);
	}


}
