/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.ground;

import java.util.logging.Logger;

import openrts.guice.annotation.InputManagerRef;
import view.EditorView;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;

import controller.Controller;
import controller.cameraManagement.GroundCameraManager;

/**
 *
 * @author Benoît
 */
public class GroundController extends Controller {

	private static final Logger logger = Logger.getLogger(GroundController.class.getName());

	protected GroundInputInterpreter inputInterpreter;
	protected GroundGUIController guiController;

	@Inject
	public GroundController(@Named("EditorView") EditorView view, @Named("GroundGUIController") GroundGUIController guiController,
			@InputManagerRef InputManager inputManager,
			@Named("Camera") Camera cam, @Named("GroundInputInterpreter") GroundInputInterpreter inputInterpreter) {
		super(view, inputManager, cam);

		this.inputInterpreter = inputInterpreter;
		cameraManager = new GroundCameraManager(cam);
		this.guiController = guiController;
	}


	@Override
	public void update(float elapsedTime) {
	}

	@Override
	public void stateAttached(AppStateManager stateManager) {
		super.stateAttached(stateManager);
		inputManager.setCursorVisible(false);
		guiController.activate();
		logger.info("ground controller on line");
	}


	@Override
	public void stateDetached(AppStateManager stateManager) {
		inputInterpreter.unregisterInputs(inputManager);
		cameraManager.unregisterInputs(inputManager);
	}

}
