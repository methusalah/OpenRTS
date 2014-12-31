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
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public abstract class Controller implements ReportEventListener {
    
    public Model model;
    public InputInterpreter ii;
    protected GUI gui;
    ArrayList<ActionListener> listeners = new ArrayList<>();
    
    public abstract void update(double elapsedTime);
    public void desactivate(){
        ii.unregisterInputs();
    }
    public void activate(){
        ii.registerInputs();
    }
    
    public void register(ActionListener listener){
        listeners.add(listener);
    }
    
    public void notifyListeners(String command){
        for(ActionListener l : listeners)
            l.actionPerformed(new ActionEvent(this, 0, command));
    }
}
