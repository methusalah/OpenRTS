/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import java.awt.Color;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public abstract class GUIDrawer {
    
    protected GUIController guiCtrl;
    
    boolean toDraw = false;

    public GUIDrawer(GUIController guiCtrl) {
        this.guiCtrl = guiCtrl;
    }
    
    public void askRedraw(){
        toDraw = true;
    }
    
    public void update(){
        if(toDraw)
            draw();
        toDraw = false;
    }
    
    protected Element getElement(String id){
        return guiCtrl.getElement(id);
    }
    
    protected abstract void draw();
    
    protected void changeLabelText(String id, String text){
        Element e = guiCtrl.getElement(id);
        if(e == null || e.getRenderer(TextRenderer.class) == null)
            throw new IllegalArgumentException("can't find label '"+id+"'.");
        
        e.getRenderer(TextRenderer.class).setText(text);
    }
    
    protected void changeButtonText(String id, String text){
        Element e = guiCtrl.getElement(id+"#text");
        if(e == null || e.getRenderer(TextRenderer.class) == null)
            throw new IllegalArgumentException("can't find button's text '"+id+"'.");
        e.getRenderer(TextRenderer.class).setText(text);
    }

    protected void changeButtonTextColor(String id, Color color){
        float a = color.getAlpha();
        float r = color.getRed();
        float g = color.getGreen();
        float b = color.getBlue();
        de.lessvoid.nifty.tools.Color niftyColor = new de.lessvoid.nifty.tools.Color(r/255, g/255, b/255, 1);
        Element e = guiCtrl.getElement(id+"#text");
        if(e == null || e.getRenderer(TextRenderer.class) == null)
            throw new IllegalArgumentException("can't find button's text '"+id+"'.");
        e.getRenderer(TextRenderer.class).setColor(niftyColor);
    }
    
    protected void setButtonPanelCustomEffet(String id, boolean val){
        if(id.equals("set0")){
                LogUtil.logger.info("set0");
                
            Element e = guiCtrl.getElement(id+"#hop");
            if(e == null)
                return;

    //            throw new IllegalArgumentException("can't find button '"+id+"'.");
            LogUtil.logger.info("hop");
            if(val)
                e.startEffect(EffectEventId.onCustom);
            else
                e.stopEffect(EffectEventId.onCustom);
        }
        
    }

    protected void setBackground(String id, String backgroundPath){
        Element e = guiCtrl.getElement(id);
        if(e == null || e.getRenderer(ImageRenderer.class) == null)
            throw new IllegalArgumentException("can't find element with background '"+id+"'.");
        e.getRenderer(ImageRenderer.class).setImage(guiCtrl.nifty.createImage(backgroundPath, false));
    }
    
    protected void enable(String elementName){
        guiCtrl.getElement(elementName).enable();
    }
    
    protected Slider getSlider(String id){
        return guiCtrl.getElement(id).getAttachedInputControl().getNiftyControl(Slider.class);
    }
}
