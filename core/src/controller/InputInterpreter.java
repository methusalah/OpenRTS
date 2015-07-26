package controller;

import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;

public abstract class InputInterpreter implements AnalogListener, ActionListener {
	protected String[] mappings;

	protected InputInterpreter() {
	}

	public abstract void registerInputs(InputManager inputManager);

	public void unregisterInputs(InputManager inputManager) {
		for (String s : mappings) {
			if (inputManager.hasMapping(s)) {
				inputManager.deleteMapping(s);
			}
		}
		inputManager.removeListener(this);
	}
}
