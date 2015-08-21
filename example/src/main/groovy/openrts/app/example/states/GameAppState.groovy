package openrts.app.example.states;

import model.ModelManager
import model.battlefield.army.ArmyManager
import model.battlefield.army.components.Unit
import openrts.app.example.MultiplayerGame
import tonegod.gui.controls.buttons.ButtonAdapter
import view.EditorView
import view.camera.Camera
import view.camera.IsometricCamera
import view.math.TranslateUtil

import com.google.common.eventbus.Subscribe
import com.google.inject.Inject
import com.google.inject.Injector
import com.jme3.app.state.AppStateManager
import com.jme3.input.FlyByCamera
import com.jme3.input.InputManager

import controller.CommandManager
import controller.SpatialSelector
import event.BattleFieldUpdateEvent
import event.EventManager
import event.client.ControllerChangeEvent
import geometry.geom2d.AlignedBoundingBox
import geometry.geom2d.Point2D
import groovy.transform.CompileStatic

@CompileStatic
public class GameAppState extends AppStateCommon {

	private boolean paused = false;
	private Point2D zoneStart;
	
	private boolean drawingZone = false;
	@Inject
	protected EditorView view;
	
	@Inject
	protected SpatialSelector spatialSelector;
	
	protected Camera cameraManager;
	@Inject
	protected Injector injector
	
	@Inject
	protected InputManager inputManager;
	
	@Inject
	protected com.jme3.renderer.Camera cam
	
	@Inject
	protected GameInputInterpreter inputInterpreter
	
	@Inject
	public GameAppState(MultiplayerGame main) {
		super(main);
		displayName = "GameAppState";
		show = true;
		EventManager.register(this);
	}

	@Override
	public void update(float elapsedTime) {
		updateSelectionZone();
		updateContext();
		//sguiController.update();

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
	public void manageEvent(BattleFieldUpdateEvent ev) {
		placeCamera();
	}

	private placeCamera() {
		((IsometricCamera)cameraManager).move(ModelManager.getBattlefield().getMap().xSize() / 2, ModelManager.getBattlefield().getMap().ySize() / 2)
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
		//guiController.activate();
		
		def btn = new ButtonAdapter(screen)
		btn.text = "hello world"
		screen.addElement(btn);
		view.reset();
		
		if (cameraManager == null) {
			cameraManager = new IsometricCamera(cam, 10);			
		}
		placeCamera();
		inputInterpreter.registerInputs(inputManager);
		cameraManager.registerInputs(inputManager);
		cameraManager.activate();
		
		
	}

	private Point2D getMouseCoord() {
		return TranslateUtil.toPoint2D(inputManager.getCursorPosition());
	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		super.stateDetached(stateManager);
		inputManager.setCursorVisible(false);
		inputInterpreter.unregisterInputs(inputManager);
		cameraManager.unregisterInputs(inputManager);
		EventManager.register(this);
	}

	@Override
	public void reshape() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initState() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateState(float tpf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanupState() {
		// TODO Auto-generated method stub
		
	}

}
