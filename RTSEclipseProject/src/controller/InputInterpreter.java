package controller;

import model.Model;
import view.View;

import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.renderer.Camera;
import model.Commander;

public abstract class InputInterpreter implements AnalogListener, ActionListener {
        protected String[] mappings;
        protected Controller ctrl;
	
	protected InputInterpreter(Controller controller){
            this.ctrl = controller;
	}
	
	protected abstract void registerInputs(InputManager inputManager);
	protected abstract void unregisterInputs(InputManager inputManager);
}
