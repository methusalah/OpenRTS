package controller.game;

import geometry.geom2d.AlignedBoundingBox;
import geometry.geom2d.Point2D;

import java.util.ArrayList;
import java.util.List;

import model.ModelManager;
import model.battlefield.army.ArmyManager;
import model.battlefield.army.components.Unit;
import util.MapArtisanManager;
import view.EditorView;
import view.MapView;
import view.camera.IsometricCamera;
import view.math.TranslateUtil;
import brainless.openrts.event.BattleFieldUpdateEvent;
import brainless.openrts.event.EventManager;
import brainless.openrts.event.client.ControllerChangeEvent;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;

import controller.CommandManager;
import controller.Controller;

public class MultiplayerGameController extends Controller {

	private boolean paused = false;
	private Point2D zoneStart;
	private boolean drawingZone = false;
	protected MapView view;
//	protected MultiplayerGameNiftyController guiController;

	@Inject
	private ArmyManager armyManager;
	
	@Inject
	private CommandManager commandManager;
	
//	@Inject
//	private ModelManager modelManager;
	
	
	@Inject
	public MultiplayerGameController(EditorView view, 
//			MultiplayerGameNiftyController guiController, 
			InputManager inputManager,
			Camera cam, Injector injector, ModelManager modelManager) {
		super(view, inputManager, cam, injector, modelManager);
		this.view = view;
		// this.inputInterpreter = inputInterpreter;
		this.spatialSelector.setCentered(false);
//		this.guiController = guiController;

		EventManager.register(this);
	}

	@Override
	public void update(float elapsedTime) {
		// updateSelectionZone();
		updateContext();
//		guiController.update();

		// update army
		if (!paused) {
			armyManager.update(elapsedTime);
		}
	}

	public void startSelectionZone() {
		zoneStart = getMouseCoord();
	}

	public void endSelectionZone() {
		zoneStart = null;
		drawingZone = false;
	}

	public boolean isDrawingZone() {
		return drawingZone;
	}

	private void updateSelectionZone() {
		if (zoneStart == null) {
			view.getGuiNode().detachAllChildren();
			return;
		}

		Point2D coord = getMouseCoord();
		if (coord.equals(zoneStart)) {
			return;
		}

		drawingZone = coord.getDistance(zoneStart) > 10;

		AlignedBoundingBox rect = new AlignedBoundingBox(zoneStart, coord);
		List<Unit> inSelection = new ArrayList<>();
		for (Unit u : armyManager.getUnits()) {
			if (rect.contains(spatialSelector.getScreenCoord(u.getPos()))) {
				inSelection.add(u);
			}
		}
		commandManager.select(inSelection);
		view.drawSelectionArea(zoneStart, coord);
	}

	private void updateContext() {
		AlignedBoundingBox screen = new AlignedBoundingBox(Point2D.ORIGIN, camera.getCamCorner());
		List<Unit> inScreen = new ArrayList<>();
		for (Unit u : armyManager.getUnits()) {
			if (screen.contains(spatialSelector.getScreenCoord(u.getPos()))) {
				inScreen.add(u);
			}
		}
		commandManager.createContextualUnities(inScreen);

	}

	@Subscribe
	public void manageEvent(ControllerChangeEvent ev) {
//		guiController.update();
	}

	@Subscribe
	public void manageEvent(BattleFieldUpdateEvent ev) {
		((IsometricCamera)camera).move(modelManager.getBattlefield().getMap().xSize() / 2, modelManager.getBattlefield().getMap().ySize() / 2);
	}

	// TODO: See AppState.setEnabled => use it, this is a better implementation
	public void togglePause() {
		paused = !paused;
		view.getActorManager().pause(paused);
	}

	@Override
	public void stateAttached(AppStateManager stateManager) {
		super.stateAttached(stateManager);
		inputManager.setCursorVisible(true);
//		guiController.activate();
		view.reset();
	}

	private Point2D getMouseCoord() {
		return TranslateUtil.toPoint2D(inputManager.getCursorPosition());
	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		// TODO Auto-generated method stub
		super.stateDetached(stateManager);
		inputManager.setCursorVisible(false);
	}

}
