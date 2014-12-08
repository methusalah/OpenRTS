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
import model.lighting.SunLight;
import model.map.editor.MapToolManager;
import tools.LogUtil;
import view.math.Translator;

public class EditorInputInterpreter extends InputInterpreter {
	
    MapToolManager editor;
    SunLight sunLight;
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

    protected final static String TOGGLE_LIGHT_COMP = "togglelightcomp";
    protected final static String INC_DAYTIME = "incdaytime";
    protected final static String DEC_DAYTIME = "decdaytime";
    protected final static String COMPASS_EAST = "compasseast";
    protected final static String COMPASS_WEST = "compasswest";
    protected final static String INC_INTENSITY = "incintensity";
    protected final static String DEC_INTENSITY = "decintensity";
    protected final static String TOGGLE_SPEED = "togglespeed";
    protected final static String DEC_RED = "decred";
    protected final static String DEC_GREEN = "decgreen";
    protected final static String DEC_BLUE = "decblue";
    protected final static String RESET_COLOR = "resetcolor";
    
    

    
    
    
    EditorInputInterpreter(InputManager im, Camera cam, MapToolManager editor, SunLight sunLight, View view, EditorController fc) {
        super(im, cam, view);
        this.editor = editor;
        this.sunLight = sunLight;
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
                            
                            TOGGLE_LIGHT_COMP,
                            INC_DAYTIME,
                            DEC_DAYTIME,
                            COMPASS_EAST,
                            COMPASS_WEST,
                            INC_INTENSITY,
                            DEC_INTENSITY,
                            TOGGLE_SPEED,
                            DEC_RED, 
                            DEC_GREEN,
                            DEC_BLUE,
                            RESET_COLOR
                            
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

            inputManager.addMapping(TOGGLE_LIGHT_COMP, new KeyTrigger(KeyInput.KEY_NUMPAD7));
            inputManager.addMapping(INC_DAYTIME, new KeyTrigger(KeyInput.KEY_NUMPAD8));
            inputManager.addMapping(DEC_DAYTIME, new KeyTrigger(KeyInput.KEY_NUMPAD5));
            inputManager.addMapping(COMPASS_EAST, new KeyTrigger(KeyInput.KEY_NUMPAD6));
            inputManager.addMapping(COMPASS_WEST, new KeyTrigger(KeyInput.KEY_NUMPAD4));
            inputManager.addMapping(INC_INTENSITY, new KeyTrigger(KeyInput.KEY_ADD));
            inputManager.addMapping(DEC_INTENSITY, new KeyTrigger(KeyInput.KEY_SUBTRACT));
            inputManager.addMapping(TOGGLE_SPEED, new KeyTrigger(KeyInput.KEY_NUMPAD9));
            inputManager.addMapping(DEC_RED, new KeyTrigger(KeyInput.KEY_NUMPAD1));
            inputManager.addMapping(DEC_GREEN, new KeyTrigger(KeyInput.KEY_NUMPAD2));
            inputManager.addMapping(DEC_BLUE, new KeyTrigger(KeyInput.KEY_NUMPAD3));
            inputManager.addMapping(RESET_COLOR, new KeyTrigger(KeyInput.KEY_NUMPAD0));

            inputManager.addListener(this, mappings);
            
            LogUtil.logger.info("------ Map editing (ZQSD zone)");
            LogUtil.logger.info(" '1' for cliff tool, '2' for height tool");
            LogUtil.logger.info(" 'z' toggle between preset cliff styles");
            LogUtil.logger.info(" Area selector : 'a' toggle between shapes (square, diamond and circle)");
            LogUtil.logger.info("                 'q' & 'w' increase/decrease radius ");
            LogUtil.logger.info("");
            LogUtil.logger.info("------ Lighting (numpad)");
            LogUtil.logger.info(" '7' toggle between sunlight components : sunlight/shadowcaster/both/ambient");
            LogUtil.logger.info(" '8' & '5' increase/decrease daytime");
            LogUtil.logger.info(" '4' & '6' rotate compass");
            LogUtil.logger.info(" '1', '2' & '3' decrease red, green and blue component");
            LogUtil.logger.info(" '0' reset color");
            LogUtil.logger.info(" + & - change intensity");
            LogUtil.logger.info("");
            LogUtil.logger.info("");
            LogUtil.logger.info("");
            LogUtil.logger.info("");
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
//        if(!isActive)
//            return;
        if (name.equals(PRIMARY_ACTION)){
                editor.primaryAction();
        } else if (name.equals(SECONDARY_ACTION)){
                editor.secondaryAction();
        } else if (name.equals(INC_DAYTIME)){
            sunLight.incDayTime();
        } else if (name.equals(DEC_DAYTIME)){
            sunLight.decDayTime();
        } else if (name.equals(COMPASS_EAST)){
            sunLight.turnCompassEast();
        } else if (name.equals(COMPASS_WEST)){
            sunLight.turnCompasWest();
        } else if (name.equals(INC_INTENSITY)){
            sunLight.incIntensity();
        } else if (name.equals(DEC_INTENSITY)){
            sunLight.decIntensity();
        } else if (name.equals(DEC_RED)){
            sunLight.decRed();
        } else if (name.equals(DEC_GREEN)){
            sunLight.decGreen();
        } else if (name.equals(DEC_BLUE)){
            sunLight.decBlue();
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
        } else if (name.equals(TOGGLE_LIGHT_COMP) && !isPressed){
            sunLight.toggleLight();
        } else if (name.equals(TOGGLE_SPEED) && !isPressed){
            sunLight.toggleSpeed();
        } else if (name.equals(RESET_COLOR) && !isPressed){
            sunLight.resetColor();
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
