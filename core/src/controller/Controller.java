/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import view.MapView;

import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;

import controller.cameraManagement.CameraManager;
import controller.cameraManagement.IsometricCameraManager;

/**
 *
 * @author Benoît
 */
public abstract class Controller extends AbstractAppState {

	// protected InputInterpreter inputInterpreter;
	public InputManager inputManager;
	public SpatialSelector spatialSelector;
	public CameraManager cameraManager;

	protected Camera cam;
	// public GUIController guiController;

	public Controller(MapView view, InputManager inputManager, Camera cam) {
		super();
		this.inputManager = inputManager;
		spatialSelector = new SpatialSelector(cam, inputManager, view);
		this.cam = cam;

	}

	@Override
	abstract public void stateDetached(AppStateManager stateManager);

	@Override
	public void stateAttached(AppStateManager stateManager) {
		if (cameraManager == null) {
			cameraManager = new IsometricCameraManager(cam, 10);
		}
		// inputInterpreter.registerInputs(inputManager);
		cameraManager.registerInputs(inputManager);
		cameraManager.activate();
	}
}
