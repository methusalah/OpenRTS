package controller.battlefield;

import geometry.geom2d.Point2D;
import model.Model;
import view.View;
import view.math.Translator;

import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;

import controller.Controller;
import controller.cameraManagement.IsometricCameraManager;
import de.lessvoid.nifty.Nifty;

/**
 *
 */
public class BattlefieldController extends Controller {
	private boolean paused = false;
	
    public BattlefieldController(Model model, View view, Nifty nifty, InputManager inputManager, Camera cam){
        super(model, view, inputManager, cam);
        
        inputInterpreter = new BattlefieldInputInterpreter(this);
        guiController = new BattlefieldGUIController(nifty, this);
        
        model.commander.registerListener(this);
        
        cameraManager = new IsometricCameraManager(cam, 10, model);
    }
    
    @Override
    public void update(float elapsedTime) {
        // draw selection rectangle
        Point2D selStart = ((BattlefieldInputInterpreter)inputInterpreter).selectionStart;
        if(selStart != null){
            Point2D p = Translator.toPoint2D(inputManager.getCursorPosition());
            view.drawSelectionArea(selStart, p);
        } else
            view.guiNode.detachAllChildren();   
        
        // update selectables
        model.commander.updateSelectables(spatialSelector.getCenterViewCoord(view.rootNode));
        guiController.update();
        
        // udpdate army
        if(!paused)
        	model.battlefield.armyManager.update(elapsedTime);
    }

    @Override
    public void manageEvent() {
        guiController.update();
    }
    
    public void togglePause(){
    	paused = !paused;
		view.actorManager.pause(paused);
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
        super.stateAttached(stateManager);
        inputManager.setCursorVisible(true);
        guiController.activate();
    }
    
    
}
