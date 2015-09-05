/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.ground;

import java.util.logging.Logger;

import model.ModelManager;
import view.EditorView;
import view.camera.GroundCamera;
import brainless.openrts.event.EventManager;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;

import controller.Controller;

/**
 *
 * @author Benoît
 */
public class GroundController extends Controller {

	private static final Logger logger = Logger.getLogger(GroundController.class.getName());

	protected GroundInputInterpreter inputInterpreter;
	protected GroundGUIController guiController;

	@Inject
	public GroundController(EditorView view, @Named("GroundGUIController") GroundGUIController guiController, InputManager inputManager,
			Camera cam, @Named("GroundInputInterpreter") GroundInputInterpreter inputInterpreter, Injector injector, ModelManager modelManager) {
		super(view, inputManager, cam,injector,modelManager);
		this.inputInterpreter = inputInterpreter;
		this.guiController = guiController;
	}


	@Override
	public void update(float elapsedTime) {
	}

	@Override
	public void stateAttached(AppStateManager stateManager) {
		camera = new GroundCamera(cam);
		super.stateAttached(stateManager);
		inputManager.setCursorVisible(false);
		guiController.activate();
		inputInterpreter.registerInputs(inputManager);
		logger.info("ground controller on line");
	}


	@Override
	public void stateDetached(AppStateManager stateManager) {
		inputInterpreter.unregisterInputs(inputManager);
		camera.unregisterInputs(inputManager);
		EventManager.unregister(this);
	}

}
