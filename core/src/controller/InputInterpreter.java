package controller;

import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;

public abstract class InputInterpreter implements AnalogListener, ActionListener {
	protected String[] mappings;
	protected Controller ctrl;

	protected InputInterpreter(Controller controller) {
		this.ctrl = controller;
	}

	protected abstract void registerInputs(InputManager inputManager);

	protected abstract void unregisterInputs(InputManager inputManager);
}
