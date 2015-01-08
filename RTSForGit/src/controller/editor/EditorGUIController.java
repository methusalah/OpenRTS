/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.editor;

import controller.Controller;
import controller.battlefield.*;
import controller.GUIController;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ListBoxSelectionChangedEvent;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.controls.SliderChangedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import model.Commander;
import model.Reporter;
import model.battlefield.army.components.Unit;
import model.battlefield.army.Unity;
import model.editor.Pencil;
import static model.editor.Pencil.Shape.Circle;
import model.editor.tools.AtlasTool;
import model.editor.tools.CliffTool;
import model.editor.tools.HeightTool;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class EditorGUIController extends GUIController {

    public EditorGUIController(Nifty nifty, Controller controller) {
        super(controller, nifty);
        drawer = new EditorGUIDrawer(this);
    }
    
    @Override
    public void activate(){
        nifty.gotoScreen("editor");
        nifty.update();
        askRedraw();
    }
    
    @Override
    public void update() {
        if(!nifty.getCurrentScreen().getScreenId().equals("editor"))
            throw new RuntimeException("updating editor screen but is not current screen.");
        if(redrawAsked){
            drawer.draw();
            redrawAsked = false;
        }
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {
    }
    
    @NiftyEventSubscriber(pattern=".*slider")
    public void onSliderChanged(final String id, final SliderChangedEvent event) {
        switch(id){
            case "sizeslider" : ctrl.model.toolManager.actualTool.pencil.size = event.getValue(); break;
            case "strslider" : ctrl.model.toolManager.actualTool.pencil.strength = event.getValue();break;
        }
    }
    
    @NiftyEventSubscriber(pattern=".*list")
    public void onListSelectionChanged(final String id, final ListBoxSelectionChangedEvent event) {
        List<Integer> selectionIndices = event.getSelectionIndices();
        if(selectionIndices.isEmpty())
            return;
        switch(id){
            case "selectionlist" : ctrl.model.toolManager.actualTool.getSet().set(selectionIndices.get(0)); break;
        }
    }
    
    
    
    
    
    
    
    
    
    
    public void load(){
        ctrl.model.loadBattlefield();
    }
    
    public void save(){
        ctrl.model.saveBattlefield();
    }
    
    public void newMap(){
        ctrl.model.setNewBattlefield();
    }
    
    public void toggleGrid(){
        ctrl.view.editorRend.toggleGrid();
    }
    public void setCliffTool(){
        ctrl.model.toolManager.setCliffTool();
        askRedraw();
    }
    public void setHeightTool(){
        ctrl.model.toolManager.setHeightTool();
        askRedraw();
    }
    public void setAtlasTool(){
        ctrl.model.toolManager.setAtlasTool();
        askRedraw();
    }
    public void setRampTool(){
        ctrl.model.toolManager.setRampTool();
        askRedraw();
    }
    public void setUnitTool(){
        ctrl.model.toolManager.setUnitTool();
        askRedraw();
    }
    public void setTrincketTool(){
        ctrl.model.toolManager.setTrinketTool();
        askRedraw();
    }

    public void setOperation(String indexString){
        ctrl.model.toolManager.actualTool.setOperation(Integer.parseInt(indexString));
        askRedraw();
    }
    
    public void setSet(String indexString){
        if(ctrl.model.toolManager.actualTool.hasSet())
            ctrl.model.toolManager.actualTool.getSet().set(Integer.parseInt(indexString));
        askRedraw();
    }
    
    public void setRoughMode(){
        ctrl.model.toolManager.actualTool.pencil.setRoughMode();
        askRedraw();
    }
    public void setAirbrushMode(){
        ctrl.model.toolManager.actualTool.pencil.setAirbrushMode();
        askRedraw();
    }
    public void setNoiseMode(){
        ctrl.model.toolManager.actualTool.pencil.setNoiseMode();
        askRedraw();
    }
    public void setSquareShape(){
        ctrl.model.toolManager.actualTool.pencil.setSquareShape();
        askRedraw();
    }
    public void setDiamondShape(){
        ctrl.model.toolManager.actualTool.pencil.setDiamondShape();
        askRedraw();
    }
    public void setCircleShape(){
        ctrl.model.toolManager.actualTool.pencil.setCircleShape();
        askRedraw();
    }
}
