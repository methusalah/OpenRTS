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

public class BattlefieldInputInterpreter extends InputInterpreter {
    protected final static String SWITCH_CTRL_1 = "ctrl1";
    protected final static String SWITCH_CTRL_2 = "ctrl2";
    protected final static String SWITCH_CTRL_3 = "ctrl3";

    protected final static String SELECT = "select";
    protected final static String ACTION = "action";
    protected final static String MOVE_ATTACK = "moveattack";
    protected final static String HOLD = "hold";
	
    public Point2D selectionStartOnMap;
    public Point2D selectionStartOnScreen;

    BattlefieldInputInterpreter(BattlefieldController controller) {
        super(controller);
        controller.spatialSelector.centered = false;
        setMappings();
    }
    
    private void setMappings(){
        mappings = new String[]{
            SWITCH_CTRL_1,
            SWITCH_CTRL_2,
            SWITCH_CTRL_3,

            SELECT,
            ACTION,
            MOVE_ATTACK,
            HOLD,
        };
    }
    
    @Override
    protected void registerInputs(InputManager inputManager) {
        inputManager.addMapping(SWITCH_CTRL_1, new KeyTrigger(KeyInput.KEY_F1));
        inputManager.addMapping(SWITCH_CTRL_2, new KeyTrigger(KeyInput.KEY_F2));
        inputManager.addMapping(SWITCH_CTRL_3, new KeyTrigger(KeyInput.KEY_F3));
        inputManager.addMapping(SELECT, new MouseButtonTrigger(0));
        inputManager.addMapping(ACTION, new MouseButtonTrigger(1));
        inputManager.addMapping(MOVE_ATTACK, new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping(HOLD, new KeyTrigger(KeyInput.KEY_H));

        inputManager.addListener(this, mappings);
        
        LogUtil.logger.info("battlefield controller online");
    }

    @Override
    protected void unregisterInputs(InputManager inputManager) {
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
                case SWITCH_CTRL_1 : ctrl.notifyListeners("CTRL1"); break;
                case SWITCH_CTRL_2 : ctrl.notifyListeners("CTRL2"); break;
                case SWITCH_CTRL_3 : ctrl.notifyListeners("CTRL3"); break;
                case SELECT :
                    if(!endSelection())
                        ctrl.model.commander.select(ctrl.spatialSelector.getSpatialLabel(), getSpatialCoord());
                    break;
                case ACTION : ctrl.model.commander.act(ctrl.spatialSelector.getSpatialLabel(), getSpatialCoord()); break;
                case MOVE_ATTACK : ctrl.model.commander.setMoveAttack(); break;
                case HOLD : ctrl.model.commander.orderHold(); break;
            }
        else
            if(name.equals(SELECT))
                beginSelection();
    }

    private Point2D getSpatialCoord(){
        return ctrl.spatialSelector.getCoord(ctrl.view.rootNode);
    }

    private void beginSelection() {
        selectionStartOnMap = ctrl.spatialSelector.getCoord(ctrl.view.rootNode);
        selectionStartOnScreen = Translator.toPoint2D(ctrl.inputManager.getCursorPosition());
    }

    private boolean endSelection() {
        boolean selectionSuccess = false;
        Point2D selectionEndOnScreen = Translator.toPoint2D(ctrl.inputManager.getCursorPosition());
        double maxX = Math.max(selectionEndOnScreen.x, selectionStartOnScreen.x);
        double minX = Math.min(selectionEndOnScreen.x, selectionStartOnScreen.x);
        double maxY = Math.max(selectionEndOnScreen.y, selectionStartOnScreen.y);
        double minY = Math.min(selectionEndOnScreen.y, selectionStartOnScreen.y);
        selectionStartOnMap = ctrl.spatialSelector.getCoord(ctrl.view.rootNode, new Point2D(minX, minY));
        Point2D selectionEndOnMap = ctrl.spatialSelector.getCoord(ctrl.view.rootNode, new Point2D(maxX, maxY));
        if(selectionStartOnMap != null && selectionEndOnMap != null &&
                selectionStartOnMap.getDistance(selectionEndOnMap) > 1){
            ctrl.model.commander.select(selectionStartOnMap, selectionEndOnMap);
            selectionSuccess = true;
        }
        selectionStartOnScreen = null;
        return selectionSuccess;
    }
}
