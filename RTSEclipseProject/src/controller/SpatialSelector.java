package controller;

import geometry.Point2D;
import geometry3D.Point3D;
import view.View;
import view.math.Translator;

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
	
	public Point2D getCoord(Node n, Point2D screenCoord) {
		Vector3f origin = cam.getWorldCoordinates(Translator.toVector2f(screenCoord), 0f);
		Vector3f direction = cam.getWorldCoordinates(Translator.toVector2f(screenCoord), 1f);
		direction.subtractLocal(origin).normalizeLocal();
		Ray r = new Ray(origin, direction);
        return view.pointer.getPointedCoord(n, r);
	}
	
	public Point2D getScreenCoord(Point3D pos){
    	Vector3f vPos = Translator.toVector3f(pos);
    	Vector3f screenCoord = cam.getScreenCoordinates(vPos);
    	return Translator.toPoint3D(screenCoord).get2D();
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
        
    public String getSpatialLabel(){
        Spatial s = getGeometry(view.rootNode);
        while(true){
            if(s == null || s.getName() == null)
                return null;
            if(s.getName().startsWith("label"))
                return s.getName();
            s = s.getParent();
        }
    }

}
