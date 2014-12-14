/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import de.lessvoid.nifty.Nifty;
import model.Model;
import model.ReportEventListener;
import view.View;

/**
 *
 * @author Benoît
 */
public abstract class Controller implements ReportEventListener {
    
    public Model model;
    public InputInterpreter ii;
    protected GUI gui;
}
