/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ElementRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import model.Reporter;

/**
 *
 * @author Benoît
 */
public abstract class GUI implements ScreenController {
    protected Controller ctrl;
    protected Nifty nifty;

    public GUI(Controller ctrl, Nifty nifty) {
        this.ctrl = ctrl;
        this.nifty = nifty;
        nifty.registerScreenController(this);
    }
    public abstract void update();
    public abstract void activate();
    
    protected Element getElement(String s){
        return nifty.getCurrentScreen().findElementByName(s);
    }
    
    protected void setText(String textElement, String text){
        Element el = getElement(textElement);
        if(el == null || el.getRenderer(TextRenderer.class) == null)
            el = getElement(textElement+"#text");
        if(el == null)
            throw new IllegalArgumentException("can't find element '"+textElement+"' nor #text child.");
        
        el.getRenderer(TextRenderer.class).setText(text);
    }


}
