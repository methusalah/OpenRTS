/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import model.Model;
import model.ReportEventListener;
import view.View;

import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;

import controller.cameraManagement.CameraManager;

/**
 *
 * @author Benoît
 */
public abstract class Controller implements ReportEventListener {
    public Model model;
    public View view;
    public InputInterpreter inputInterpreter;
    public InputManager inputManager;
    public SpatialSelector spatialSelector;
    public CameraManager cameraManager;
    
    public GUIController guiController;
    ArrayList<ActionListener> listeners = new ArrayList<>();
    
    public Controller(Model model, View view, InputManager inputManager, Camera cam){
        this.model = model;
        this.view = view;
        this.inputManager = inputManager;
        spatialSelector = new SpatialSelector(cam, inputManager, view);
    }
    
    
    public abstract void update(double elapsedTime);
    
    public void desactivate(){
        inputInterpreter.unregisterInputs(inputManager);
        cameraManager.unregisterInputs(inputManager);
    }
    public void activate(){
        inputInterpreter.registerInputs(inputManager);
        cameraManager.registerInputs(inputManager);
        cameraManager.activate();
    }
    
    public void addListener(ActionListener listener){
        listeners.add(listener);
    }
    
    public void notifyListeners(String command){
        for(ActionListener l : listeners)
            l.actionPerformed(new ActionEvent(this, 0, command));
    }
}
