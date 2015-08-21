package openrts.server;

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
import view.MapView;
import view.camera.IsometricCamera;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;

import controller.CommandManager;
import controller.Controller;
import controller.GUIController;

public class GameController extends Controller {

	private boolean paused = false;
	private Point2D zoneStart;
	private boolean drawingZone = false;
	protected MapView view;

	protected ServerGameInputInterpreter inputInterpreter;
	protected GUIController guiController;

	@Inject
	public GameController(@Named("MapView") MapView view, @Named("GameNiftyController") GameNiftyController guiController,
			@Named("InputManager") InputManager inputManager,
			@Named("Camera") Camera cam, @Named("GameInputInterpreter") ServerGameInputInterpreter inputInterpreter) {
		super(view, inputManager, cam);
		this.inputInterpreter = inputInterpreter;
		this.view = view;
		this.spatialSelector.setCentered(false);
		this.guiController = guiController;


	}

	@Override
	public void update(float elapsedTime) {
		// updateSelectionZone();
		updateContext();
		guiController.update();
		view.getActorManager().render();

		// update army
		if (!paused) {
			ArmyManager.update(elapsedTime);
		}
	}

	public void startSelectionZone(double x, double y) {
		zoneStart = new Point2D(x, y);
	}

	public void endSelectionZone() {
		zoneStart = null;
		drawingZone = false;
	}

	public boolean isDrawingZone() {
		return drawingZone;
	}

	// private void updateSelectionZone(double x, double y) {
	// if (zoneStart == null) {
	// view.getGuiNode().detachAllChildren();
	// return;
	// }
	//
	// Point2D coord = new Point2D(x, y);
	// if (coord.equals(zoneStart)) {
	// return;
	// }
	//
	// drawingZone = coord.getDistance(zoneStart) > 10;
	//
	// AlignedBoundingBox rect = new AlignedBoundingBox(zoneStart, coord);
	// List<Unit> inSelection = new ArrayList<>();
	// for (Unit u : ArmyManager.getUnits()) {
	// if (rect.contains(spatialSelector.getScreenCoord(Translator.toVector3f(u.getPos())))) {
	// inSelection.add(u);
	// }
	// }
	// CommandManager.select(inSelection);
	// view.drawSelectionArea(zoneStart, coord);
	// }

	private void updateContext() {
		AlignedBoundingBox screen = new AlignedBoundingBox(Point2D.ORIGIN, camera.getCamCorner());
		List<Unit> inScreen = new ArrayList<>();
		// for (Unit u : ArmyManager.getUnits()) {
		// if (screen.contains(spatialSelector.getScreenCoord(u.getPos()))) {
		// inScreen.add(u);
		// }
		// }
		CommandManager.createContextualUnities(inScreen);

	}

	@Subscribe
	public void manageEvent(ControllerChangeEvent ev) {
		guiController.update();
	}

	@Subscribe
	public void manageEvent(BattleFieldUpdateEvent ev) {
		((IsometricCamera) camera).move(ModelManager.getBattlefield().getMap().xSize() / 2, ModelManager.getBattlefield().getMap().ySize() / 2);
	}

	// TODO: See AppState.setEnabled => use it, this is a better implementation
	public void togglePause() {
		paused = !paused;
		// view.getActorManager().pause(paused);
	}

	@Override
	public void stateAttached(AppStateManager stateManager) {
		super.stateAttached(stateManager);
		inputManager.setCursorVisible(true);
		guiController.activate();
		EventManager.register(this);
	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		inputInterpreter.unregisterInputs(inputManager);
		camera.unregisterInputs(inputManager);
		EventManager.unregister(this);
	}

}
