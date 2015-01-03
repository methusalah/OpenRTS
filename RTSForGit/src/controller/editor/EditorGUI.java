/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.editor;

import controller.Controller;
import controller.battlefield.*;
import controller.GUI;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import java.util.ArrayList;
import model.Commander;
import model.Reporter;
import model.army.data.Unit;
import model.army.Unity;
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
    
    private void askRefresh(){
        toRefresh = true;
    }
    private void refresh(){
        toRefresh = false;
        setText("operation0", getOperationName(0));
        setText("operation1", getOperationName(1));
        setText("operation2", getOperationName(2));
    }
    
    private String getOperationName(int index){
        return ctrl.model.toolManager.actualTool.getOperationName(index);
    }
    
    public void setOperation(String indexString){
        ctrl.model.toolManager.actualTool.setOperation(Integer.parseInt(indexString));
    }
}
