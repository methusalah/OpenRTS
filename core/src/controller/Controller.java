/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import model.ReportEventListener;
import view.View;

import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;

import controller.cameraManagement.CameraManager;

/**
 *
 * @author Benoît
 */
public abstract class Controller extends AbstractAppState implements ReportEventListener {
	public View view;
	public InputInterpreter inputInterpreter;
	public InputManager inputManager;
	public SpatialSelector spatialSelector;
	public CameraManager cameraManager;

	public GUIController guiController;
	ArrayList<ActionListener> listeners = new ArrayList<>();

	public Controller(View view, InputManager inputManager, Camera cam) {
		super();
		this.view = view;
		this.inputManager = inputManager;
		spatialSelector = new SpatialSelector(cam, inputManager, view);

	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		inputInterpreter.unregisterInputs(inputManager);
		cameraManager.unregisterInputs(inputManager);
	}

	@Override
	public void stateAttached(AppStateManager stateManager) {
		inputInterpreter.registerInputs(inputManager);
		cameraManager.registerInputs(inputManager);
		cameraManager.activate();
	}

	public void addListener(ActionListener listener) {
		listeners.add(listener);
	}

	public void notifyListeners(String command) {
		for (ActionListener l : listeners) {
			l.actionPerformed(new ActionEvent(this, 0, command));
		}
	}
}
