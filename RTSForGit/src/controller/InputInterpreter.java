package controller;

import model.Model;
import view.View;

import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.renderer.Camera;
import model.Commander;

public abstract class InputInterpreter implements AnalogListener, ActionListener {

	protected Commander commander;
	protected View view;
	protected SpatialSelector selector;
	public boolean isActive = false;
	protected InputManager inputManager;
	
	protected InputInterpreter(InputManager im, Camera c, Commander commander, View v){
		this.commander = commander;
		view = v;
		inputManager = im;
		selector = new SpatialSelector(c, im, v);
		registerInputs();
	}
	
	protected abstract void registerInputs();
	
	public void activate(){
		isActive = true;
	}
	
	public void desactivate(){
		isActive = false;
	}
}
