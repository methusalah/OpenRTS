/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.editor;

import controller.battlefield.*;
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
public class EditorController extends Controller {
    
    IsometricCamera isoCam;
    InputManager im;
    SpatialSelector ss;
    
    View view;
    
    Point2D screenCoord;
    
    

    
    public EditorController(Model model, View view, Nifty nifty, InputManager im, Camera cam){
        this.model = model;
        ii = new EditorInputInterpreter(im, cam, model.editor, model.sunLight, view, this);
        gui = new EditorGUI(nifty, model.commander, model.reporter);
        this.im = im;
        this.view = view;
        ss = new SpatialSelector(cam, im, view);
        ss.centered = false;
        
        model.commander.registerListener(this);
        
        isoCam = new IsometricCamera(cam, 10);
        isoCam.registerWithInput(im);
        isoCam.setEnabled(true);
        im.setCursorVisible(true);
    }
    
    public void drawPencilPreview(){
//        screenCoord = Translator.toPoint2D(im.getCursorPosition());
        Point2D coord = ss.getCoord(view.editorRend.gridNode);
        if(coord != null && model.map.isInBounds(coord)){
            model.editor.pencil.setPos(coord);
            view.editorRend.drawPencil();
        }
    }

    @Override
    public void manageEvent() {
        gui.update();
    }
}
