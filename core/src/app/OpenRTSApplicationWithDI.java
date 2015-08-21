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

import openrts.guice.annotation.AppSettingsRef;
import openrts.guice.annotation.AudioRendererRef;
import openrts.guice.annotation.GuiNodeRef;
import openrts.guice.annotation.RootNodeRef;
import openrts.guice.annotation.StateManagerRef;
import openrts.guice.annotation.ViewPortRef;
import view.EditorView;
import view.camera.AzertyFlyByCamera;
import view.mapDrawing.EditorRenderer;
import view.material.MaterialManager;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.jme3.app.Application;
import com.jme3.app.StatsView;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext.Type;
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
	protected NiftyJmeDisplay niftyDisplay;

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

		if (inputManager != null) {
			flyCam = new AzertyFlyByCamera(cam);
			flyCam.setMoveSpeed(1f);
			flyCam.registerWithInput(inputManager);

			if (context.getType() == Type.Display) {
				inputManager.addMapping("SIMPLEAPP_Exit", new KeyTrigger(KeyInput.KEY_ESCAPE));
			}

			inputManager.addMapping("SIMPLEAPP_CameraPos", new KeyTrigger(KeyInput.KEY_C));
			inputManager.addMapping("SIMPLEAPP_Memory", new KeyTrigger(KeyInput.KEY_M));
			inputManager.addListener(actionListener, "SIMPLEAPP_Exit", "SIMPLEAPP_CameraPos", "SIMPLEAPP_Memory");
		}

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
		final Application app = this;

		this.modules = new LinkedList<Module>();
		// register new instances to Guice (DI)
		this.modules.add(new AbstractModule() {

			@Override
			protected void configure() {

				bind(AssetManager.class).toInstance(assetManager);
				bind(Node.class).annotatedWith(GuiNodeRef.class).toInstance(guiNode);
				bind(AppSettings.class).annotatedWith(AppSettingsRef.class).toInstance(settings);
				bind(AppStateManager.class).annotatedWith(StateManagerRef.class).toInstance(stateManager);
				bind(ViewPort.class).annotatedWith(Names.named("ViewPort")).toInstance(viewPort);
				bind(ViewPort.class).annotatedWith(Names.named("GuiViewPort")).toInstance(guiViewPort);
				bind(AudioRenderer.class).annotatedWith(AudioRendererRef.class).toInstance(audioRenderer);
				bind(InputManager.class).toInstance(inputManager);
				bind(Camera.class).toInstance(cam);
				bind(FlyByCamera.class).annotatedWith(Names.named("FlyByCamera")).toInstance(flyCam);

				bind(Application.class).toInstance(app);

				// bind(ClientManager.class).in(Singleton.class);
				// bind(NetworkNiftyController.class).in(Singleton.class);

				// bind(MapView.class).annotatedWith(Names.named("MapView")).to(MapView.class).in(Singleton.class);

				// bind(BattlefieldController.class).annotatedWith(Names.named("BattlefieldController")).to(BattlefieldController.class).in(Singleton.class);
				// bind(BattlefieldGUIController.class).annotatedWith(Names.named("BattlefieldGUIController")).to(BattlefieldGUIController.class)
				// .in(Singleton.class);
				// bind(BattlefieldInputInterpreter.class).annotatedWith(Names.named("BattlefieldInputInterpreter")).to(BattlefieldInputInterpreter.class)
				// .in(Singleton.class);

				// bind(EditorGUIController.class).annotatedWith(Names.named("EditorGUIController")).to(EditorGUIController.class).in(Singleton.class);
				// bind(EditorInputInterpreter.class).annotatedWith(Names.named("EditorInputInterpreter")).to(EditorInputInterpreter.class).in(Singleton.class);
				// bind(EditorController.class).annotatedWith(Names.named("EditorController")).to(EditorController.class).in(Singleton.class);

				// bind(GroundController.class).annotatedWith(Names.named("GroundController")).to(GroundController.class).in(Singleton.class);
				// bind(GroundGUIController.class).annotatedWith(Names.named("GroundGUIController")).to(GroundGUIController.class).in(Singleton.class);
				// bind(GroundInputInterpreter.class).annotatedWith(Names.named("GroundInputInterpreter")).to(GroundInputInterpreter.class).in(Singleton.class);

				bind(Node.class).annotatedWith(RootNodeRef.class).toInstance(rootNode);
				// FIXME: Viewport is already binded
				bind(ViewPort.class).annotatedWith(ViewPortRef.class).toInstance(viewPort);

				bind(EditorView.class).in(Singleton.class);

				bind(MaterialManager.class).in(Singleton.class);

				// no singleton needed here => it easier for resetting
				bind(EditorRenderer.class).in(Singleton.class);

			}
		});
		modules.addAll(newModules);

		injector = Guice.createInjector(modules);
		injector.injectMembers(this);
	}

}
