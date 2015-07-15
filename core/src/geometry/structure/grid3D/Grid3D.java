package geometry.structure.grid3D;

import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import geometry.geom3d.Triangle3D;
import geometry.math.AngleUtil;
import geometry.structure.grid.Grid;

public class Grid3D<T extends Node3D> extends Grid<T> {

	public Grid3D(){
		super();
	}
	public Grid3D(int width, int height) {
		super(width, height);
	}

	private Triangle3D getTriangleAt(Point2D coord) {
		T n = get(coord);
		T nE = getEastNode(n);
		T nN = getNorthNode(n);

		if (nN == null || nE == null) {
			return null;
		}
		T nNE = getNorthNode(nE);

		Point2D nodePos2D = getCoord(n.getIndex());
		Point2D nodeNEPos2D = getCoord(nNE.getIndex());

		Point3D nw = getPos(nN);
		Point3D ne = getPos(nNE);
		Point3D sw = getPos(n);
		Point3D se = getPos(nE);
		Triangle3D tr;

		if (AngleUtil.getTurn(nodePos2D, nodeNEPos2D, coord) == AngleUtil.CLOCKWISE) {
			tr = new Triangle3D(sw, se, ne);
		} else {
			tr = new Triangle3D(sw, ne, nw);
		}
		return tr;
	}

	public double getAltitudeAt(Point2D coord) {
		Triangle3D tr = getTriangleAt(coord);
		return tr == null ? 0 : tr.getElevated(coord).z;
	}

	public Point3D getNormalVectorAt(Point2D coord) {
		Triangle3D tr = getTriangleAt(coord);
		return tr == null ? Point3D.UNIT_Z : tr.normal;
	}


	public Point3D getPos(T n){
		return getCoord(n.getIndex()).get3D(n.getElevation());
	}
}
