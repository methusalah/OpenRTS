/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.battlefield;

import app.AzertyFlyByCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import controller.Controller;
import controller.cameraManagement.IsometricCameraManager;
import controller.SpatialSelector;
import de.lessvoid.nifty.Nifty;
import geometry.Point2D;
import model.Model;
import view.View;
import view.math.Translator;

/**
 *
 * @author Benoît
 */
public class BattleFieldController extends Controller {
    public BattleFieldController(Model model, View view, Nifty nifty, InputManager inputManager, Camera cam){
        super(model, view, inputManager, cam);
        
        inputInterpreter = new BattleFieldInputInterpreter(this);
        gui = new BattleFieldGUI(nifty, model.commander, model.reporter);
        
        model.commander.registerListener(this);
        
        cameraManager = new IsometricCameraManager(cam, 10, model);
    }
    
    @Override
    public void update(double elapsedTime) {
        // draw selection rectangle
        Point2D selStart = ((BattleFieldInputInterpreter)inputInterpreter).selectionStartOnScreen;
        if(selStart != null){
            Point2D p = Translator.toPoint2D(inputManager.getCursorPosition());
            view.drawSelectionArea(selStart, p);
        } else
            view.guiNode.detachAllChildren();   
        
        // update selectables
        model.commander.updateSelectables(getViewCenter());
        
        // udpdate army
        model.battlefield.armyManager.update(elapsedTime);
    }

    @Override
    public void manageEvent() {
        gui.update();
    }
    
    private Point2D getViewCenter(){
        return spatialSelector.getCoord(view.rootNode);
    }

    @Override
    public void activate() {
        super.activate();
        inputManager.setCursorVisible(true);
    }
    
    
}
