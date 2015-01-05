/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.effects.Effect;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ElementRenderer;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.awt.Color;
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
    
    protected void changeLabelText(String id, String text){
        Element e = getElement(id);
        if(e == null || e.getRenderer(TextRenderer.class) == null)
            throw new IllegalArgumentException("can't find label '"+id+"'.");
        
        e.getRenderer(TextRenderer.class).setText(text);
    }
    
    protected void changeButtonText(String id, String text){
        Element e = getElement(id+"#text");
        if(e == null || e.getRenderer(TextRenderer.class) == null)
            throw new IllegalArgumentException("can't find button's text panel '"+id+"'.");
        e.getRenderer(TextRenderer.class).setText(text);
    }

    protected void changeButtonTextColor(String id, Color color){
        de.lessvoid.nifty.tools.Color niftyColor = new de.lessvoid.nifty.tools.Color(color.toString());//color.getRed(), color.getGreen(), color.getBlue(), 1);
        Element e = getElement(id+"#text");
        if(e == null || e.getRenderer(TextRenderer.class) == null)
            throw new IllegalArgumentException("can't find button's text panel '"+id+"'.");
        e.getRenderer(TextRenderer.class).setColor(niftyColor);
    }
    
    
    
    protected void setBackground(String id, String backgroundPath){
        Element e = getElement(id);
        if(e == null || e.getRenderer(ImageRenderer.class) == null)
            throw new IllegalArgumentException("can't find element with background '"+id+"'.");
        e.getRenderer(ImageRenderer.class).setImage(nifty.createImage(backgroundPath, false));
    }
    
    protected void enable(String elementName){
        getElement(elementName).enable();
    }
    
    protected Slider getSlider(String id){
        return getElement(id).getAttachedInputControl().getNiftyControl(Slider.class);
    }


}
