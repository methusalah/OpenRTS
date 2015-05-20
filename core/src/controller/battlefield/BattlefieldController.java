package controller.battlefield;

import java.util.ArrayList;
import java.util.List;

import model.CommandManager;
import model.ModelManager;
import model.battlefield.army.components.Unit;
import view.MapView;
import view.math.Translator;

import com.google.common.eventbus.Subscribe;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;

import controller.Controller;
import controller.cameraManagement.IsometricCameraManager;
import de.lessvoid.nifty.Nifty;
import event.EventManager;
import event.InputEvent;
import geometry.geom2d.AlignedBoundingBox;
import geometry.geom2d.Point2D;

/**
 *
 */
public class BattlefieldController extends Controller {
	private boolean paused = false;
	private Point2D zoneStart;
	private boolean drawingZone = false;


	public BattlefieldController(MapView view, Nifty nifty, InputManager inputManager, Camera cam) {
		super(view, inputManager, cam);
		this.view = view;
		inputInterpreter = new BattlefieldInputInterpreter(this);
		guiController = new BattlefieldGUIController(nifty, this);

		EventManager.register(this);

		cameraManager = new IsometricCameraManager(cam, 10);
	}

	@Override
	public void update(float elapsedTime) {
		updateSelectionZone();
		
		
		// update selectables
		CommandManager.updateSelectables(spatialSelector.getCenterViewCoord(view.rootNode));
		guiController.update();

		// udpdate army
		if(!paused) {
			ModelManager.battlefield.armyManager.update(elapsedTime);
		}
	}

	public void startSelectionZone(){
		zoneStart = getMouseCoord();
	}

	public void endSelectionZone(){
		zoneStart = null;
		drawingZone = false;
	}
	
	public boolean isDrawingZone(){
		return drawingZone;
	}
	
	private void updateSelectionZone(){
		if(zoneStart == null){
			view.guiNode.detachAllChildren();
			return;
		}
		
        Point2D coord = getMouseCoord();
        if(coord.equals(zoneStart))
        	return;
        
        drawingZone = coord.getDistance(zoneStart) > 10;
        
        AlignedBoundingBox rect = new AlignedBoundingBox(zoneStart, coord);
        List<Unit> inSelection = new ArrayList<>();
        for(Unit u : ModelManager.battlefield.armyManager.getUnits()) {
			if(rect.contains(spatialSelector.getScreenCoord(u.getPos()))) {
				inSelection.add(u);
			}
		}
        CommandManager.select(inSelection);
		view.drawSelectionArea(zoneStart, coord);
	}
	
	
	@Subscribe
	public void manageEvent(InputEvent ev) {
		guiController.update();

	}

	// TODO: See AppState.setEnabled => use it, this is a better implementation
	public void togglePause(){
		paused = !paused;
		view.actorManager.pause(paused);
	}

	@Override
	public void stateAttached(AppStateManager stateManager) {
		super.stateAttached(stateManager);
		inputManager.setCursorVisible(true);
		guiController.activate();
	}
	
	private Point2D getMouseCoord(){
		return Translator.toPoint2D(inputManager.getCursorPosition());
	}
}
