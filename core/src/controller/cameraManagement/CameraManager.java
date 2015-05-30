/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.cameraManagement;

import geometry.geom2d.Point2D;

import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.renderer.Camera;

/**
 *
 * @author Benoît
 */
public abstract class CameraManager implements AnalogListener, ActionListener{
    protected final Camera cam;
    
    protected String[] mappings;

    public CameraManager(Camera cam) {
        this.cam = cam;
    }
    
    public Point2D getCamCorner(){
    	return new Point2D(cam.getWidth(), cam.getHeight());
    }
    
    public abstract void unregisterInputs(InputManager inputManager);
    public abstract void registerInputs(InputManager inputManager);
    public abstract void activate();
    public abstract void desactivate();
    
}
