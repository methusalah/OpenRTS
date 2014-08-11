package controller;

import geometry.Point2D;
import view.View;

import com.jme3.input.InputManager;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

public class SpatialSelector {

	Camera cam;
	InputManager im;
	View view;
	public boolean centered = false;
	
	public SpatialSelector(Camera c, InputManager im, View v) {
		cam = c;
		this.im = im;
		view = v;
	}
	
	public Geometry getGeometry(Node n) {
		Ray r;
		if(centered)
			r = getCameraRay();
		else 
			r = getMouseRay();
                return view.pointer.getPointedGeometry(n, r);
	}
	
	public Point2D getCoord(Node n) {
		Ray r;
		if(centered)
			r = getCameraRay();
		else 
			r = getMouseRay();
                return view.pointer.getPointedCoord(n, r);
	}
	
	private Ray getMouseRay(){
		Vector3f origin = cam.getWorldCoordinates(im.getCursorPosition(), 0f);
		Vector3f direction = cam.getWorldCoordinates(im.getCursorPosition(), 1f);
		direction.subtractLocal(origin).normalizeLocal();
		return new Ray(origin, direction);
	}
	
	private Ray getCameraRay(){
		return new Ray(cam.getLocation(), cam.getDirection());
	}
}
