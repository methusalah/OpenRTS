package openrts.server;

import geometry.geom2d.AlignedBoundingBox;
import geometry.geom2d.Point2D;

import java.util.ArrayList;
import java.util.List;

import model.ModelManager;
import model.battlefield.army.ArmyManager;
import model.battlefield.army.components.Unit;
import view.MapView;

import com.google.common.eventbus.Subscribe;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.Camera;

import controller.CommandManager;
import controller.GUIController;
import controller.cameraManagement.CameraManager;
import controller.cameraManagement.IsometricCameraManager;
import de.lessvoid.nifty.Nifty;
import event.BattleFieldUpdateEvent;
import event.ControllerChangeEvent;

public class GameController extends AbstractAppState {

	private boolean paused = false;
	private Point2D zoneStart;
	private boolean drawingZone = false;
	private SpatialSelector spatialSelector;
	private CameraManager cameraManager;
	private GUIController guiController;

	public GameController(MapView view, Nifty nifty, Camera cam) {
		super();
		// this.view = view;
		spatialSelector = new SpatialSelector(cam, view);
		// spatialSelector.centered = false;
		guiController = new GameNiftyController(nifty);

		cameraManager = new IsometricCameraManager(cam, 10);
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
		((IsometricCameraManager) cameraManager).move(ModelManager.getBattlefield().getMap().xSize() / 2, ModelManager.getBattlefield().getMap().ySize() / 2);
	}

	// TODO: See AppState.setEnabled => use it, this is a better implementation
	public void togglePause() {
		paused = !paused;
		// FIXME: Pause is not support now
		// view.getActorManager().pause(paused);
	}

	@Override
	public void stateAttached(AppStateManager stateManager) {
		super.stateAttached(stateManager);
		guiController.activate();
	}

	public SpatialSelector getSpatialSelector() {
		return spatialSelector;
	}

}
