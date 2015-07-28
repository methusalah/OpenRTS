package app;

import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

import model.ModelManager;
import model.editor.ToolManager;
import openrts.guice.annotation.AppSettingsRef;
import openrts.guice.annotation.AssetManagerRef;
import openrts.guice.annotation.AudioRendererRef;
import openrts.guice.annotation.GuiNodeRef;
import openrts.guice.annotation.InputManagerRef;
import openrts.guice.annotation.RootNodeRef;
import openrts.guice.annotation.StateManagerRef;
import openrts.guice.annotation.ViewPortRef;
import view.EditorView;
import view.mapDrawing.MapDrawer;

import com.google.common.eventbus.Subscribe;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

import controller.Controller;
import controller.battlefield.BattlefieldController;
import controller.battlefield.BattlefieldGUIController;
import controller.battlefield.BattlefieldInputInterpreter;
import controller.editor.EditorController;
import controller.editor.EditorGUIController;
import controller.editor.EditorInputInterpreter;
import controller.ground.GroundController;
import controller.ground.GroundGUIController;
import controller.ground.GroundInputInterpreter;
import de.lessvoid.nifty.Nifty;
import event.EventManager;
import event.client.ControllerChangeEvent;

public class MainRTSWithDI extends OpenRTSApplicationWithDI {

	private static final Logger logger = Logger.getLogger(MainRTSWithDI.class.getName());

	EditorView view;
	MapDrawer tr;
	private BattlefieldController fieldCtrl;
	private EditorController editorCtrl;
	private GroundController groundCtrl;
	Controller actualCtrl;
	protected Injector injector;
	protected Collection<Module> modules;

	public static void main(String[] args) {
		OpenRTSApplicationWithDI.main(new MainRTSWithDI());
	}

	@Override
	public void simpleInitApp() {
		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, 0, -1));
		// stateManager.detach(bulletAppState);

		flyCam.setUpVector(new Vector3f(0, 0, 1));
		flyCam.setEnabled(false);

		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);

		Application app = this;

		this.modules = new LinkedList<Module>();
		// register new instances to Guice (DI)
		this.modules.add(new AbstractModule() {

			@Override
			protected void configure() {

				bind(AssetManager.class).annotatedWith(AssetManagerRef.class).toInstance(assetManager);
				bind(Node.class).annotatedWith(GuiNodeRef.class).toInstance(guiNode);
				bind(AppSettings.class).annotatedWith(AppSettingsRef.class).toInstance(settings);
				bind(AppStateManager.class).annotatedWith(StateManagerRef.class).toInstance(stateManager);
				bind(Node.class).annotatedWith(Names.named("RootNode")).toInstance(rootNode);
				bind(Node.class).annotatedWith(Names.named("GuiNode")).toInstance(guiNode);
				bind(ViewPort.class).annotatedWith(Names.named("ViewPort")).toInstance(viewPort);
				bind(ViewPort.class).annotatedWith(Names.named("GuiViewPort")).toInstance(guiViewPort);
				bind(AudioRenderer.class).annotatedWith(AudioRendererRef.class).toInstance(audioRenderer);
				bind(InputManager.class).annotatedWith(InputManagerRef.class).toInstance(inputManager);
				bind(Camera.class).annotatedWith(Names.named("Camera")).toInstance(cam);
				bind(FlyByCamera.class).annotatedWith(Names.named("FlyByCamera")).toInstance(flyCam);

				bind(Application.class).toInstance(app);

				// bind(ClientManager.class).in(Singleton.class);
				// bind(NetworkNiftyController.class).in(Singleton.class);

				// bind(MapView.class).annotatedWith(Names.named("MapView")).to(MapView.class).in(Singleton.class);

				bind(BattlefieldController.class).annotatedWith(Names.named("BattlefieldController")).to(BattlefieldController.class).in(Singleton.class);
				bind(BattlefieldGUIController.class).annotatedWith(Names.named("BattlefieldGUIController")).to(BattlefieldGUIController.class)
				.in(Singleton.class);
				bind(BattlefieldInputInterpreter.class).annotatedWith(Names.named("BattlefieldInputInterpreter")).to(BattlefieldInputInterpreter.class)
				.in(Singleton.class);

				bind(EditorGUIController.class).annotatedWith(Names.named("EditorGUIController")).to(EditorGUIController.class).in(Singleton.class);
				bind(EditorInputInterpreter.class).annotatedWith(Names.named("EditorInputInterpreter")).to(EditorInputInterpreter.class).in(Singleton.class);
				bind(EditorController.class).annotatedWith(Names.named("EditorController")).to(EditorController.class).in(Singleton.class);

				bind(GroundController.class).annotatedWith(Names.named("GroundController")).to(GroundController.class).in(Singleton.class);
				bind(GroundGUIController.class).annotatedWith(Names.named("GroundGUIController")).to(GroundGUIController.class).in(Singleton.class);
				bind(GroundInputInterpreter.class).annotatedWith(Names.named("GroundInputInterpreter")).to(GroundInputInterpreter.class).in(Singleton.class);

				bind(NiftyJmeDisplay.class).annotatedWith(Names.named("NiftyJmeDisplay")).toInstance(niftyDisplay);
				bind(Node.class).annotatedWith(RootNodeRef.class).toInstance(rootNode);
				bind(ViewPort.class).annotatedWith(ViewPortRef.class).toInstance(viewPort);

				bind(EditorView.class).in(Singleton.class);;
				bind(Nifty.class).annotatedWith(Names.named("Nifty")).toInstance(niftyDisplay.getNifty());

			}
		});
		injector = Guice.createInjector(modules);
		injector.injectMembers(this);

		view = injector.getInstance(EditorView.class);


		niftyDisplay.getNifty().setIgnoreKeyboardEvents(true);
		// TODO: validation is needed to be sure everyting in XML is fine. see http://wiki.jmonkeyengine.org/doku.php/jme3:advanced:nifty_gui_best_practices
		// niftyDisplay.getNifty().validateXml("interface/screen.xml");
		niftyDisplay.getNifty().fromXml("interface/screen.xml", "editor");

		editorCtrl = injector.getInstance(EditorController.class);
		fieldCtrl = injector.getInstance(BattlefieldController.class);
		groundCtrl = injector.getInstance(GroundController.class);

		// fieldCtrl = new BattlefieldController(view, niftyDisplay.getNifty(), inputManager, cam);
		// editorCtrl = new EditorController(view, niftyDisplay.getNifty(), inputManager, cam);
		// groundCtrl = new GroundController(view, niftyDisplay.getNifty(), inputManager, cam);
		EventManager.register(this);

		actualCtrl = editorCtrl;
		stateManager.attach(actualCtrl);
		actualCtrl.setEnabled(true);
		guiViewPort.addProcessor(niftyDisplay);

		ModelManager.setNewBattlefield();
	}

	@Override
	public void simpleUpdate(float tpf) {
		float maxedTPF = Math.min(tpf, 0.1f);
		listener.setLocation(cam.getLocation());
		listener.setRotation(cam.getRotation());
		view.getActorManager().render();
		actualCtrl.update(maxedTPF);
		ModelManager.updateConfigs();
	}

	@Override
	public void destroy() {
		ToolManager.killSower();
	}

	@Subscribe
	public void handleEvent(ControllerChangeEvent e) {
		Controller desiredCtrl;
		switch (e.getControllerIndex()) {
			case 0:
				desiredCtrl = fieldCtrl;
				break;
			case 1:
				desiredCtrl = editorCtrl;
				break;
			case 2:
				desiredCtrl = groundCtrl;
				break;
			default:
				return;
		}
		logger.info("switching controller to " + desiredCtrl.getClass().getSimpleName());

		stateManager.detach(actualCtrl);
		actualCtrl.setEnabled(false);
		actualCtrl = desiredCtrl;
		stateManager.attach(actualCtrl);
		actualCtrl.setEnabled(true);

	}

}
