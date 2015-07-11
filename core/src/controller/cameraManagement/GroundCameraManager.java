package controller.cameraManagement;


import geometry.geom3d.Point3D;
import geometry.math.AngleUtil;

import java.util.logging.Logger;

import model.ModelManager;
import view.math.TranslateUtil;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public class GroundCameraManager extends CameraManager {

	private static final Logger logger = Logger.getLogger(GroundCameraManager.class.getName());
	protected final static String ROTATE_LEFT = "rotateleft";
	protected final static String ROTATE_RIGHT = "rotateright";
	protected final static String ROTATE_UP = "rotateup";
	protected final static String ROTATE_DOWN = "rotatedown";
	protected final static String STRAFE_LEFT = "strafeleft";
	protected final static String STRAFE_RIGHT = "straferight";
	protected final static String MOVE_FOREWARD = "moveforeward";
	protected final static String MOVE_BACKWARD = "movebackward";

	private double maxSpeed = 5;
	private double maxRotSpeed = 100;
	private Point3D pos;
	private Quaternion rotation;

	public GroundCameraManager(Camera cam) {
		super(cam);
		pos = new Point3D(1, 1, 0);
		rotation = new Quaternion().fromAngles((float)AngleUtil.RIGHT, 0, (float)AngleUtil.FLAT);
		cam.setFrustumPerspective(45, (float)cam.getWidth()/cam.getHeight(), 0.01f, 1000);

		applyRotationToCam();
		setMappings();
	}

	private void setMappings(){
		mappings = new String[]{
				ROTATE_UP,
				ROTATE_DOWN,
				ROTATE_LEFT,
				ROTATE_RIGHT,

				STRAFE_LEFT,
				STRAFE_RIGHT,
				MOVE_FOREWARD,
				MOVE_BACKWARD,
		};
	}

	private void placeCam(){
		if (ModelManager.getBattlefield().getMap().isInBounds(pos.get2D())) {
			pos.z = ModelManager.getBattlefield().getMap().getAltitudeAt(pos.get2D()) + 0.5;
		} else {
			pos.z = 0;
		}
		cam.setLocation(TranslateUtil.toVector3f(pos));
	}

	@Override
	public void activate() {
		placeCam();
		applyRotationToCam();
		//        cam.setFrustumPerspective(50, 1600/850, 0.1f, 100);
	}

	@Override
	public void desactivate() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}



	@Override
	public void unregisterInputs(InputManager inputManager){
		for(String s : mappings) {
			if(inputManager.hasMapping(s)) {
				inputManager.deleteMapping(s);
			}
		}
		inputManager.removeListener(this);
	}

	@Override
	public void registerInputs(InputManager inputManager){
		inputManager.addMapping(ROTATE_UP, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
		inputManager.addMapping(ROTATE_DOWN, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
		inputManager.addMapping(ROTATE_LEFT, new MouseAxisTrigger(MouseInput.AXIS_X, true));
		inputManager.addMapping(ROTATE_RIGHT, new MouseAxisTrigger(MouseInput.AXIS_X, false));

		inputManager.addMapping(STRAFE_LEFT, new KeyTrigger(KeyInput.KEY_Q),
				new KeyTrigger(KeyInput.KEY_LEFT));
		inputManager.addMapping(STRAFE_RIGHT, new KeyTrigger(KeyInput.KEY_D),
				new KeyTrigger(KeyInput.KEY_RIGHT));
		inputManager.addMapping(MOVE_FOREWARD, new KeyTrigger(KeyInput.KEY_Z),
				new KeyTrigger(KeyInput.KEY_UP));
		inputManager.addMapping(MOVE_BACKWARD, new KeyTrigger(KeyInput.KEY_S),
				new KeyTrigger(KeyInput.KEY_DOWN));
		inputManager.addListener(this, mappings);
	}

	@Override
	public void onAnalog(String name, float value, float tpf) {
		double velocity = tpf*maxSpeed*1;
		switch(name){
			case ROTATE_UP : changeRotation(-value, getLeft()); break;
			case ROTATE_DOWN : changeRotation(value, getLeft()); break;
			case ROTATE_LEFT : changeRotation(value, Point3D.UNIT_Z); break;
			case ROTATE_RIGHT : changeRotation(-value, Point3D.UNIT_Z); break;

			case STRAFE_LEFT : move(getLeft(), velocity); break;
			case STRAFE_RIGHT : move(getRight(), velocity); break;
			case MOVE_FOREWARD : move(getDir(), velocity); break;
			case MOVE_BACKWARD : move(getRear(), velocity); break;
		}
	}

	private void move(Point3D vec, double distance){
		pos = pos.getAddition(vec.getScaled(distance));
		placeCam();
	}

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {

	}

	protected void changeRotation(float value, Point3D axis){
		Matrix3f mat = new Matrix3f();
		mat.fromAngleNormalAxis(value, TranslateUtil.toVector3f(axis));

		Vector3f up = cam.getUp();
		Vector3f left = cam.getLeft();
		Vector3f dir = cam.getDirection();

		mat.mult(up, up);
		mat.mult(left, left);
		mat.mult(dir, dir);

		rotation = new Quaternion();
		rotation.fromAxes(left, up, dir);
		rotation.normalizeLocal();

		applyRotationToCam();
	}

	private void applyRotationToCam(){
		cam.setAxes(rotation);
	}


	private Point3D getLeft(){
		return TranslateUtil.toPoint3D(cam.getLeft());
	}
	private Point3D getRight(){
		return TranslateUtil.toPoint3D(cam.getLeft()).getNegation();
	}
	private Point3D getDir(){
		return TranslateUtil.toPoint3D(cam.getDirection());
	}
	private Point3D getRear(){
		return TranslateUtil.toPoint3D(cam.getDirection()).getNegation();
	}
}
