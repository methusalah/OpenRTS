/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.editor;

import geometry.geom2d.Point2D;
import model.ModelManager;
import model.battlefield.army.ArmyManager;
import model.builders.entity.definitions.BuilderManager;
import model.editor.ToolManager;
import view.EditorView;
import view.camera.IsometricCamera;
import brainless.openrts.event.BattleFieldUpdateEvent;
import brainless.openrts.event.EventManager;

import com.google.common.eventbus.Subscribe;
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
public class EditorController extends Controller {
	protected Point2D screenCoord;

	protected EditorView view;

	protected EditorGUIController guiController;

	// protected EditorInputInterpreter inputInterpreter;

//	@Inject
//	private ModelManager modelManager;
	
	@Inject
	private ArmyManager armyManager;
	
	@Inject
	private BuilderManager builderManager;
	
	@Inject
	private ToolManager toolManager;
	
	@Inject
	public EditorController(EditorView view, @Named("EditorGUIController") EditorGUIController guiController, InputManager inputManager, Camera cam,
			@Named("EditorInputInterpreter") EditorInputInterpreter inputInterpreter, Injector injector, ModelManager modelManager) {
		super(view, inputManager, cam,injector, modelManager);
		this.view = view;
		this.guiController = guiController;
		this.inputInterpreter = inputInterpreter;
	}

	@Override
	public void update(float elapsedTime) {
		//        screenCoord = Translator.toPoint2D(im.getCursorPosition());
		toolManager.setPointedSpatialLabel(spatialSelector.getSpatialLabel());
		toolManager.setPointedSpatialEntityId(spatialSelector.getEntityId());
		if(view.editorRend != null){
			Point2D coord = spatialSelector.getCoord(view.editorRend.gridNode);
			if (coord != null &&
					ModelManager.battlefieldReady &&
					modelManager.getBattlefield().getMap().isInBounds(coord)) {
				toolManager.updatePencilsPos(coord);
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
		if (modelManager.getBattlefield() != null) {
			modelManager.getBattlefield().getEngagement().reset(armyManager, builderManager);
		}
		inputInterpreter.registerInputs(inputManager);

		spatialSelector.setCentered(false);
		EventManager.register(this);
	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		modelManager.getBattlefield().getEngagement().save(armyManager);
		super.stateDetached(stateManager);
		view.getRootNode().detachChild(view.editorRend.mainNode);
		EventManager.unregister(this);
	}

	@Subscribe
	public void manageEvent(BattleFieldUpdateEvent ev) {
		((IsometricCamera)camera).move(modelManager.getBattlefield().getMap().xSize() / 2, modelManager.getBattlefield().getMap().ySize() / 2);
	}

}
