package controller.battlefield;

import java.util.ArrayList;
import java.util.List;

import event.EventManager;
import event.InputEvent;
import geometry.geom2d.AlignedBoundingBox;
import geometry.geom2d.Point2D;
import geometry.tools.LogUtil;
import model.CommandManager;
import model.ModelManager;
import model.battlefield.army.components.Unit;
import view.math.Translator;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

import controller.InputInterpreter;

public class BattlefieldInputInterpreter extends InputInterpreter {
	protected final static String SWITCH_CTRL_1 = "ctrl1";
	protected final static String SWITCH_CTRL_2 = "ctrl2";
	protected final static String SWITCH_CTRL_3 = "ctrl3";

	protected final static String SELECT = "select";
	protected final static String ACTION = "action";
	protected final static String MOVE_ATTACK = "moveattack";
	protected final static String HOLD = "hold";
	protected final static String PAUSE = "pause";

	public Point2D selectionStart;

	BattlefieldInputInterpreter(BattlefieldController controller) {
		super(controller);
		controller.spatialSelector.centered = false;
		setMappings();
	}

	private void setMappings() {
		mappings = new String[] { SWITCH_CTRL_1, SWITCH_CTRL_2, SWITCH_CTRL_3, SELECT, ACTION, MOVE_ATTACK, HOLD, PAUSE };
	}

	@Override
	protected void registerInputs(InputManager inputManager) {
		inputManager.addMapping(SWITCH_CTRL_1, new KeyTrigger(KeyInput.KEY_F1));
		inputManager.addMapping(SWITCH_CTRL_2, new KeyTrigger(KeyInput.KEY_F2));
		inputManager.addMapping(SWITCH_CTRL_3, new KeyTrigger(KeyInput.KEY_F3));
		inputManager.addMapping(SELECT, new MouseButtonTrigger(0));
		inputManager.addMapping(ACTION, new MouseButtonTrigger(1));
		inputManager.addMapping(MOVE_ATTACK, new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping(HOLD, new KeyTrigger(KeyInput.KEY_H));
		inputManager.addMapping(PAUSE, new KeyTrigger(KeyInput.KEY_SPACE));

		inputManager.addListener(this, mappings);

		LogUtil.logger.info("battlefield controller online");
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
		// TODO: duplicate Post Event: first here, another in notifyListener
		EventManager.post(new InputEvent(name));
		if (!isPressed) {
			switch (name) {
				case SWITCH_CTRL_1:
					ctrl.notifyListeners("CTRL1");
					break;
				case SWITCH_CTRL_2:
					ctrl.notifyListeners("CTRL2");
					break;
				case SWITCH_CTRL_3:
					ctrl.notifyListeners("CTRL3");
					break;
				case SELECT:
					if (!endSelection()) {
						CommandManager.select(ctrl.spatialSelector.getEntityId(), getSpatialCoord());
					}
					break;
				case ACTION:
					CommandManager.act(ctrl.spatialSelector.getEntityId(), getSpatialCoord());
					break;
				case MOVE_ATTACK:
					CommandManager.setMoveAttack();
					LogUtil.logger.info("move attack");
					break;
				case HOLD:
					CommandManager.orderHold();
					break;
				case PAUSE:
					((BattlefieldController) ctrl).togglePause();
					break;
			}
		} else if (name.equals(SELECT)) {
			beginSelection();
		}
	}

	private Point2D getSpatialCoord() {
		return ctrl.spatialSelector.getCoord(ctrl.view.rootNode);
	}

	private void beginSelection() {
		selectionStart = Translator.toPoint2D(ctrl.inputManager.getCursorPosition());
	}

	private boolean endSelection() {
        Point2D selectionEnd = Translator.toPoint2D(ctrl.inputManager.getCursorPosition());
        AlignedBoundingBox rect = new AlignedBoundingBox(selectionStart, selectionEnd);
        
        List<Unit> inSelection = new ArrayList<>();
        for(Unit u : ModelManager.battlefield.armyManager.getUnits())
        	if(rect.contains(ctrl.spatialSelector.getScreenCoord(u.getPos())))
        		inSelection.add(u);
        CommandManager.select(inSelection);
		selectionStart = null;
		return !inSelection.isEmpty();
	}
}
