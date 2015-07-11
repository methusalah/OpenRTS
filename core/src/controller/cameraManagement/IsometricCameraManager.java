package controller.cameraManagement;

import geometry.geom3d.Point3D;
import model.ModelManager;
import view.math.TranslateUtil;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public class IsometricCameraManager extends CameraManager {
	protected final static String STRAFE_NORTH = "strafenorth";
	protected final static String STRAFE_SOUTH = "strafesouth";
	protected final static String STRAFE_EAST = "strafeeast";
	protected final static String STRAFE_WEST = "strafewest";
	protected final static String ZOOM_IN = "zoomin";
	protected final static String ZOOM_OUT = "zoomout";

	private double maxSpeed = 10;
	private Point3D pos;
	private Point3D target;

	public IsometricCameraManager(Camera cam, float elevation) {
		super(cam);
		pos = new Point3D(0, 0, elevation);
		target = new Point3D(0, elevation*2/3, 0);
		placeCam();
		setMappings();
	}

	private void setMappings(){
		mappings = new String[]{
				STRAFE_NORTH,
				STRAFE_SOUTH,
				STRAFE_EAST,
				STRAFE_WEST,
				ZOOM_IN,
				ZOOM_OUT,
		};
	}

	@Override
	public void activate() {
		placeCam();
	}

	@Override
	public void desactivate() {
	}

	private void placeCam(){
		cam.setLocation(TranslateUtil.toVector3f(pos));
		cam.lookAt(TranslateUtil.toVector3f(target), Vector3f.UNIT_Z);
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
		inputManager.addMapping(STRAFE_NORTH, new KeyTrigger(KeyInput.KEY_UP));
		inputManager.addMapping(STRAFE_SOUTH, new KeyTrigger(KeyInput.KEY_DOWN));
		inputManager.addMapping(STRAFE_EAST, new KeyTrigger(KeyInput.KEY_RIGHT));
		inputManager.addMapping(STRAFE_WEST, new KeyTrigger(KeyInput.KEY_LEFT));
		inputManager.addMapping(ZOOM_IN, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
		inputManager.addMapping(ZOOM_OUT, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
		inputManager.addListener(this, mappings);
	}

	public void move(double x, double y){
		if (ModelManager.getBattlefield().getMap().isInBounds(target.getAddition(x, y, 0).get2D())) {
			pos = pos.getAddition(x, y, 0);
			target = target.getAddition(x, y, 0);
			placeCam();
		}
	}

	protected void zoom(double value){
		pos = pos.getAddition(0, value, -value);
		placeCam();
	}

	@Override
	public void onAnalog(String name, float value, float tpf) {
		double velocity = tpf*maxSpeed*1;
		switch(name){
			case STRAFE_NORTH : move(0, velocity); break;
			case STRAFE_SOUTH : move(0, -velocity); break;
			case STRAFE_EAST : move(velocity, 0); break;
			case STRAFE_WEST : move(-velocity, 0); break;
			case ZOOM_IN : zoom(1); break;
			case ZOOM_OUT : zoom(-1); break;
		}
	}

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {

	}
}
