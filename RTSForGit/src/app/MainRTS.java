package app;

import com.jme3.bullet.BulletAppState;
import java.util.logging.Level;
import java.util.logging.Logger;

import math.MyRandom;
import model.Model;
import model.map.Map;
import tools.LogUtil;
import view.View;
import view.mapDrawing.MapRenderer;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.system.AppSettings;
import controller.battlefield.BattleFieldController;
import geometry.Point2D;
import math.Angle;

import model.map.MapFactory;

public class MainRTS extends MySimpleApplication {
        Model model;
	View view;
	MapRenderer tr;
	BattleFieldController fieldCtrl;

	public static void main(String[] args) {
		AppSettings settings = new AppSettings(true);
		settings.setBitsPerPixel(32);
		settings.setWidth(1550);
		settings.setHeight(850);
		settings.setTitle("RTS");
		settings.setVSync(true);
		Logger.getLogger("").setLevel(Level.INFO);
		LogUtil.init();
		LogUtil.logger.info("seed : " + MyRandom.SEED);

		MainRTS app = new MainRTS();
		app.setShowSettings(false);
		app.setSettings(settings);
		app.start();
	}
        
        @Override
	public void simpleInitApp() {
		FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
		BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
		fpp.addFilter(bloom);
		viewPort.addProcessor(fpp);
                
                bulletAppState = new BulletAppState();
                stateManager.attach(bulletAppState);
                bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, 0, -1));

		flyCam.setUpVector(new Vector3f(0, 0, 1));
		flyCam.setEnabled(false);

                Map m = MapFactory.buildMap("assets/data/maps/map.bmp");
                model = new Model(m);
                view = new View(rootNode, guiNode, bulletAppState.getPhysicsSpace(), assetManager, viewPort, model);
                NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);

                fieldCtrl = new BattleFieldController(model, view, niftyDisplay.getNifty(), inputManager, cam);
		
                view.mapRend.renderTiles();
                
                guiViewPort.addProcessor(niftyDisplay);
	}

        Vector3f dir = new Vector3f(0, 0, 0);
        double angle = Angle.FLAT;
        
        @Override
        public void simpleUpdate(float tpf) {
            float maxedTPF = Math.min(tpf, 0.1f);
            model.armyManager.updateMovers(maxedTPF);
            view.actorManager.render();
            fieldCtrl.updateSelection();
            model.updateConfigs();
            model.commander.updateSelectables(fieldCtrl.getViewCenter());
//            angle+=tpf*10;
//            if(angle>Angle.FLAT*2){
//                angle = Angle.FLAT;
////                Vector3f dir = new Vector3f(2, -1, 0);
//            }
//
//            double newX = Math.cos(angle);
//            double newY = Math.sin(angle);
//            dir = new Vector3f((float)newX, (float)newY, -1);
//
//            view.sunComp1.setDirection(dir);
        }

	@Override
	public void destroy() {
	}

	@Override
	public void prePhysicsTick(PhysicsSpace arg0, float arg1) {
	}
}
