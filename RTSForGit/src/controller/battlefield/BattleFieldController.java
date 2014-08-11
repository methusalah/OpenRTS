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
import controller.GUI;
import controller.InputInterpreter;
import controller.SpatialSelector;
import de.lessvoid.nifty.Nifty;
import geometry.Point2D;
import model.Model;
import model.ReportEventListener;
import view.View;
import view.math.Translator;

/**
 *
 * @author Benoît
 */
public class BattleFieldController extends Controller {
    
    IsometricCamera isoCam;
    FlyByCamera flyCam;
    InputManager im;
    SpatialSelector ss;
    
    View view;
    
    

    
    public BattleFieldController(Model model, View view, Nifty nifty, InputManager im, Camera cam){
        this.model = model;
        ii = new BattleFieldInputInterpreter(im, cam, model.commander, view, this);
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
        
        flyCam = new AzertyFlyByCamera(cam);
        flyCam.setEnabled(false);
        flyCam.setMoveSpeed(10);
    }
    
    protected void switchCamera(){
        if(isoCam.isEnabled()){
            isoCam.setEnabled(false);
            isoCam.unregisterInput(im);
            
            flyCam.setEnabled(true);
            flyCam.registerWithInput(im);
            im.setCursorVisible(false);
        } else {
            flyCam.setEnabled(false);
            flyCam.unregisterInput();
            
            isoCam.setEnabled(true);
            isoCam.registerWithInput(im);
            im.setCursorVisible(true);
        }
    }
    
    public void updateSelection(){
        Point2D selStart = ((BattleFieldInputInterpreter)ii).selectionStartOnScreen;
        if(selStart != null){
            Point2D p = Translator.toPoint2D(im.getCursorPosition());
            view.drawSelectionArea(selStart, p);
        } else
            view.guiNode.detachAllChildren();        
    }

    @Override
    public void manageEvent() {
        gui.update();
    }
    
    public Point2D getViewCenter(){
        return ss.getCoord(view.rootNode);
    }
}
