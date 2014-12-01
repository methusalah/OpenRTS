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
import model.map.cliff.editor.MapEditor;
import tools.LogUtil;
import view.math.Translator;

public class EditorInputInterpreter extends InputInterpreter {
	
    MapEditor editor;
    private EditorController controller;

    protected final static String leftclic = "lc";
    protected final static String rightclic = "rc";
    protected final static String Selection_Shape = "selectionshape";
    protected final static String Selection_Radius_Inc = "selectionradius+";
    protected final static String Selection_Radius_Dec = "selectionradius-";
    protected final static String r = "r";
    protected final static String f = "f";
    protected final static String GridDisplay = "GridDisplay";

    EditorInputInterpreter(InputManager im, Camera cam, MapEditor editor, View view, EditorController fc) {
        super(im, cam, view);
        this.editor = editor;
        selector.centered = false;
        this.controller = fc;
    }

    @Override
    protected void registerInputs() {
            String[] mappings = new String[]{
                            leftclic,
                            rightclic,
                            Selection_Shape,
                            Selection_Radius_Inc,
                            Selection_Radius_Dec,
                            r,
                            f,
                            GridDisplay,
                            
            };
            inputManager.addMapping(leftclic, new MouseButtonTrigger(0));
            inputManager.addMapping(rightclic, new MouseButtonTrigger(1));
            inputManager.addMapping(Selection_Shape, new KeyTrigger(KeyInput.KEY_A));
            inputManager.addMapping(Selection_Radius_Inc, new KeyTrigger(KeyInput.KEY_Q));
            inputManager.addMapping(Selection_Radius_Dec, new KeyTrigger(KeyInput.KEY_W));
            inputManager.addMapping(r, new KeyTrigger(KeyInput.KEY_R));
            inputManager.addMapping(f, new KeyTrigger(KeyInput.KEY_F));
            inputManager.addMapping(GridDisplay, new KeyTrigger(KeyInput.KEY_G));


            inputManager.addListener(this, mappings);
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if(!isActive)
            return;
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals(leftclic) && !isPressed){
                editor.levelUp(getSpatialCoord());
        } else if (name.equals(rightclic) && !isPressed){
                editor.levelDown(getSpatialCoord());
        } else if (name.equals(Selection_Shape) && !isPressed){
                editor.selector.toggleShape();
        } else if (name.equals(Selection_Radius_Inc) && !isPressed){
                editor.selector.incRadius();
        } else if (name.equals(Selection_Radius_Dec) && !isPressed){
                editor.selector.decRadius();
        } else if (name.equals(r) && !isPressed){
                editor.incHeight(getSpatialCoord());
        } else if (name.equals(f) && !isPressed){
                editor.decHeight(getSpatialCoord());
        } else if (name.equals(GridDisplay) && !isPressed){
                controller.view.mapRend.toggleGrid();
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
        return selector.getCoord(view.mapRend.gridNode);
    }
}
