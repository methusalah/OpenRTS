/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.editor;

import controller.Controller;
import controller.battlefield.*;
import controller.GUI;
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
public class EditorGUI extends GUI {

    boolean toRefresh = false;
    
    public EditorGUI(Nifty nifty, Controller controller) {
        super(controller, nifty);
        
    }
    
    @Override
    public void activate(){
        nifty.gotoScreen("editor");
        askRefresh();
    }
    
    @Override
    public void update() {
        if(!nifty.getCurrentScreen().getScreenId().equals("editor")){
            LogUtil.logger.info("updating editor gui but screen's not ready.");
            return;
        }
        if(toRefresh)
            refresh();
        
        String n = System.getProperty("line.separator");
        
//        LogUtil.logger.info("value = "+getElement("sizeslider").getAttachedInputControl().getNiftyControl(Slider.class).getValue());
//        setText("operation2", "pointed : "+ctrl.model.toolManager.pointedSpatialLabel);

//        // update unities
//        unities = c.getUnitiesInContext();
//        // Unity selectors
//        for(int i=0; i<5; i++){
//            if(i > unities.size()-1){
//                if(getElement("psel"+i).isVisible())
//                    getElement("psel"+i).hide();
//            } else {
//                if(!getElement("psel"+i).isVisible())
//                    getElement("psel"+i).show();
//                if(getElement("sel"+i) != null && getElement("sel"+i).getRenderer(TextRenderer.class)!=null){
//                    Unity u = unities.get(i);
//                    getElement("sel"+i).getRenderer(TextRenderer.class).setText(u.get(0).UIName+n+u.size());
//                }
//            }
//        }
        
        // update info
//        if(c.selection.size() == 1){
//            Unit u = c.selection.get(0);
//            getElement("unitName").getRenderer(TextRenderer.class).setText(Reporter.getName(u));
//            getElement("unitHealth").getRenderer(TextRenderer.class).setText(Reporter.getHealth(u));
//            getElement("unitState").getRenderer(TextRenderer.class).setText(Reporter.getState(u));
//            getElement("unitOrder").getRenderer(TextRenderer.class).setText(Reporter.getOrder(u));
//            getElement("unitHolding").getRenderer(TextRenderer.class).setText(Reporter.getHolding(u));
//            getElement("info").show();
//        } else
//            getElement("info").hide();

            
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
    
    private void askRefresh(){
        toRefresh = true;
    }
    private void refresh(){
        toRefresh = false;
        
        drawOperationPanel();
        drawSetPanel();
        drawPencilPanel();
    }
    
    private void drawOperationPanel(){
        changeButtonText("operation0", getOperationName(0));
        changeButtonText("operation1", getOperationName(1));
        changeButtonText("operation2", getOperationName(2));
    }
    
    private void drawSetPanel(){
        getElement("setpanel").hide();
        if(!ctrl.model.toolManager.actualTool.hasSet())
            return;
        getElement("setpanel").show();
        for(int i=0;i<8; i++){
            if(i<ctrl.model.toolManager.actualTool.getSet().getCount())
                setBackground("set"+i, ctrl.model.toolManager.actualTool.getSet().getIcon(i));
            else
                getElement("set"+i).hide();
        }
    }
    
    private void drawPencilPanel(){
        getElement("pencilpanel").hide();
        
        Pencil pencil = ctrl.model.toolManager.actualTool.pencil;
        
        if(pencil.sizeIncrement != 0){
            getElement("pencilpanel").show();
            
            switch(pencil.shape){
                case Circle :
                    releaseButton("square");
                    releaseButton("diamond");
                    maintainButton("circle");
                    break;
                case Square :
                    releaseButton("circle");
                    releaseButton("diamond");
                    maintainButton("square");
                    break;
                case Diamond :
                    releaseButton("square");
                    releaseButton("circle");
                    maintainButton("diamond");
                    break;
            }
            
            Slider sizeSlider = getSlider("sizeslider");
            sizeSlider.setMin((float)pencil.sizeIncrement);
            sizeSlider.setMax((float)Pencil.MAX_SIZE);
            sizeSlider.setStepSize((float)pencil.sizeIncrement);
            sizeSlider.setValue((float)pencil.size);
            
            if(pencil.mode == Pencil.Mode.Unique)
                getElement("pencilmodepanel").hide();
            else
                getElement("pencilmodepanel").show();
            
            if(pencil.strengthIncrement == 0)
                getElement("strpanel").hide();
            else {
                getElement("strpanel").show();
                Slider strengthSlider = getSlider("strslider");
                strengthSlider.setMin(0.1f);
                strengthSlider.setMax(1);
                strengthSlider.setStepSize((float)pencil.strengthIncrement);
                strengthSlider.setValue((float)pencil.strength);
            }
        }
    }
    
    private String getOperationName(int index){
        return ctrl.model.toolManager.actualTool.getOperationName(index);
    }
    
    @NiftyEventSubscriber(pattern=".*slider")
    public void onSliderChanged(final String id, final SliderChangedEvent event) {
        switch(id){
            case "sizeslider" : ctrl.model.toolManager.actualTool.pencil.size = event.getValue(); break;
            case "strslider" : ctrl.model.toolManager.actualTool.pencil.strength = event.getValue();break;
        }
    }
    
    private void maintainButton(String id){
        changeButtonTextColor(id, Color.GREEN);
    }
    private void releaseButton(String id){
        changeButtonTextColor(id, Color.WHITE);
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
        askRefresh();
    }
    public void setHeightTool(){
        ctrl.model.toolManager.setHeightTool();
        askRefresh();
    }
    public void setAtlasTool(){
        ctrl.model.toolManager.setAtlasTool();
        askRefresh();
    }
    public void setRampTool(){
        ctrl.model.toolManager.setRampTool();
        askRefresh();
    }
    public void setUnitTool(){
        ctrl.model.toolManager.setUnitTool();
        askRefresh();
    }

    public void setOperation(String indexString){
        ctrl.model.toolManager.actualTool.setOperation(Integer.parseInt(indexString));
        askRefresh();
    }
    
    public void setSet(String indexString){
        if(ctrl.model.toolManager.actualTool.hasSet())
            ctrl.model.toolManager.actualTool.getSet().set(Integer.parseInt(indexString));
        askRefresh();
    }
    
    public void setRoughMode(){
        ctrl.model.toolManager.actualTool.pencil.setRoughMode();
        askRefresh();
    }
    public void setAirbrushMode(){
        ctrl.model.toolManager.actualTool.pencil.setAirbrushMode();
        askRefresh();
    }
    public void setNoiseMode(){
        ctrl.model.toolManager.actualTool.pencil.setNoiseMode();
        askRefresh();
    }
    public void setSquareShape(){
        ctrl.model.toolManager.actualTool.pencil.setSquareShape();
        askRefresh();
    }
    public void setDiamondShape(){
        ctrl.model.toolManager.actualTool.pencil.setDiamondShape();
        askRefresh();
    }
    public void setCircleShape(){
        ctrl.model.toolManager.actualTool.pencil.setCircleShape();
        askRefresh();
    }
}
