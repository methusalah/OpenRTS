/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.editor;

import geometry.geom2d.Point2D;
import model.Commander;
import model.Model;
import view.View;

import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;

import controller.Controller;
import controller.cameraManagement.IsometricCameraManager;
import de.lessvoid.nifty.Nifty;

/**
 *
 * @author Benoît
 */
public class EditorController extends Controller {
	Point2D screenCoord;

	public EditorController(View view, Nifty nifty, InputManager inputManager, Camera cam) {
		super(view, inputManager, cam);

		inputInterpreter = new EditorInputInterpreter(this);
		guiController = new EditorGUIController(nifty, this);

		Commander.registerListener(this);

		cameraManager = new IsometricCameraManager(cam, 10);
	}

	@Override
	public void update(float elapsedTime) {
		//        screenCoord = Translator.toPoint2D(im.getCursorPosition());
		Model.toolManager.setPointedSpatialLabel(spatialSelector.getSpatialLabel());
		Model.toolManager.setPointedSpatialEntityId(spatialSelector.getEntityId());
		Point2D coord = spatialSelector.getCoord(view.editorRend.gridNode);
		if (coord != null && Model.battlefield.map.isInBounds(coord)) {
			Model.toolManager.updatePencilsPos(coord);
			view.editorRend.drawPencil();
		}

		guiController.update();
	}

	@Override
	public void manageEvent() {
	}

	@Override
	public void stateAttached(AppStateManager stateManager) {
		super.stateAttached(stateManager);
		inputManager.setCursorVisible(true);
		view.rootNode.attachChild(view.editorRend.mainNode);
		guiController.activate();
		Model.battlefield.engagement.resetEngagement();
	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		Model.battlefield.engagement.saveEngagement();
		Model.battlefield.map.prepareForBattle();
		super.stateDetached(stateManager);
		view.rootNode.detachChild(view.editorRend.mainNode);
	}



}
