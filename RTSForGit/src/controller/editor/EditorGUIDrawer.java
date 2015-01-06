/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.editor;

import controller.GUIController;
import controller.GUIDrawer;
import de.lessvoid.nifty.controls.Slider;
import java.awt.Color;
import model.editor.Pencil;
import static model.editor.Pencil.Shape.Circle;
import static model.editor.Pencil.Shape.Diamond;
import static model.editor.Pencil.Shape.Square;
import model.editor.tools.AtlasTool;
import model.editor.tools.CliffTool;
import model.editor.tools.HeightTool;
import model.editor.tools.RampTool;
import model.editor.tools.Tool;
import model.editor.tools.UnitTool;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class EditorGUIDrawer extends GUIDrawer{
    private static final String OPERATION_BUTTON_ID_PREFIX = "operation";
    private static final String CLIFF_TOOL_BUTTON_ID = "clifftool";
    private static final String HEIGHT_TOOL_BUTTON_ID = "heighttool";
    private static final String ATLAS_TOOL_BUTTON_ID = "atlastool";
    private static final String RAMP_TOOL_BUTTON_ID = "ramptool";
    private static final String UNIT_TOOL_BUTTON_ID = "unittool";
    private static final String SET_PANEL_ID = "setpanel";
    private static final String SET_BUTTON_ID_PREFIX = "set";
    

    public EditorGUIDrawer(GUIController guiCtrl) {
        super(guiCtrl);
    }

    @Override
    protected void draw() {
        drawToolPanel();
        drawOperationPanel();
        drawSetPanel();
        drawPencilPanel();
    }
    
    private void drawToolPanel(){
        Tool tool = guiCtrl.ctrl.model.toolManager.actualTool;
        if(tool instanceof CliffTool)
            maintainButton(CLIFF_TOOL_BUTTON_ID);
        else
            releaseButton(CLIFF_TOOL_BUTTON_ID);

        if(tool instanceof HeightTool)
            maintainButton(HEIGHT_TOOL_BUTTON_ID);
        else
            releaseButton(HEIGHT_TOOL_BUTTON_ID);
        
        if(tool instanceof AtlasTool)
            maintainButton(ATLAS_TOOL_BUTTON_ID);
        else
            releaseButton(ATLAS_TOOL_BUTTON_ID);
        
        if(tool instanceof RampTool)
            maintainButton(RAMP_TOOL_BUTTON_ID);
        else
            releaseButton(RAMP_TOOL_BUTTON_ID);
        
        if(tool instanceof UnitTool)
            maintainButton(UNIT_TOOL_BUTTON_ID);
        else
            releaseButton(UNIT_TOOL_BUTTON_ID);
    }
    
    private void drawOperationPanel(){
        Tool tool = guiCtrl.ctrl.model.toolManager.actualTool;
        for(int i=0; i<3; i++)
            if(!tool.getOperation(i).isEmpty()){
                getElement(OPERATION_BUTTON_ID_PREFIX+i).show();
                changeButtonText(OPERATION_BUTTON_ID_PREFIX+i, tool.getOperation(i));
                if(tool.getActualOperation().equals(tool.getOperation(i)))
                    maintainButton(OPERATION_BUTTON_ID_PREFIX+i);
                else
                    releaseButton(OPERATION_BUTTON_ID_PREFIX+i);
            } else
                getElement(OPERATION_BUTTON_ID_PREFIX+i).hide();
                
    }
    
    private void drawSetPanel(){
        Tool tool = guiCtrl.ctrl.model.toolManager.actualTool;
        if(tool.hasSet()){
            getElement(SET_PANEL_ID).show();
            for(int i=0; i<8; i++){
                if(i < tool.getSet().getCount()){
                    getElement(SET_BUTTON_ID_PREFIX+i).show();
                    setBackground(SET_BUTTON_ID_PREFIX+i, tool.getSet().getIcon(i));
                    if(tool.getSet().actual == i){
                        changeButtonText(SET_BUTTON_ID_PREFIX+i, "o");
                        maintainButton(SET_BUTTON_ID_PREFIX+i);
                    } else {
                        changeButtonText(SET_BUTTON_ID_PREFIX+i, "");
                        releaseButton(SET_BUTTON_ID_PREFIX+i);
                    }
                } else
                    getElement(SET_BUTTON_ID_PREFIX+i).hide();
            }
        } else
            getElement(SET_PANEL_ID).hide();
    }
    
    private void drawPencilPanel(){
        getElement("pencilpanel").hide();
        
        Pencil pencil = guiCtrl.ctrl.model.toolManager.actualTool.pencil;
        
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
    
    private void maintainButton(String id){
        setButtonPanelCustomEffet(id, true);
        changeButtonTextColor(id, Color.green);
    }
    private void releaseButton(String id){
        setButtonPanelCustomEffet(id, true);
        changeButtonTextColor(id, Color.white);
    }

    
    
}
