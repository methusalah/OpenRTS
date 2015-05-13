package app;

import geometry.math.MyRandom;
import geometry.tools.LogUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.Model;
import view.View;
import view.mapDrawing.MapRenderer;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.system.AppSettings;

import controller.Controller;
import controller.battlefield.BattlefieldController;
import controller.editor.EditorController;
import controller.ground.GroundController;

public class MainRTS extends MySimpleApplication implements ActionListener {
    Model model;
	View view;
	MapRenderer tr;
	BattlefieldController fieldCtrl;
    EditorController editorCtrl;
    GroundController groundCtrl;
    Controller actualCtrl;

	public static void main(String[] args) {
		AppSettings settings = new AppSettings(true);
		settings.setBitsPerPixel(32);
		settings.setWidth(1920);
		settings.setHeight(960);
		settings.setTitle("RTS");
		settings.setVSync(true);
		Logger.getLogger("").setLevel(Level.INFO);
		LogUtil.init();
		LogUtil.logger.info("seed : " + MyRandom.SEED);

		MainRTS app = new MainRTS();
		app.setShowSettings(false);
		app.setSettings(settings);
		app.start();
		
		// test
	}
        
    @Override
	public void simpleInitApp() {
		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, 0, -1));
//		stateManager.detach(bulletAppState);

		flyCam.setUpVector(new Vector3f(0, 0, 1));
		flyCam.setEnabled(false);

        model = new Model();
        view = new View(rootNode, guiNode, bulletAppState.getPhysicsSpace(), assetManager, viewPort, model);
        
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);

        fieldCtrl = new BattlefieldController(model, view, niftyDisplay.getNifty(), inputManager, cam);
        fieldCtrl.addListener(this);
        editorCtrl = new EditorController(model, view, niftyDisplay.getNifty(), inputManager, cam);
        editorCtrl.addListener(this);
        groundCtrl = new GroundController(model, view, inputManager, cam);
        groundCtrl.addListener(this);

        niftyDisplay.getNifty().setIgnoreKeyboardEvents(true);
        //TODO: validation is needed to be sure everyting in XML is fine. see http://wiki.jmonkeyengine.org/doku.php/jme3:advanced:nifty_gui_best_practices
//        niftyDisplay.getNifty().validateXml("interface/screen.xml");
        niftyDisplay.getNifty().fromXml("interface/screen.xml", "editor");
        
        actualCtrl = editorCtrl;
        stateManager.attach(actualCtrl);
        actualCtrl.setEnabled(true);

        view.mapRend.renderTiles();
        
        guiViewPort.addProcessor(niftyDisplay);
	}
        
    @Override
    public void simpleUpdate(float tpf) {
        float maxedTPF = Math.min(tpf, 0.1f);
        listener.setLocation(cam.getLocation());
        listener.setRotation(cam.getRotation());
        view.actorManager.render();
        actualCtrl.update(maxedTPF);
        model.updateConfigs();
    }

	@Override
	public void destroy() {
	}

	@Override
	public void prePhysicsTick(PhysicsSpace arg0, float arg1) {
	}

    @Override
    public void actionPerformed(ActionEvent e) {
        Controller desiredCtrl;
        switch(e.getActionCommand()){
            case "CTRL1" : desiredCtrl = fieldCtrl; break;
            case "CTRL2" : desiredCtrl = editorCtrl; break;
            case "CTRL3" : desiredCtrl = groundCtrl; break;
                default:throw new IllegalAccessError();
        }
        LogUtil.logger.info("switching controller to "+desiredCtrl.getClass().getSimpleName());
        
        stateManager.detach(actualCtrl);
        actualCtrl.setEnabled(false);
        actualCtrl = desiredCtrl;
        stateManager.attach(actualCtrl);
        actualCtrl.setEnabled(true);
        
    }
}
