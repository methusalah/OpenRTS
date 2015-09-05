package app;

import exception.TechnicalException;
import geometry.math.RandomUtil;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;

import model.ModelManager;
import view.camera.AzertyFlyByCamera;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.jme3.app.Application;
import com.jme3.app.StatsView;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;
import com.jme3.util.BufferUtils;

public abstract class OpenRTSApplicationWithDI extends Application implements PhysicsTickListener {

	private static final Logger logger = Logger.getLogger(OpenRTSApplicationWithDI.class.getName());

	public static OpenRTSApplicationWithDI appInstance;

	protected Node rootNode = new Node("Root Node");
	protected Node guiNode = new Node("Gui Node");

	protected float secondCounter = 0.0f;

	protected BitmapText fpsText;
	protected MyDebugger debugger;
	protected StatsView statsView;

	protected AzertyFlyByCamera flyCam;

	private AppActionListener actionListener = new AppActionListener();

	protected BulletAppState bulletAppState;

	protected Injector injector;
	protected Collection<Module> modules;

	private class AppActionListener implements ActionListener {
		@Override
		public void onAction(String name, boolean value, float tpf) {
			if (!value) {
				return;
			}

			if (name.equals("SIMPLEAPP_Exit")) {
				stop();
			} else if (name.equals("SIMPLEAPP_CameraPos")) {
				if (cam != null) {
					Vector3f loc = cam.getLocation();
					Quaternion rot = cam.getRotation();
					System.out.println("Camera Position: (" + loc.x + ", " + loc.y + ", " + loc.z + ")");
					System.out.println("Camera Rotation: " + rot);
					System.out.println("Camera Direction: " + cam.getDirection());
				}
			} else if (name.equals("SIMPLEAPP_Memory")) {
				BufferUtils.printCurrentDirectMemory(null);
			}
		}
	}

	@Override
	public void start() {
		super.start();
		// set some default settings in-case
		// settings dialog is not shown
		if (settings == null) {
			setSettings(new AppSettings(true));
			settings.setWidth(1024);
			settings.setHeight(768);
			// try {
			// settings.load("openrts.example");
			// } catch (BackingStoreException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}

	}

	public AzertyFlyByCamera getFlyByCamera() {
		return flyCam;
	}


	private void initTexts() {
		BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
		fpsText = new BitmapText(guiFont, false);
		fpsText.setLocalTranslation(0, fpsText.getLineHeight(), 0);
		fpsText.setText("Frames per second");
		guiNode.attachChild(fpsText);

		debugger = new MyDebugger(0, settings.getHeight(), assetManager.loadFont("Interface/Fonts/Console.fnt"));
		guiNode.attachChild(debugger.getNode());
	}

	public void loadStatsView() {
		statsView = new StatsView("Statistics View", assetManager, renderer.getStatistics());
		// move it up so it appears above fps text
		statsView.setLocalTranslation(0, fpsText.getLineHeight(), 0);
		guiNode.attachChild(statsView);
	}

	@Override
	public void initialize() {
		bulletAppState = new BulletAppState();
		bulletAppState.startPhysics();

		super.initialize();

		guiNode.setQueueBucket(Bucket.Gui);
		guiNode.setCullHint(CullHint.Never);
		initTexts();
		loadStatsView();
		viewPort.attachScene(rootNode);
		guiViewPort.attachScene(guiNode);

		// call user code
		simpleInitApp();
		stateManager.attach(bulletAppState);
		getPhysicsSpace().addTickListener(this);
	}

	@Override
	public void update() {
		super.update(); // makes sure to execute AppTasks
		if (speed == 0 || paused) {
			return;
		}

		float tpf = timer.getTimePerFrame() * speed;

		secondCounter += timer.getTimePerFrame();
		int fps = (int) timer.getFrameRate();
		if (secondCounter >= 1.0f) {
			fpsText.setText("Frames per second: " + fps);
			secondCounter = 0.0f;
		}

		// update states
		stateManager.update(tpf);

		// simple update and root node
		debugger.reset();
		simpleUpdate(tpf);
		debugger.getNode();

		rootNode.updateLogicalState(tpf);
		guiNode.updateLogicalState(tpf);
		rootNode.updateGeometricState();
		guiNode.updateGeometricState();

		// render states
		stateManager.render(renderManager);
		renderManager.render(tpf, true);
		simpleRender(renderManager);
		stateManager.postRender();
	}

	public abstract void simpleInitApp();


	public void simpleUpdate(float tpf) {
		injector.getInstance(ModelManager.class).updateConfigs();
	}

	public void simpleRender(RenderManager rm) {
	}

	public void simplePhysicsUpdate(float tpf) {
	}

	@Override
	public void physicsTick(PhysicsSpace space, float f) {
		simplePhysicsUpdate(f);
	}

	public PhysicsSpace getPhysicsSpace() {
		return bulletAppState.getPhysicsSpace();
	}

	@Override
	public void prePhysicsTick(PhysicsSpace arg0, float arg1) {
	}

	public void toggleToFullscreen() {
		GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		DisplayMode[] modes = device.getDisplayModes();
		int i = 0; // note: there are usually several, let's pick the first
		settings.setResolution(modes[i].getWidth(), modes[i].getHeight());
		settings.setFrequency(modes[i].getRefreshRate());
		settings.setBitsPerPixel(modes[i].getBitDepth());
		settings.setFullscreen(device.isFullScreenSupported());
		appInstance.setSettings(settings);
		appInstance.restart(); // restart the context to apply changes
	}

	public static void main(OpenRTSApplicationWithDI app) {
		appInstance = app;
		logger.info("seed : " + RandomUtil.SEED);

		appInstance.start();
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

	// public void startClient() {
	// // ClientManager.startClient(stateManager, this);
	// }

	// public void stopClient() {
	// ClientManager.stopClient();
	// }

	protected void initGuice() {
		initGuice(new ArrayList<Module>());
	}

	protected void initGuice(List<Module> newModules) {
		this.modules = new LinkedList<Module>();
		// register new instances to Guice (DI)
		this.modules.add(new MainGuiceModule(this));
		modules.addAll(newModules);

		injector = Guice.createInjector(modules);
		injector.injectMembers(this);
	}

	protected AppSettings getSettings() {
		return settings;
	}

	protected Node getRootNode() {
		return rootNode;
	}
	
}
