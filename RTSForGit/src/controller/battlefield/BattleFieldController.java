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
    
    IsometricCamera isoCam;
    InputManager im;
    SpatialSelector ss;
    
    View view;

    public BattleFieldController(Model model, View view, Nifty nifty, InputManager im, Camera cam){
        this.model = model;
        ii = new BattleFieldInputInterpreter(im, cam, view, this);
        gui = new BattleFieldGUI(nifty, model.commander, model.reporter);
        this.im = im;
        this.view = view;
        ss = new SpatialSelector(cam, im, view);
        ss.centered = true;
        
        model.commander.registerListener(this);
        
        isoCam = new IsometricCamera(cam, 10);
        isoCam.registerWithInput(im);
        isoCam.setEnabled(true);
        im.setCursorVisible(true);
        
    }
    
    @Override
    public void update(double elapsedTime) {
        // draw selection rectangle
        Point2D selStart = ((BattleFieldInputInterpreter)ii).selectionStartOnScreen;
        if(selStart != null){
            Point2D p = Translator.toPoint2D(im.getCursorPosition());
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
        return ss.getCoord(view.rootNode);
    }
}
