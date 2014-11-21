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
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import controller.battlefield.BattleFieldController;
import geometry.Point2D;
import geometry3D.MyMesh;
import geometry3D.Point3D;
import math.Angle;

import model.map.MapFactory;
import view.math.Translator;

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
        
//        private void test(){
//            MyMesh mesh = new MyMesh();
//            
//            Point3D O = new Point3D(0, 0, 0);
//            Point3D A = new Point3D(16, 40, 15);
//            Point3D B = new Point3D(55, 43, 35);
//            
//            mesh.vertices.add(O);
//            mesh.vertices.add(A);
//            mesh.vertices.add(B);
//            
//            mesh.indices.add(0);
//            mesh.indices.add(2);
//            mesh.indices.add(1);
//            
//            mesh.normals.add(Point3D.UNIT_Z);
//            mesh.normals.add(Point3D.UNIT_Z);
//            mesh.normals.add(Point3D.UNIT_Z);
//            
//            mesh.textCoord.add(new Point2D(12, 11));
//            mesh.textCoord.add(new Point2D(16, 40));
//            mesh.textCoord.add(new Point2D(55, 43));
//            
//            Geometry g1 = new Geometry();
//            g1.setMesh(Translator.toJMEMesh(mesh));
//            g1.setMaterial(view.mm.redMaterial);
//            rootNode.attachChild(g1);
//            
//            
//            Point3D p = new Point3D(20, 50, 0);
//                
////            Point3D A = J.getSubtraction(O);
////            Point3D B = K.getSubtraction(O);
////            Point3D P = p.getSubtraction(O);
//
//            double a = A.x;
//            double d = A.y;
//            double g = A.z;
//            double b = B.x;
//            double e = B.y;
//            double h = B.z;
//            double c = p.x;
//            double f = p.y;
//            double z = (b*f*g+c*d*h-c*e*g-a*f*h)/(b*d-a*e);
//                
//            p=p.getAddition(0, 0, z);
//            
//            Geometry g2 = new Geometry();
//            g2.setMesh(new Box(0.5f, 0.5f, 0.5f));
//            g2.setMaterial(view.mm.greenMaterial);
//            g2.setLocalTranslation(Translator.toVector3f(p));
//            rootNode.attachChild(g2);
//        }

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
