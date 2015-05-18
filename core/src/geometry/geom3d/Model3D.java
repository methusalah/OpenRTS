package geometry.geom3d;

import java.util.ArrayList;
import java.util.List;

public class Model3D {

	public List<MyMesh> meshes = new ArrayList<MyMesh>();
	public List<String> textures = new ArrayList<String>();
	public boolean glow = false;
	public double rotationYZ;
	public double rotationXZ;
	public Point3D pos;
}
