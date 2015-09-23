package brainless.openrts.server;

import exception.TechnicalException;
import geometry.math.RandomUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;

import app.OpenRTSApplicationWithDI;
import brainless.openrts.model.Game;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.jme3.network.Server;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;

abstract class OpenRTSServer extends OpenRTSApplicationWithDI {

	private static final Logger logger = Logger.getLogger(OpenRTSServer.class.getName());

	static final String gameName = "OpenRTS";
	static int version = 1;

	static Server myServer;
	static final int PORT = 6143;
	//	Map<Integer, Game> games = new HashMap<Integer, Game>();

	protected Injector injector;
	protected Collection<Module> modules;
	
	protected List<Game> games = new ArrayList<Game>();

	@Override
	public void start() {
		super.start();
		// set some default settings in-case
		// settings dialog is not shown
		if (settings == null) {
			setSettings(new AppSettings(true));
			settings.setWidth(800);
			settings.setHeight(600);
			// try {
			// settings.load("openrts.example");
			// } catch (BackingStoreException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}

	}


	@Override
	public void initialize() {
		//		bulletAppState = new BulletAppState();
		//		bulletAppState.startPhysics();

		super.initialize();

		//		guiNode.setQueueBucket(Bucket.Gui);
		//		guiNode.setCullHint(CullHint.Never);
		//
		//		//		loadStatsView();
		//		viewPort.attachScene(rootNode);
		//		guiViewPort.attachScene(guiNode);
		//
		//		if (inputManager != null) {
		//			flyCam = new AzertyFlyByCamera(cam);
		//			flyCam.setMoveSpeed(1f);
		//			flyCam.registerWithInput(inputManager);
		//
		//			if (context.getType() == Type.Display) {
		//				inputManager.addMapping("SIMPLEAPP_Exit", new KeyTrigger(KeyInput.KEY_ESCAPE));
		//			}
		//
		//			inputManager.addMapping("SIMPLEAPP_CameraPos", new KeyTrigger(KeyInput.KEY_C));
		//			inputManager.addMapping("SIMPLEAPP_Memory", new KeyTrigger(KeyInput.KEY_M));
		//			inputManager.addListener(actionListener, "SIMPLEAPP_Exit", "SIMPLEAPP_CameraPos", "SIMPLEAPP_Memory");
		//		}

		// call user code
		//		simpleInitApp();
		//		stateManager.attach(bulletAppState);
		//		getPhysicsSpace().addTickListener(this);
	}

	public abstract void simpleInitApp();


	public void simpleUpdate(float tpf) {
	}

	public void simpleRender(RenderManager rm) {
	}

	//	public void simplePhysicsUpdate(float tpf) {
	//	}

	//	@Override
	//	public void physicsTick(PhysicsSpace space, float f) {
	//		simplePhysicsUpdate(f);
	//	}

	//	public PhysicsSpace getPhysicsSpace() {
	//		return bulletAppState.getPhysicsSpace();
	//	}
	//
	//	@Override
	//	public void prePhysicsTick(PhysicsSpace arg0, float arg1) {
	//	}

	public static void main(OpenRTSServer app) {
		OpenRTSApplicationWithDI.appInstance = app;
		logger.info("seed : " + RandomUtil.SEED);

		app.start();
	}

	public void changeSettings() {
		JmeSystem.showSettingsDialog(settings, false);
		if (settings.isFullscreen()) {
			logger.info("Fullscreen not yet supported");
			settings.setFullscreen(false);
		}

		try {
			settings.save("openrts.example");
		} catch (BackingStoreException e) {
			throw new TechnicalException(e);
		}
		appInstance.setSettings(settings);
		appInstance.restart(); // restart the context to apply changes
		cam.resize(settings.getWidth(), settings.getHeight(), true);
	}


}
