package controller.battlefield;

import view.View;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import controller.InputInterpreter;
import geometry.Point2D;
import model.Commander;
import tools.LogUtil;
import view.math.Translator;

public class BattleFieldInputInterpreter extends InputInterpreter {
	
    private BattleFieldController fc;
    public Point2D selectionStartOnMap;
    public Point2D selectionStartOnScreen;

    protected final static String Select = "select";
    protected final static String Action = "action";
    protected final static String MoveAttack = "moveattack";
    protected final static String Hold = "hold";
    protected final static String SwitchCam = "switchcam";

    BattleFieldInputInterpreter(InputManager im, Camera cam, Commander commander, View view, BattleFieldController fc) {
        super(im, cam, commander, view);
        selector.centered = false;
        this.fc = fc;
    }

    @Override
    protected void registerInputs() {
            String[] mappings = new String[]{
                            Select,
                            Action,
                            MoveAttack,
                            Hold,
                            SwitchCam,
            };
            inputManager.addMapping(Select, new MouseButtonTrigger(0));
            inputManager.addMapping(Action, new MouseButtonTrigger(1));
            inputManager.addMapping(MoveAttack, new KeyTrigger(KeyInput.KEY_A));
            inputManager.addMapping(Hold, new KeyTrigger(KeyInput.KEY_H));
            inputManager.addMapping(SwitchCam, new KeyTrigger(KeyInput.KEY_C));


            inputManager.addListener(this, mappings);
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if(!isActive)
            return;
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals(Select)) {
            if(isPressed)
                beginSelection();
            else if(!endSelection())
                    commander.select(getSpatialLabel(), getSpatialCoord());

        } else if(name.equals(Action) && !isPressed) {
            commander.act(getSpatialLabel(), getSpatialCoord());

        } else if(name.equals(MoveAttack) && !isPressed) {
            commander.setMoveAttack();

        } else if(name.equals(Hold) && !isPressed) {
            commander.orderHold();

        } else if(name.equals(SwitchCam) && !isPressed) {
            fc.switchCamera();

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
            commander.select(selectionStartOnMap, selectionEndOnMap);
            selectionSuccess = true;
        }
        selectionStartOnScreen = null;
        return selectionSuccess;
    }
}
