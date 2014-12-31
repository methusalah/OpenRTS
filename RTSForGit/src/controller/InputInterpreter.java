package controller;

import model.Model;
import view.View;

import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.renderer.Camera;
import model.Commander;

public abstract class InputInterpreter implements AnalogListener, ActionListener {

	protected View view;
	protected SpatialSelector selector;
	public boolean isActive = false;
	protected InputManager inputManager;
        protected String[] mappings;
	
	protected InputInterpreter(InputManager im, Camera c, View v){
		view = v;
		inputManager = im;
		selector = new SpatialSelector(c, im, v);
	}
	
	protected abstract void registerInputs();
	protected abstract void unregisterInputs();
	
	public void activate(){
		isActive = true;
	}
	
	public void desactivate(){
		isActive = false;
	}
}
