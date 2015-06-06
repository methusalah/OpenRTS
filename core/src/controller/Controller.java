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

/**
 *
 * @author Benoît
 */
public abstract class Controller extends AbstractAppState {

	protected InputInterpreter inputInterpreter;
	public InputManager inputManager;
	public SpatialSelector spatialSelector;
	public CameraManager cameraManager;
	public GUIController guiController;


	public Controller(MapView view, InputManager inputManager, Camera cam) {
		super();
		this.inputManager = inputManager;
		spatialSelector = new SpatialSelector(cam, inputManager, view);

	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		inputInterpreter.unregisterInputs(inputManager);
		cameraManager.unregisterInputs(inputManager);
	}

	@Override
	public void stateAttached(AppStateManager stateManager) {
		inputInterpreter.registerInputs(inputManager);
		cameraManager.registerInputs(inputManager);
		cameraManager.activate();
	}
}
