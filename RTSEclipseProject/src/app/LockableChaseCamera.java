package app;

import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

public class LockableChaseCamera extends ChaseCamera {

	public LockableChaseCamera(Camera cam, Spatial target, InputManager inputManager) {
		super(cam, target, inputManager);
	}
	
	public void lock() {
        inputManager.deleteMapping(ChaseCamMoveLeft);
        inputManager.deleteMapping(ChaseCamMoveRight);
        inputManager.deleteMapping(ChaseCamUp);
        inputManager.deleteMapping(ChaseCamDown);
        inputManager.deleteMapping(ChaseCamZoomIn);
        inputManager.deleteMapping(ChaseCamZoomOut);
        inputManager.deleteMapping(ChaseCamToggleRotate);
        inputManager.setCursorVisible(true);
	}
	
	public void unLock() {
		String[] inputs = {ChaseCamToggleRotate,
	            ChaseCamDown,
	            ChaseCamUp,
	            ChaseCamMoveLeft,
	            ChaseCamMoveRight,
	            ChaseCamZoomIn,
	            ChaseCamZoomOut};

	        if (!invertYaxis) {
	            inputManager.addMapping(ChaseCamDown, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
	            inputManager.addMapping(ChaseCamUp, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
	        } else {
	            inputManager.addMapping(ChaseCamDown, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
	            inputManager.addMapping(ChaseCamUp, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
	        }
	        inputManager.addMapping(ChaseCamZoomIn, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
	        inputManager.addMapping(ChaseCamZoomOut, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
	        if(!invertXaxis){
	            inputManager.addMapping(ChaseCamMoveLeft, new MouseAxisTrigger(MouseInput.AXIS_X, true));
	            inputManager.addMapping(ChaseCamMoveRight, new MouseAxisTrigger(MouseInput.AXIS_X, false));
	        }else{
	            inputManager.addMapping(ChaseCamMoveLeft, new MouseAxisTrigger(MouseInput.AXIS_X, false));
	            inputManager.addMapping(ChaseCamMoveRight, new MouseAxisTrigger(MouseInput.AXIS_X, true));
	        }
	        inputManager.addMapping(ChaseCamToggleRotate, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
	        inputManager.addMapping(ChaseCamToggleRotate, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

	        inputManager.addListener(this, inputs);
	        
	        if(!isDragToRotate())
	        	inputManager.setCursorVisible(false);
	        	
	        
	}
	
	public void setCanRotate(boolean value){
		canRotate = value;
	}

	public Vector3f getDirection() {
		return cam.getDirection();
	}

}
