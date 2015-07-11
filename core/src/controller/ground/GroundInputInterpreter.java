package controller.ground;

import java.util.logging.Logger;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;

import controller.InputInterpreter;
import event.ControllerChangeEvent;
import event.EventManager;

public class GroundInputInterpreter extends InputInterpreter {

	private static final Logger logger = Logger.getLogger(GroundInputInterpreter.class.getName());

	protected final static String SWITCH_CTRL_1 = "ctrl1";
	protected final static String SWITCH_CTRL_2 = "ctrl2";
	protected final static String SWITCH_CTRL_3 = "ctrl3";

	GroundInputInterpreter(GroundController controller) {
		super(controller);
		setMappings();
	}

	private void setMappings(){
		mappings = new String[]{
				SWITCH_CTRL_1,
				SWITCH_CTRL_2,
				SWITCH_CTRL_3,
		};
	}

	@Override
	protected void registerInputs(InputManager inputManager) {
		inputManager.addMapping(SWITCH_CTRL_1, new KeyTrigger(KeyInput.KEY_F1));
		inputManager.addMapping(SWITCH_CTRL_2, new KeyTrigger(KeyInput.KEY_F2));
		inputManager.addMapping(SWITCH_CTRL_3, new KeyTrigger(KeyInput.KEY_F3));
		inputManager.addListener(this, mappings);
		logger.info("Ground conroller online");
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
		if(!isPressed){
			switch(name){
				case SWITCH_CTRL_1 :
					EventManager.post(new ControllerChangeEvent(0));
					break;
				case SWITCH_CTRL_2 :
					EventManager.post(new ControllerChangeEvent(1));
					break;
				case SWITCH_CTRL_3 :
					EventManager.post(new ControllerChangeEvent(2));
					break;
			}
		}
	}
}
