/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.ModelManager;
import view.MapView;
import view.camera.Camera;
import view.camera.IsometricCamera;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;

/**
 *
 * @author Benoît
 */
public abstract class Controller extends AbstractAppState {

	protected InputInterpreter inputInterpreter;
	public InputManager inputManager;
	public SpatialSelector spatialSelector;
	public Camera camera;
	protected Injector injector;

	protected com.jme3.renderer.Camera cam;
	
	protected ModelManager modelManager;

	protected Controller(MapView view, InputManager inputManager, com.jme3.renderer.Camera cam, Injector injector, ModelManager modelManager) {
		super();
		this.inputManager = inputManager;
		this.injector = injector;
		spatialSelector = injector.getInstance(SpatialSelector.class);
		this.cam = cam;
		this.modelManager = modelManager;

	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		inputInterpreter.unregisterInputs(inputManager);
		camera.unregisterInputs(inputManager);
	}

	@Override
	public void stateAttached(AppStateManager stateManager) {
		if (camera == null) {
			camera = new IsometricCamera(cam, 10, modelManager);
		}
		// inputInterpreter.registerInputs(inputManager);
		camera.registerInputs(inputManager);
		camera.activate();
	}
}
