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
import openrts.guice.annotation.InputManagerRef;
import view.EditorView;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;

import controller.Controller;
import controller.cameraManagement.IsometricCameraManager;

/**
 *
 * @author Benoît
 */
public class EditorController extends Controller {
	protected Point2D screenCoord;

	protected EditorView view;

	@Inject
	public EditorController(@Named("EditorView") EditorView view, @Named("NiftyJmeDisplay") NiftyJmeDisplay niftyDisplay,
			@InputManagerRef InputManager inputManager,
			@Named("Camera") Camera cam) {
		super(view, inputManager, cam);
		this.view = view;
		guiController = new EditorGUIController(niftyDisplay.getNifty(), this);
		cameraManager = new IsometricCameraManager(cam, 10);
		spatialSelector.setCentered(false);
		EventManager.register(this);
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
	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		ModelManager.getBattlefield().getEngagement().save();
		// super.stateDetached(stateManager);
		view.getRootNode().detachChild(view.editorRend.mainNode);
	}

	@Subscribe
	public void manageEvent(BattleFieldUpdateEvent ev) {
		((IsometricCameraManager)cameraManager).move(ModelManager.getBattlefield().getMap().xSize() / 2, ModelManager.getBattlefield().getMap().ySize() / 2);
	}

}
