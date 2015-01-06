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
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.controls.SliderChangedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import java.awt.Color;
import java.util.ArrayList;
import model.Commander;
import model.Reporter;
import model.army.data.Unit;
import model.army.Unity;
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

    boolean toRefresh = false;
    
    public EditorGUIController(Nifty nifty, Controller controller) {
        super(controller, nifty);
        drawer = new EditorGUIDrawer(this);
    }
    
    @Override
    public void activate(){
        nifty.gotoScreen("editor");
        drawer.askRedraw();
    }
    
    @Override
    public void update() {
        if(!nifty.getCurrentScreen().getScreenId().equals("editor")){
            LogUtil.logger.info("updating editor gui but screen's not ready.");
            return;
        }
        drawer.update();
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
    
    
    
    
    
    
    
    
    
    
    public void load(){
        ctrl.model.loadBattlefield();
    }
    
    public void save(){
        ctrl.model.saveBattlefield();
    }
    
    public void newMap(){
        ctrl.model.setNewBattlefield();
    }
    public void setCliffTool(){
        ctrl.model.toolManager.setCliffTool();
        drawer.askRedraw();
    }
    public void setHeightTool(){
        ctrl.model.toolManager.setHeightTool();
        drawer.askRedraw();
    }
    public void setAtlasTool(){
        ctrl.model.toolManager.setAtlasTool();
        drawer.askRedraw();
    }
    public void setRampTool(){
        ctrl.model.toolManager.setRampTool();
        drawer.askRedraw();
    }
    public void setUnitTool(){
        ctrl.model.toolManager.setUnitTool();
        drawer.askRedraw();
    }

    public void setOperation(String indexString){
        ctrl.model.toolManager.actualTool.setOperation(Integer.parseInt(indexString));
        drawer.askRedraw();
    }
    
    public void setSet(String indexString){
        if(ctrl.model.toolManager.actualTool.hasSet())
            ctrl.model.toolManager.actualTool.getSet().set(Integer.parseInt(indexString));
        drawer.askRedraw();
    }
    
    public void setRoughMode(){
        ctrl.model.toolManager.actualTool.pencil.setRoughMode();
        drawer.askRedraw();
    }
    public void setAirbrushMode(){
        ctrl.model.toolManager.actualTool.pencil.setAirbrushMode();
        drawer.askRedraw();
    }
    public void setNoiseMode(){
        ctrl.model.toolManager.actualTool.pencil.setNoiseMode();
        drawer.askRedraw();
    }
    public void setSquareShape(){
        ctrl.model.toolManager.actualTool.pencil.setSquareShape();
        drawer.askRedraw();
    }
    public void setDiamondShape(){
        ctrl.model.toolManager.actualTool.pencil.setDiamondShape();
        drawer.askRedraw();
    }
    public void setCircleShape(){
        ctrl.model.toolManager.actualTool.pencil.setCircleShape();
        drawer.askRedraw();
    }
}
