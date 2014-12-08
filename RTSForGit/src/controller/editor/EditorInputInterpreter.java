package controller.editor;

import controller.battlefield.*;
import view.View;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import controller.InputInterpreter;
import geometry.Point2D;
import math.MyRandom;
import model.Commander;
import model.map.editor.MapToolManager;
import tools.LogUtil;
import view.math.Translator;

public class EditorInputInterpreter extends InputInterpreter {
	
    MapToolManager editor;
    private EditorController controller;

    
    protected final static String PRIMARY_ACTION = "lc";
    protected final static String SECONDARY_ACTION = "rc";
    protected final static String TOGGLE_GRID = "GridDisplay";

    protected final static String SET_CLIFF_TOOL = "setclifftool";
    protected final static String SET_HEIGHT_TOOL = "setheighttool";
    protected final static String TOGGLE_SELECTOR_SHAPE = "selectorshape";
    protected final static String INC_SELECTOR_RADIUS = "selectorradius+";
    protected final static String DEC_SELECTOR_RADIUS = "selectorradius-";

    protected final static String TOGGLE_CLIFF_SHAPE = "togglecliffshape";

    protected final static String INC_AIRBRUSH_FALLOF = "incairbrushfallof";
    protected final static String DEC_AIRBRUSH_FALLOF = "decairbrushfallof";
    
    
    EditorInputInterpreter(InputManager im, Camera cam, MapToolManager editor, View view, EditorController fc) {
        super(im, cam, view);
        this.editor = editor;
        selector.centered = false;
        this.controller = fc;
    }

    @Override
    protected void registerInputs() {
            String[] mappings = new String[]{
                            PRIMARY_ACTION,
                            SECONDARY_ACTION,
                            TOGGLE_SELECTOR_SHAPE,
                            INC_SELECTOR_RADIUS,
                            DEC_SELECTOR_RADIUS,
                            SET_CLIFF_TOOL,
                            SET_HEIGHT_TOOL,
                            TOGGLE_GRID,
                            TOGGLE_CLIFF_SHAPE,
                            INC_AIRBRUSH_FALLOF,
                            DEC_AIRBRUSH_FALLOF,
                            
            };
            inputManager.addMapping(PRIMARY_ACTION, new MouseButtonTrigger(0));
            inputManager.addMapping(SECONDARY_ACTION, new MouseButtonTrigger(1));
            
            inputManager.addMapping(TOGGLE_SELECTOR_SHAPE, new KeyTrigger(KeyInput.KEY_A));
            inputManager.addMapping(INC_SELECTOR_RADIUS, new KeyTrigger(KeyInput.KEY_Q));
            inputManager.addMapping(DEC_SELECTOR_RADIUS, new KeyTrigger(KeyInput.KEY_W));
            inputManager.addMapping(SET_CLIFF_TOOL, new KeyTrigger(KeyInput.KEY_1));
            inputManager.addMapping(SET_HEIGHT_TOOL, new KeyTrigger(KeyInput.KEY_2));
            
            inputManager.addMapping(TOGGLE_GRID, new KeyTrigger(KeyInput.KEY_G));
            inputManager.addMapping(TOGGLE_CLIFF_SHAPE, new KeyTrigger(KeyInput.KEY_Z));


            inputManager.addListener(this, mappings);
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
//        if(!isActive)
//            return;
        if (name.equals(PRIMARY_ACTION)){
                editor.primaryAction();
        } else if (name.equals(SECONDARY_ACTION)){
                editor.secondaryAction();
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals(TOGGLE_SELECTOR_SHAPE) && !isPressed){
                editor.selector.toggleShape();
        } else if (name.equals(INC_SELECTOR_RADIUS) && !isPressed){
                editor.selector.incRadius();
        } else if (name.equals(DEC_SELECTOR_RADIUS) && !isPressed){
                editor.selector.decRadius();
        } else if (name.equals(SET_CLIFF_TOOL) && !isPressed){
                editor.setCliffTool();
        } else if (name.equals(SET_HEIGHT_TOOL) && !isPressed){
                editor.setHeightTool();
        } else if (name.equals(TOGGLE_CLIFF_SHAPE) && !isPressed){
                editor.cliffTool.swichCliff();
        } else if (name.equals(TOGGLE_GRID) && !isPressed){
                controller.view.editorRend.toggleGrid();
        }
    }

    private String getSpatialLabel(){
        Spatial s = selector.getGeometry(view.rootNode);
        while(true){
            if(s == null || s.getName() == null)
                return null;
            if(s.getName().startsWith("label"))
                return s.getName();
            s = s.getParent();
            if(s == null)
                return null;
        }
    }
        
    private Point2D getSpatialCoord(){
        return selector.getCoord(view.editorRend.gridNode);
    }
}
