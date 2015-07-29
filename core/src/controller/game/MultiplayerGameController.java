package controller.game;

import event.BattleFieldUpdateEvent;
import event.EventManager;
import event.client.ControllerChangeEvent;
import geometry.geom2d.AlignedBoundingBox;
import geometry.geom2d.Point2D;

import java.util.ArrayList;
import java.util.List;

import model.ModelManager;
import model.battlefield.army.ArmyManager;
import model.battlefield.army.components.Unit;
import openrts.guice.annotation.InputManagerRef;
import view.EditorView;
import view.MapView;
import view.math.TranslateUtil;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;

import controller.CommandManager;
import controller.Controller;
import controller.cameraManagement.IsometricCameraManager;

public class MultiplayerGameController extends Controller {

	private boolean paused = false;
	private Point2D zoneStart;
	private boolean drawingZone = false;
	protected MapView view;
	protected MultiplayerGameNiftyController guiController;

	@Inject
	public MultiplayerGameController(EditorView view, MultiplayerGameNiftyController guiController,
			@InputManagerRef InputManager inputManager,
			@Named("Camera") Camera cam) {
		super(view, inputManager, cam);
		this.view = view;
		// this.inputInterpreter = inputInterpreter;
		this.spatialSelector.setCentered(false);
		this.guiController = guiController;

		EventManager.register(this);
	}

	@Override
	public void update(float elapsedTime) {
		// updateSelectionZone();
		updateContext();
		guiController.update();

		// update army
		if (!paused) {
			ArmyManager.update(elapsedTime);
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
		for (Unit u : ArmyManager.getUnits()) {
			if (rect.contains(spatialSelector.getScreenCoord(u.getPos()))) {
				inSelection.add(u);
			}
		}
		CommandManager.select(inSelection);
		view.drawSelectionArea(zoneStart, coord);
	}

	private void updateContext() {
		AlignedBoundingBox screen = new AlignedBoundingBox(Point2D.ORIGIN, cameraManager.getCamCorner());
		List<Unit> inScreen = new ArrayList<>();
		for (Unit u : ArmyManager.getUnits()) {
			if (screen.contains(spatialSelector.getScreenCoord(u.getPos()))) {
				inScreen.add(u);
			}
		}
		CommandManager.createContextualUnities(inScreen);

	}

	@Subscribe
	public void manageEvent(ControllerChangeEvent ev) {
		guiController.update();
	}

	@Subscribe
	public void manageEvent(BattleFieldUpdateEvent ev) {
		((IsometricCameraManager)cameraManager).move(ModelManager.getBattlefield().getMap().xSize() / 2, ModelManager.getBattlefield().getMap().ySize() / 2);
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
		guiController.activate();
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
