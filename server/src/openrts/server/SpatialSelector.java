package openrts.server;

import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import model.EntityManager;
import view.Pointer;
import view.acting.ModelPerformer;
import view.math.Translator;

import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class SpatialSelector {

	private Pointer pointer;
	private Node rootNode;

	public SpatialSelector(Node rootNode) {
		pointer = new Pointer();
		this.rootNode = rootNode;
	}


	public Geometry getGeometry(Point3D origin, Node n) {
		Ray r = getMouseRay(origin);
		return pointer.getPointedGeometry(n, r);
	}

	public Point2D getCoord(Point3D origin, Node n) {
		Ray r = getMouseRay(origin);
		return pointer.getPointedCoord(n, r);
	}

	public Point2D getCoord(Node n, Vector3f origin, Vector3f direction) {
		direction.subtractLocal(origin).normalizeLocal();
		Ray r = new Ray(origin, direction);
		return pointer.getPointedCoord(n, r);
	}

	public Point2D getScreenCoord(Point3D screenCoord) {
		return Translator.toPoint3D(screenCoord).get2D();
	}

	private Ray getMouseRay(Point3D origin) {
		Point3D direction = new Point3D(origin.x, origin.y, 1f);
		direction.subtractLocal(origin).normalizeLocal();
		return new Ray(origin, direction);
	}

	public long getEntityId(float originX, float originY, float originZ) {
		return getEntityId(new Point3D(originX, originY, originZ));
	}

	public long getEntityId(Point3D origin) {
		Spatial s = getGeometry(origin, rootNode);
		while (s != null && s.getName() != null) {
			Object o = s.getUserData(ModelPerformer.ENTITYID_USERDATA);
			if (o != null && EntityManager.isValidId((long) o)) {
				return (long) o;
			}
			s = s.getParent();
		}
		return EntityManager.NOT_VALID_ID;
	}

}
