package controller.battlefield;

import event.ControllerChangeEvent;
import event.EventManager;
import geometry.geom2d.Point2D;

import java.util.logging.Logger;

import model.CommandManager;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

import controller.InputInterpreter;

public class BattlefieldInputInterpreter extends InputInterpreter {

	private static final Logger logger = Logger.getLogger(BattlefieldInputInterpreter.class.getName());

	protected final static String SWITCH_CTRL_1 = "ctrl1";
	protected final static String SWITCH_CTRL_2 = "ctrl2";
	protected final static String SWITCH_CTRL_3 = "ctrl3";

	protected final static String SELECT = "select";
	protected final static String ACTION = "action";
	protected final static String MOVE_ATTACK = "moveattack";
	protected final static String MULTIPLE_SELECTION = "multipleselection";
	protected final static String HOLD = "hold";
	protected final static String PAUSE = "pause";

	protected final static int DOUBLE_CLIC_DELAY = 200;// milliseconds
	protected final static int DOUBLE_CLIC_MAX_OFFSET = 5;// in pixels on screen

	boolean multipleSelection = false;
	double dblclicTimer = 0;
	Point2D dblclicCoord;

	BattlefieldInputInterpreter(BattlefieldController controller) {
		super(controller);
		controller.spatialSelector.centered = false;
		setMappings();
	}

	private void setMappings() {
		mappings = new String[] { SWITCH_CTRL_1, SWITCH_CTRL_2, SWITCH_CTRL_3, SELECT, ACTION, MOVE_ATTACK, MULTIPLE_SELECTION, HOLD, PAUSE };
	}

	@Override
	protected void registerInputs(InputManager inputManager) {
		inputManager.addMapping(SWITCH_CTRL_1, new KeyTrigger(KeyInput.KEY_F1));
		inputManager.addMapping(SWITCH_CTRL_2, new KeyTrigger(KeyInput.KEY_F2));
		inputManager.addMapping(SWITCH_CTRL_3, new KeyTrigger(KeyInput.KEY_F3));
		inputManager.addMapping(SELECT, new MouseButtonTrigger(0));
		inputManager.addMapping(ACTION, new MouseButtonTrigger(1));
		inputManager.addMapping(MOVE_ATTACK, new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping(MULTIPLE_SELECTION, new KeyTrigger(KeyInput.KEY_LCONTROL),
				new KeyTrigger(KeyInput.KEY_RCONTROL));
		inputManager.addMapping(HOLD, new KeyTrigger(KeyInput.KEY_H));
		inputManager.addMapping(PAUSE, new KeyTrigger(KeyInput.KEY_SPACE));

		inputManager.addListener(this, mappings);

		logger.info("battlefield controller online");
	}

	@Override
	protected void unregisterInputs(InputManager inputManager) {
		for (String s : mappings) {
			if (inputManager.hasMapping(s)) {
				inputManager.deleteMapping(s);
			}
		}
		inputManager.removeListener(this);
	}

	@Override
	public void onAnalog(String name, float value, float tpf) {
	}

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		if (!isPressed) {
			switch (name) {
				case SWITCH_CTRL_1:
					EventManager.post(new ControllerChangeEvent(0));
					break;
				case SWITCH_CTRL_2:
					EventManager.post(new ControllerChangeEvent(1));
					break;
				case SWITCH_CTRL_3:
					EventManager.post(new ControllerChangeEvent(2));
					break;

				case MULTIPLE_SELECTION:
					CommandManager.setMultipleSelection(false);
					break;
				case SELECT:
					if(System.currentTimeMillis()-dblclicTimer < DOUBLE_CLIC_DELAY &&
							dblclicCoord.getDistance(getSpatialCoord()) < DOUBLE_CLIC_MAX_OFFSET){
						// double clic
						CommandManager.selectUnityInContext(ctrl.spatialSelector.getEntityId());
					} else {
						if(!((BattlefieldController) ctrl).isDrawingZone()) {
							CommandManager.select(ctrl.spatialSelector.getEntityId(), getSpatialCoord());
						}
					}
					((BattlefieldController) ctrl).endSelectionZone();
					dblclicTimer = System.currentTimeMillis();
					dblclicCoord = getSpatialCoord();
					break;
				case ACTION:
					CommandManager.act(ctrl.spatialSelector.getEntityId(), getSpatialCoord());
					break;
				case MOVE_ATTACK:
					CommandManager.setMoveAttack();
					break;
				case HOLD:
					CommandManager.orderHold();
					break;
				case PAUSE:
					((BattlefieldController) ctrl).togglePause();
					break;
			}
		} else {
			// input pressed
			switch(name){
				case MULTIPLE_SELECTION:
					CommandManager.setMultipleSelection(true);
					break;
				case SELECT:
					((BattlefieldController) ctrl).startSelectionZone();
					break;
			}
		}
	}

	private Point2D getSpatialCoord() {
		return ctrl.spatialSelector.getCoord((((BattlefieldController) ctrl).view.getRootNode()));
	}
}
