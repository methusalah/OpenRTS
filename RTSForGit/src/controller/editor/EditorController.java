/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.editor;

import controller.cameraManagement.IsometricCameraManager;
import controller.battlefield.*;
import app.AzertyFlyByCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
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
    Point2D screenCoord;
    public EditorController(Model model, View view, Nifty nifty, InputManager inputManager, Camera cam){
        super(model, view, inputManager, cam);

        inputInterpreter = new EditorInputInterpreter(this);
        gui = new EditorGUI(nifty, this);

        model.commander.registerListener(this);
        
        cameraManager = new IsometricCameraManager(cam, 10, model);
    }

    @Override
    public void update(double elapsedTime) {
//        screenCoord = Translator.toPoint2D(im.getCursorPosition());
        model.toolManager.pointedSpatialLabel = spatialSelector.getSpatialLabel();
        Point2D coord = spatialSelector.getCoord(view.editorRend.gridNode);
        if(coord != null && model.battlefield.map.isInBounds(coord)){
            model.toolManager.pencil.setPos(coord);
            view.editorRend.drawPencil();
        }
        
        gui.update();
    }

    @Override
    public void manageEvent() {
        gui.update();
    }

    @Override
    public void activate() {
        super.activate();
        inputManager.setCursorVisible(true);
        view.rootNode.attachChild(view.editorRend.mainNode);
        gui.activate();
    }

    @Override
    public void desactivate() {
        super.desactivate();
        view.rootNode.detachChild(view.editorRend.mainNode);
    }
    
    
    
}
