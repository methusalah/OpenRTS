/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.editor;

import event.BattleFieldUpdateEvent;
import event.EventManager;
import geometry.geom2d.Point2D;
import model.ModelManager;
import model.editor.ToolManager;
import view.EditorView;
import view.camera.IsometricCamera;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;

import controller.Controller;

/**
 *
 * @author Benoît
 */
public class EditorController extends Controller {
	protected Point2D screenCoord;

	protected EditorView view;

	protected EditorGUIController guiController;

	// protected EditorInputInterpreter inputInterpreter;

	@Inject
	public EditorController(EditorView view, @Named("EditorGUIController") EditorGUIController guiController, InputManager inputManager, @Named("Camera") Camera cam,
			@Named("EditorInputInterpreter") EditorInputInterpreter inputInterpreter) {
		super(view, inputManager, cam);
		this.view = view;
		this.guiController = guiController;
		this.inputInterpreter = inputInterpreter;
	}

	@Override
	public void update(float elapsedTime) {
		//        screenCoord = Translator.toPoint2D(im.getCursorPosition());
		ToolManager.setPointedSpatialLabel(spatialSelector.getSpatialLabel());
		ToolManager.setPointedSpatialEntityId(spatialSelector.getEntityId());
		if(view.editorRend != null){
			Point2D coord = spatialSelector.getCoord(view.editorRend.gridNode);
			if (coord != null &&
					ModelManager.battlefieldReady &&
					ModelManager.getBattlefield().getMap().isInBounds(coord)) {
				ToolManager.updatePencilsPos(coord);
				view.editorRend.drawPencil();
			}
		}

		guiController.update();
	}

	@Override
	public void stateAttached(AppStateManager stateManager) {
		super.stateAttached(stateManager);
		inputManager.setCursorVisible(true);
		//		view.getRootNode().attachChild(view.editorRend.mainNode);
		guiController.activate();
		if (ModelManager.getBattlefield() != null) {
			ModelManager.getBattlefield().getEngagement().reset();
		}
		inputInterpreter.registerInputs(inputManager);

		spatialSelector.setCentered(false);
		EventManager.register(this);
	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		ModelManager.getBattlefield().getEngagement().save();
		super.stateDetached(stateManager);
		view.getRootNode().detachChild(view.editorRend.mainNode);
		EventManager.unregister(this);
	}

	@Subscribe
	public void manageEvent(BattleFieldUpdateEvent ev) {
		((IsometricCamera)camera).move(ModelManager.getBattlefield().getMap().xSize() / 2, ModelManager.getBattlefield().getMap().ySize() / 2);
	}

}
