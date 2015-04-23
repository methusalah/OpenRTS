/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.Color;
import java.util.List;

import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;

/**
 *
 * @author Benoît
 */
public abstract class GUIDrawer {
    
    protected GUIController guiCtrl;
    
    public GUIDrawer(GUIController guiCtrl) {
        this.guiCtrl = guiCtrl;
    }
    
    protected Element getElement(String id){
        return guiCtrl.getElement(id);
    }
    
    public abstract void draw();
    
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
    
    protected void setButtonCustomEffet(String id, boolean val){
        Element e = guiCtrl.getElement(id);
        if(e == null)
            throw new IllegalArgumentException("can't find button '"+id+"'.");
        if(val)
            e.startEffect(EffectEventId.onCustom);
        else
            e.stopEffect(EffectEventId.onCustom);
    }
    
    protected void fillList(String id, List<String> strings){
        ListBox lb = guiCtrl.getControl(id, ListBox.class);
        List<Integer> selected = lb.getSelectedIndices();
        int index;
        if(!selected.isEmpty())
            index = selected.get(0);
        else
            index = 0;
        lb.clear();
        lb.addAllItems(strings);
        lb.selectItemByIndex(index);
    }

    protected void fillDropDown(String id, List<String> strings, int index){
        DropDown dd = guiCtrl.getControl(id, DropDown.class);
        dd.disable();
        dd.clear();
        dd.addAllItems(strings);
        dd.selectItemByIndex(index);
        dd.enable();
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
