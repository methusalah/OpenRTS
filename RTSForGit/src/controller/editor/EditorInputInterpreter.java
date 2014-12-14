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
import java.util.logging.Level;
import java.util.logging.Logger;
import math.MyRandom;
import model.Commander;
import model.lighting.SunLight;
import model.map.MapFactory;
import model.map.editor.MapToolManager;
import tools.LogUtil;
import view.math.Translator;

public class EditorInputInterpreter extends InputInterpreter {
	
    MapToolManager toolManager;
    SunLight sunLight;
    private EditorController controller;

    
    protected final static String PRIMARY_ACTION = "lc";
    protected final static String SECONDARY_ACTION = "rc";
    protected final static String TOGGLE_GRID = "GridDisplay";

    protected final static String SET_CLIFF_TOOL = "setclifftool";
    protected final static String SET_HEIGHT_TOOL = "setheighttool";
    protected final static String SET_ATLAS_TOOL = "setatlastool";
    protected final static String TOGGLE_PENCIL_SHAPE = "pencilshape";
    protected final static String TOGGLE_PENCIL_MODE = "pencilmode";
    protected final static String INC_SELECTOR_RADIUS = "selectorradius+";
    protected final static String DEC_SELECTOR_RADIUS = "selectorradius-";

    protected final static String TOGGLE_SET = "toggleset";

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

    protected final static String SAVE = "save";
    protected final static String LOAD = "load";
    protected final static String NEW = "new";

    
    EditorInputInterpreter(InputManager im, Camera cam, MapToolManager editor, SunLight sunLight, View view, EditorController fc) {
        super(im, cam, view);
        this.toolManager = editor;
        this.sunLight = sunLight;
        selector.centered = false;
        this.controller = fc;
    }

    @Override
    protected void registerInputs() {
            String[] mappings = new String[]{
                            PRIMARY_ACTION,
                            SECONDARY_ACTION,
                            TOGGLE_PENCIL_SHAPE,
                            TOGGLE_PENCIL_MODE,
                            INC_SELECTOR_RADIUS,
                            DEC_SELECTOR_RADIUS,
                            SET_CLIFF_TOOL,
                            SET_HEIGHT_TOOL,
                            SET_ATLAS_TOOL,
                            TOGGLE_GRID,
                            TOGGLE_SET,
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
                            RESET_COLOR,
                            SAVE,
                            LOAD,
                            NEW,
            };
            inputManager.addMapping(PRIMARY_ACTION, new MouseButtonTrigger(0));
            inputManager.addMapping(SECONDARY_ACTION, new MouseButtonTrigger(1));
            
            inputManager.addMapping(TOGGLE_PENCIL_SHAPE, new KeyTrigger(KeyInput.KEY_A));
            inputManager.addMapping(TOGGLE_PENCIL_MODE, new KeyTrigger(KeyInput.KEY_Z));
            inputManager.addMapping(INC_SELECTOR_RADIUS, new KeyTrigger(KeyInput.KEY_Q));
            inputManager.addMapping(DEC_SELECTOR_RADIUS, new KeyTrigger(KeyInput.KEY_W));
            inputManager.addMapping(SET_CLIFF_TOOL, new KeyTrigger(KeyInput.KEY_1));
            inputManager.addMapping(SET_HEIGHT_TOOL, new KeyTrigger(KeyInput.KEY_2));
            inputManager.addMapping(SET_ATLAS_TOOL, new KeyTrigger(KeyInput.KEY_3));

            inputManager.addMapping(TOGGLE_GRID, new KeyTrigger(KeyInput.KEY_G));
            inputManager.addMapping(TOGGLE_SET, new KeyTrigger(KeyInput.KEY_E));

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
            inputManager.addMapping(SAVE, new KeyTrigger(KeyInput.KEY_F5));
            inputManager.addMapping(LOAD, new KeyTrigger(KeyInput.KEY_F9));
            inputManager.addMapping(NEW, new KeyTrigger(KeyInput.KEY_F12));

            inputManager.addListener(this, mappings);
            
            LogUtil.logger.info("------ Map editing (ZQSD zone)");
            LogUtil.logger.info(" '1' for cliff tool, '2' for height tool, '3' for atlas tool");
            LogUtil.logger.info(" 'e' toggle between sets for the actual tool.");
            LogUtil.logger.info(" Pencil : 'a' toggle between shapes (square, diamond and circle)");
            LogUtil.logger.info("          'z' toggle between modes (rough, brush and noise)");
            LogUtil.logger.info("          'q' & 'w' increase/decrease radius");
            LogUtil.logger.info("");
            LogUtil.logger.info("------ Lighting (numpad)");
            LogUtil.logger.info(" '7' toggle between sunlight components : sunlight/shadowcaster/both/ambient");
            LogUtil.logger.info(" '8' & '5' increase/decrease daytime");
            LogUtil.logger.info(" '4' & '6' rotate compass");
            LogUtil.logger.info(" '1', '2' & '3' decrease red, green and blue component");
            LogUtil.logger.info(" '0' reset color");
            LogUtil.logger.info(" + & - change intensity");
            LogUtil.logger.info("");
            LogUtil.logger.info("------- General");
            LogUtil.logger.info("F5 to save, F9 to load, F12 for a new map");
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        switch(name){
            case PRIMARY_ACTION : toolManager.primaryAction(); break;
            case SECONDARY_ACTION : toolManager.secondaryAction(); break;
            case INC_DAYTIME : sunLight.incDayTime(); break;
            case DEC_DAYTIME : sunLight.decDayTime(); break;
            case COMPASS_EAST : sunLight.turnCompassEast(); break;
            case COMPASS_WEST : sunLight.turnCompasWest(); break;
            case INC_INTENSITY : sunLight.incIntensity(); break;
            case DEC_INTENSITY : sunLight.decIntensity(); break;
            case DEC_RED : sunLight.decRed(); break;
            case DEC_GREEN : sunLight.decGreen(); break;
            case DEC_BLUE : sunLight.decBlue(); break;
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if(!isPressed)
            switch(name){
                case TOGGLE_PENCIL_SHAPE : toolManager.pencil.toggleShape(); break;
                case TOGGLE_PENCIL_MODE : toolManager.pencil.toggleMode(); break;
                case INC_SELECTOR_RADIUS : toolManager.pencil.incRadius(); break;
                case DEC_SELECTOR_RADIUS : toolManager.pencil.decRadius(); break;
                case SET_CLIFF_TOOL : toolManager.setCliffTool(); break;
                case SET_HEIGHT_TOOL : toolManager.setHeightTool(); break;
                case SET_ATLAS_TOOL : toolManager.setAtlasTool(); break;
                case TOGGLE_SET : toolManager.toggleSet(); break;
                case TOGGLE_GRID : controller.view.editorRend.toggleGrid(); break;
                case TOGGLE_LIGHT_COMP : sunLight.toggleLight(); break;
                case TOGGLE_SPEED : sunLight.toggleSpeed(); break;
                case RESET_COLOR : sunLight.resetColor(); break;
                case SAVE : MapFactory.save(toolManager.map); break;
                case LOAD : controller.model.map = MapFactory.load(); break;
//                case NEW : controller.model.map = MapFactory.getNew(128, 128); break;
            }
    }
}
