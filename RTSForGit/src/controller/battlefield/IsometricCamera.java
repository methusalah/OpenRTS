package controller.battlefield;

import tools.LogUtil;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public class IsometricCamera implements AnalogListener, ActionListener{

    private float elevation;
    private float moveSpeed = 10;
    private double rotationSpeed = 3;
    private boolean enabled = true;
    private InputManager inputManager;
    private Camera cam;
    String[] mappings;

	
    public IsometricCamera(Camera cam, float elevation){
        this.cam = cam;
        this.elevation = elevation;
        this.cam.setLocation(new Vector3f(0, 0, elevation));
        this.cam.lookAt(new Vector3f(0, elevation*2/3, 0), new Vector3f(0, 0, 1));
    }

	/**
     * Sets the move speed. The speed is given in world units per second.
     * @param moveSpeed
     */
    public void setMoveSpeed(float moveSpeed){
        this.moveSpeed = moveSpeed;
    }

    /**
     * Sets the rotation speed.
     * @param rotationSpeed
     */
    public void setRotationSpeed(float rotationSpeed){
        this.rotationSpeed = rotationSpeed;
    }

    /**
     * @param enable If false, the camera will ignore input.
     */
    public void setEnabled(boolean enable){
       	enabled = enable;
    }
    
    public void unregisterInput(InputManager inputManager){
        if (inputManager == null)
            return;
    
        for (String s : mappings)
            if (inputManager.hasMapping(s))
                inputManager.deleteMapping( s );

        inputManager.removeListener(this);
    }

    /**
     * @return If enabled
     * @see FlyByCamera#setEnabled(boolean)
     */
    public boolean isEnabled(){
        return enabled;
    }

    /**
     * Registers the FlyByCamera to receive input events from the provided
     * Dispatcher.
     * @param dispacher
     */
    public void registerWithInput(InputManager inputManager){

        this.inputManager = inputManager;
        
        mappings = new String[]{
       		"StrafeNorth",
       		"StrafeSouth",
            "StrafeEast",
            "StrafeWest",
            "ZoomIn",
            "ZoomOut",
        };

        inputManager.addMapping("StrafeNorth", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("StrafeSouth", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("StrafeEast", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("StrafeWest", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("ZoomIn", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("ZoomOut", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));

        inputManager.addListener(this, mappings);
    }
    
    protected void move(float x, float y){
        cam.setLocation(new Vector3f(cam.getLocation().x+x, cam.getLocation().y+y, cam.getLocation().z));
    }

    protected void zoom(float value){
        cam.setLocation(new Vector3f(cam.getLocation().x, cam.getLocation().y+value, cam.getLocation().z-value));
    }

    public void onAnalog(String name, float value, float tpf) {
        if (!enabled)
            return;

        if (name.equals("StrafeNorth")){
            move(0, value*moveSpeed);
        }else if (name.equals("StrafeSouth")){
            move(0, -value*moveSpeed);
        }else if (name.equals("StrafeEast")){
            move(value*moveSpeed, 0);
        }else if (name.equals("StrafeWest")){
            move(-value*moveSpeed, 0);
        }else if (name.equals("ZoomIn")){
            zoom(value);
        }else if (name.equals("ZoomOut")){
            zoom(-value);
        }
    }

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		// TODO Auto-generated method stub
		
	}

}
