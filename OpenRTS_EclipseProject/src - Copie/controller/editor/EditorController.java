/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.editor;

import geometry.geom2d.Point2D;
import model.Model;
import view.View;

import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;

import controller.Controller;
import controller.cameraManagement.IsometricCameraManager;
import de.lessvoid.nifty.Nifty;

/**
 *
 * @author Benoît
 */
public class EditorController extends Controller {
    Point2D screenCoord;
    
    public EditorController(Model model, View view, Nifty nifty, InputManager inputManager, Camera cam){
        super(model, view, inputManager, cam);

        inputInterpreter = new EditorInputInterpreter(this);
        guiController = new EditorGUIController(nifty, this);

        model.commander.registerListener(this);
        
        cameraManager = new IsometricCameraManager(cam, 10, model);
    }

    @Override
    public void update(double elapsedTime) {
//        screenCoord = Translator.toPoint2D(im.getCursorPosition());
        model.toolManager.pointedSpatialLabel = spatialSelector.getSpatialLabel();
        Point2D coord = spatialSelector.getCoord(view.editorRend.gridNode);
        if(coord != null && model.battlefield.map.isInBounds(coord)){
            model.toolManager.updatePencilsPos(coord);
            view.editorRend.drawPencil();
        }
        
        guiController.update();
    }

    @Override
    public void manageEvent() {
    }

    @Override
    public void activate() {
        super.activate();
        inputManager.setCursorVisible(true);
        view.rootNode.attachChild(view.editorRend.mainNode);
        guiController.activate();
        model.battlefield.engagement.resetEngagement();
    }

    @Override
    public void desactivate() {
    	model.battlefield.engagement.saveEngagement();
    	model.battlefield.map.prepareForBattle();
        super.desactivate();
        view.rootNode.detachChild(view.editorRend.mainNode);
    }
    
    
    
}
