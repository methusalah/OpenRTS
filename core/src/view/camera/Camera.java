/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.camera;

import geometry.geom2d.Point2D;

import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;

/**
 *
 * @author Benoît
 */
public abstract class Camera implements AnalogListener, ActionListener{
    protected final com.jme3.renderer.Camera cam;
    
    protected String[] mappings;

    public Camera(com.jme3.renderer.Camera cam) {
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
