package controller.game;

import geometry.geom2d.Point2D;

import java.util.logging.Logger;

import brainless.openrts.event.EventManager;
import brainless.openrts.event.network.HoldEvent;
import brainless.openrts.event.network.MoveAttackEvent;
import brainless.openrts.event.network.MultiSelectEntityEvent;
import brainless.openrts.event.network.PauseEvent;
import brainless.openrts.event.network.SelectEntityEvent;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

import controller.CommandManager;
import controller.InputInterpreter;

public class MultiplayerGameInputInterpreter extends InputInterpreter {

	private static final Logger logger = Logger.getLogger(MultiplayerGameInputInterpreter.class.getName());

	protected final static String SELECT = "select";
	protected final static String ACTION = "action";
	protected final static String MOVE_ATTACK = "moveattack";
	protected final static String MULTIPLE_SELECTION = "multipleselection";
	protected final static String HOLD = "hold";
	protected final static String PAUSE = "pause";

	protected final static int DOUBLE_CLICK_DELAY = 200;// milliseconds
	protected final static int DOUBLE_CLICK_MAX_OFFSET = 5;// in pixels on screen

	private boolean multipleSelection = false;
	private double dblclickTimer = 0;
	private Point2D dblclickCoord;

	private MultiplayerGameController ctrl;
	
	@Inject
	private CommandManager commandManager;
	
	@Inject
	public MultiplayerGameInputInterpreter(@Named("MultiplayerGameController") MultiplayerGameController ctrl) {
		super();
		this.ctrl = ctrl;
		mappings = new String[] { SELECT, ACTION, MOVE_ATTACK, MULTIPLE_SELECTION, HOLD, PAUSE };
	}


	@Override
	public void registerInputs(InputManager inputManager) {

		inputManager.addMapping(SELECT, new MouseButtonTrigger(0));
		inputManager.addMapping(ACTION, new MouseButtonTrigger(1));
		inputManager.addMapping(MOVE_ATTACK, new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping(MULTIPLE_SELECTION, new KeyTrigger(KeyInput.KEY_LCONTROL), new KeyTrigger(KeyInput.KEY_RCONTROL));
		inputManager.addMapping(HOLD, new KeyTrigger(KeyInput.KEY_H));
		inputManager.addMapping(PAUSE, new KeyTrigger(KeyInput.KEY_SPACE));

		inputManager.addListener(this, mappings);

		logger.info("multiplayer controller online");
	}

	@Override
	public void onAnalog(String name, float value, float tpf) {
	}

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		if (!isPressed) {
			switch (name) {

				case MULTIPLE_SELECTION:
					commandManager.setMultipleSelection(false);
					break;
				case SELECT:
					if(System.currentTimeMillis()-dblclickTimer < DOUBLE_CLICK_DELAY &&
							dblclickCoord.getDistance(getSpatialCoord()) < DOUBLE_CLICK_MAX_OFFSET){
						// double click
						EventManager.post(new SelectEntityEvent(ctrl.spatialSelector.getEntityId()));
						commandManager.selectUnitInContext(ctrl.spatialSelector.getEntityId());
					} else {
						if(!ctrl.isDrawingZone()) {
							commandManager.select(ctrl.spatialSelector.getEntityId(), getSpatialCoord());
							EventManager.post(new MultiSelectEntityEvent(ctrl.spatialSelector.getEntityId()));
						}
					}
					ctrl.endSelectionZone();
					dblclickTimer = System.currentTimeMillis();
					dblclickCoord = getSpatialCoord();
					break;
				case ACTION:
					EventManager.post(new SelectEntityEvent(ctrl.spatialSelector.getEntityId()));
					commandManager.act(ctrl.spatialSelector.getEntityId(), getSpatialCoord());
					break;
				case MOVE_ATTACK:
					EventManager.post(new MoveAttackEvent(ctrl.spatialSelector.getEntityId()));
					commandManager.setMoveAttack();
					break;
				case HOLD:
					EventManager.post(new HoldEvent(ctrl.spatialSelector.getEntityId()));
					commandManager.orderHold();
					break;
				case PAUSE:
					EventManager.post(new PauseEvent(ctrl.spatialSelector.getEntityId()));
					ctrl.togglePause();
					break;
			}
		} else {
			// input pressed
			switch(name){
				case MULTIPLE_SELECTION:
					commandManager.setMultipleSelection(true);
					break;
				case SELECT:
					ctrl.startSelectionZone();
					break;
			}
		}
	}

	private Point2D getSpatialCoord() {
		return ctrl.spatialSelector.getCoord((ctrl.view.getRootNode()));
	}
}
