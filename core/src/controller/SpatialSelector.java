package controller;

import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import model.EntityManager;
import view.MapView;
import view.PointUtil;
import view.acting.ModelPerformer;
import view.math.TranslateUtil;

import com.jme3.input.InputManager;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class SpatialSelector {

	Camera cam;
	InputManager im;
	MapView view;
	public boolean centered = false;

	public SpatialSelector(Camera c, InputManager im, MapView v) {
		cam = c;
		this.im = im;
		view = v;
	}

	public Geometry getGeometry(Node n) {
		Ray r;
		if (centered) {
			r = getCameraRay();
		} else {
			r = getMouseRay();
		}
		return PointUtil.getPointedGeometry(n, r);
	}

	public Point2D getCoord(Node n) {
		Ray r;
		if (centered) {
			r = getCameraRay();
		} else {
			r = getMouseRay();
		}
		return PointUtil.getPointedCoord(n, r);
	}

	public Point2D getCenterViewCoord(Node n) {
		return PointUtil.getPointedCoord(n, getCameraRay());
	}

	public Point2D getCoord(Node n, Point2D screenCoord) {
		Vector3f origin = cam.getWorldCoordinates(TranslateUtil.toVector2f(screenCoord), 0f);
		Vector3f direction = cam.getWorldCoordinates(TranslateUtil.toVector2f(screenCoord), 1f);
		direction.subtractLocal(origin).normalizeLocal();
		Ray r = new Ray(origin, direction);
		return PointUtil.getPointedCoord(n, r);
	}

	public Point2D getScreenCoord(Point3D pos) {
		Vector3f vPos = TranslateUtil.toVector3f(pos);
		Vector3f screenCoord = cam.getScreenCoordinates(vPos);
		return TranslateUtil.toPoint3D(screenCoord).get2D();
	}

	private Ray getMouseRay() {
		Vector3f origin = cam.getWorldCoordinates(im.getCursorPosition(), 0f);
		Vector3f direction = cam.getWorldCoordinates(im.getCursorPosition(), 1f);
		direction.subtractLocal(origin).normalizeLocal();
		return new Ray(origin, direction);
	}

	private Ray getCameraRay() {
		return new Ray(cam.getLocation(), cam.getDirection());
	}

	public String getSpatialLabel() {
		Spatial s = getGeometry(view.getRootNode());
		while (s != null && s.getName() != null) {
			if (s.getName().startsWith("label")) {
				return s.getName();
			}
			s = s.getParent();
		}
		return null;
	}

	public long getEntityId() {
		Spatial s = getGeometry(view.getRootNode());
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
