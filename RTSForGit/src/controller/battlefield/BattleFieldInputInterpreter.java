package controller.battlefield;

import view.View;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.renderer.Camera;
import controller.InputInterpreter;
import geometry.Point2D;
import tools.LogUtil;
import view.math.Translator;

public class BattleFieldInputInterpreter extends InputInterpreter {
    protected final static String SWITCH_CTRL_1 = "ctrl1";
    protected final static String SWITCH_CTRL_2 = "ctrl2";
    protected final static String SWITCH_CTRL_3 = "ctrl3";

    protected final static String SELECT = "select";
    protected final static String ACTION = "action";
    protected final static String MOVE_ATTACK = "moveattack";
    protected final static String HOLD = "hold";
	
    private BattleFieldController controller;
    public Point2D selectionStartOnMap;
    public Point2D selectionStartOnScreen;

    BattleFieldInputInterpreter(InputManager im, Camera cam, View view, BattleFieldController fc) {
        super(im, cam, view);
        selector.centered = false;
        this.controller = fc;
        setMappings();
    }
    
    private void setMappings(){
        mappings = new String[]{
            SWITCH_CTRL_1,
            SWITCH_CTRL_2,
            SWITCH_CTRL_2,

            SELECT,
            ACTION,
            MOVE_ATTACK,
            HOLD,
        };
    }
    
    @Override
    protected void registerInputs() {
        inputManager.addMapping(SWITCH_CTRL_1, new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping(SWITCH_CTRL_2, new KeyTrigger(KeyInput.KEY_O));
        inputManager.addMapping(SWITCH_CTRL_3, new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping(SELECT, new MouseButtonTrigger(0));
        inputManager.addMapping(ACTION, new MouseButtonTrigger(1));
        inputManager.addMapping(MOVE_ATTACK, new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping(HOLD, new KeyTrigger(KeyInput.KEY_H));

        inputManager.addListener(this, mappings);
        
        LogUtil.logger.info("battlefield controller online");
    }

    @Override
    protected void unregisterInputs() {
        for(String s : mappings)
            if(inputManager.hasMapping(s))
                inputManager.deleteMapping(s);
        inputManager.removeListener(this);
    }
    
    

    @Override
    public void onAnalog(String name, float value, float tpf) {
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if(!isPressed)
            switch (name){
                case SWITCH_CTRL_1 : controller.notifyListeners("CTRL1"); break;
                case SWITCH_CTRL_2 : controller.notifyListeners("CTRL2"); break;
                case SWITCH_CTRL_3 : controller.notifyListeners("CTRL3"); break;
                case SELECT :
                    if(!endSelection())
                        controller.model.commander.select(selector.getSpatialLabel(), getSpatialCoord());
                    break;
                case ACTION : controller.model.commander.act(selector.getSpatialLabel(), getSpatialCoord()); break;
                case MOVE_ATTACK : controller.model.commander.setMoveAttack(); break;
                case HOLD : controller.model.commander.orderHold(); break;
            }
        else
            if(name.equals(SELECT))
                beginSelection();
    }

    private Point2D getSpatialCoord(){
        return selector.getCoord(view.rootNode);
    }

    private void beginSelection() {
        selectionStartOnMap = selector.getCoord(view.rootNode);
        selectionStartOnScreen = Translator.toPoint2D(inputManager.getCursorPosition());
    }

    private boolean endSelection() {
        boolean selectionSuccess = false;
        Point2D selectionEndOnMap = selector.getCoord(view.rootNode);
        if(selectionStartOnMap != null && selectionEndOnMap != null &&
                selectionStartOnMap.getDistance(selectionEndOnMap) > 1){
            controller.model.commander.select(selectionStartOnMap, selectionEndOnMap);
            selectionSuccess = true;
        }
        selectionStartOnScreen = null;
        return selectionSuccess;
    }
}
