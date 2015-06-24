package geometry.structure.grid3D;

import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import geometry.geom3d.Triangle3D;
import geometry.math.Angle;
import geometry.structure.grid.Grid;

public class Grid3D extends Grid {

	public Grid3D(int width, int height) {
		super(width, height);
	}
	
	private Triangle3D getTriangleAt(Point2D coord) {
		Node3D n = get(coord);
		if (n.n() == null || n.e() == null) {
			return null;
		}
	
		Point2D nodePos2D = getCoord(n.index);
		Point2D nodeNEPos2D = getCoord(n.e().n().index);
	
		Point3D nw = getPos((Node3D)n.n());
		Point3D ne = getPos((Node3D)n.n().e());
		Point3D sw = getPos((Node3D)n);
		Point3D se = getPos((Node3D)n.e());
		Triangle3D tr;
	
		if (Angle.getTurn(nodePos2D, nodeNEPos2D, coord) == Angle.CLOCKWISE) {
			tr = new Triangle3D(sw, se, ne);
		} else {
			tr = new Triangle3D(sw, ne, nw);
		}
		return tr;
	}

	public double getAltitudeAt(Point2D coord) {
		Triangle3D tr = getTriangleAt(coord);
		return tr == null ? 0 : getTriangleAt(coord).getElevated(coord).z;
	}

	public Point3D getNormalVectorAt(Point2D coord) {
		Triangle3D tr = getTriangleAt(coord);
		return tr == null ? Point3D.UNIT_Z : tr.normal;
	}
	

	public Point3D getPos(Node3D n){
		return getCoord(n.index).get3D(n.elevation);
	}

}
