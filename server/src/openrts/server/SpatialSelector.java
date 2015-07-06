package openrts.server;

import geometry.geom2d.Point2D;
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


	public Geometry getGeometry(Vector3f origin, Node n) {
		Ray r = getMouseRay(origin);
		return pointer.getPointedGeometry(n, r);
	}

	public Point2D getCoord(Vector3f origin, Node n) {
		Ray r = getMouseRay(origin);
		return pointer.getPointedCoord(n, r);
	}

	public Point2D getCoord(Node n, Vector3f origin, Vector3f direction) {
		direction.subtractLocal(origin).normalizeLocal();
		Ray r = new Ray(origin, direction);
		return pointer.getPointedCoord(n, r);
	}

	public Point2D getScreenCoord(Vector3f screenCoord) {
		return Translator.toPoint3D(screenCoord).get2D();
	}

	private Ray getMouseRay(Vector3f origin) {
		Vector3f direction = new Vector3f(origin.x, origin.y, 1f);
		direction.subtractLocal(origin).normalizeLocal();
		return new Ray(origin, direction);
	}

	public long getEntityId(float originX, float originY, float originZ) {
		return getEntityId(new Vector3f(originX, originY, originZ));
	}

	public long getEntityId(Vector3f origin) {
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
