package geometry.geom3d;

import geometry.geom2d.Point2D;

import java.util.ArrayList;
import java.util.List;


public class MyMesh {
	// TODO: make me protected and use it with getters
	public List<Point3D> vertices = new ArrayList<Point3D>();
	public List<Point2D> textCoord = new ArrayList<Point2D>();
	public List<Point3D> normals = new ArrayList<Point3D>();
	public List<Integer> indices = new ArrayList<Integer>();
}
