/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.ground;

import java.util.logging.Logger;

import view.EditorView;

import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;

import controller.Controller;
import controller.cameraManagement.GroundCameraManager;
import de.lessvoid.nifty.Nifty;

/**
 *
 * @author Benoît
 */
public class GroundController extends Controller {

	private static final Logger logger = Logger.getLogger(GroundController.class.getName());

	public GroundController(EditorView view, Nifty nifty, InputManager inputManager, Camera cam) {
		super(view, inputManager, cam);

		inputInterpreter = new GroundInputInterpreter(this);
		cameraManager = new GroundCameraManager(cam);
		guiController = new GroundGUIController(nifty, this);
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



}
