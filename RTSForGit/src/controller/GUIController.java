/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.ScreenController;
import java.awt.Color;

/**
 *
 * @author Benoît
 */
public abstract class GUIController implements ScreenController {
    public Controller ctrl;
    protected Nifty nifty;
    protected GUIDrawer drawer;

    public GUIController(Controller ctrl, Nifty nifty) {
        this.ctrl = ctrl;
        this.nifty = nifty;
        nifty.registerScreenController(this);
    }
    public abstract void update();
    public abstract void activate();
    
    protected Element getElement(String s){
        return nifty.getCurrentScreen().findElementByName(s);
    }

}
