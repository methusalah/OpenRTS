package geometry.geom3d;

import geometry.geom2d.Point2D;

import java.util.ArrayList;


public class MyMesh {

	public ArrayList<Point3D> vertices = new ArrayList<Point3D>();
	public ArrayList<Point2D> textCoord = new ArrayList<Point2D>();
	public ArrayList<Point3D> normals = new ArrayList<Point3D>();
	public ArrayList<Integer> indices = new ArrayList<Integer>();
}
