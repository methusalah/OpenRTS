package app.example;

import java.util.Collection;
import java.util.logging.Logger;

import model.ModelManager;
import openrts.guice.GuiceApplication;
import view.MapView;

import com.google.common.eventbus.Subscribe;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;

import controller.game.MultiplayerGameController;
import controller.game.NetworkAppState;
import controller.game.NetworkNiftyController;
import event.EventManager;
import event.network.AckEvent;

public class MultiplayerGame extends GuiceApplication {

	private static final Logger logger = Logger.getLogger(MultiplayerGame.class.getName());

	// protected MapView view;
	protected NetworkAppState networkState;

	@Inject
	private BulletAppState bulletAppState;
	// @Inject
	// private MessageManager messageManager;;

	private static String NiftyInterfaceFile = "interface/MultiplayerScreen.xml";
	private static String NiftyInterfaceFile2 = "interface/map_loading.xml";
	@Inject
	private NetworkNiftyController networkNiftyController;
	private static String NiftyScreen = "network";

	// @Inject
	// private NiftyJmeDisplay niftyDisplay;

	// protected boolean showSettings = true;

	public static void main(String[] args) {
		// Properties preferences = new Properties();
		// try {
		// FileInputStream configFile = new FileInputStream("logging.properties");
		// preferences.load(configFile);
		// LogManager.getLogManager().readConfiguration(configFile);
		// } catch (IOException ex) {
		// System.err.println("WARNING: Could not open configuration file - please create a logging.properties for correct logging");
		// System.err.println("WARNING: Logging not configured (console output only)");
		// }
		MultiplayerGame app = new MultiplayerGame();
		app.start();
	}


	@Override
	public void guiceAppInit() {
		BulletAppState bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, 0, -1));
		// stateManager.detach(bulletAppState);

		flyCam.setUpVector(new Vector3f(0, 0, 1));
		flyCam.setEnabled(false);

		// view = new MapView(rootNode, guiNode, bulletAppState.getPhysicsSpace(), assetManager, viewPort);
		// view.reset();

		final NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
		niftyDisplay.getNifty().setIgnoreKeyboardEvents(true);
		// TODO: validation is needed to be sure everyting in XML is fine. see http://wiki.jmonkeyengine.org/doku.php/jme3:advanced:nifty_gui_best_practices
		try {
			niftyDisplay.getNifty().validateXml(NiftyInterfaceFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.modules.add(new AbstractModule() {
			@Override
			protected void configure() {
				bind(NiftyJmeDisplay.class).annotatedWith(Names.named("NiftyJmeDisplay")).toInstance(niftyDisplay);
			}
		});

		niftyDisplay.getNifty().fromXml(NiftyInterfaceFile, NiftyScreen, networkNiftyController);
		niftyDisplay.getNifty().addXml(NiftyInterfaceFile2);

		networkState = new NetworkAppState(this);
		// view, niftyDisplay.getNifty(), inputManager, cam);
		stateManager.attach(networkState);
		networkState.setEnabled(true);
		EventManager.register(this);
		// FIXME later this must activate
		// if (view.getMapRend() != null) {
		// view.getMapRend().renderTiles();
		// }
		guiViewPort.addProcessor(niftyDisplay);
	}

	@Override
	public void simpleUpdate(float tpf) {
		float maxedTPF = Math.min(tpf, 0.1f);
		listener.setLocation(cam.getLocation());
		listener.setRotation(cam.getRotation());
		// view.getActorManager().render();
		networkState.update(maxedTPF);
		ModelManager.updateConfigs();
	}


	@Override
	protected void addApplicationModules(Collection<Module> modules) {
		super.addApplicationModules(modules);
	}

	@Subscribe
	public void manageAckEvent(AckEvent ev) {
		logger.info("sounds perfect. Server has loaded Map at time:" + ev.getAckDate());
		MapView view = new MapView(rootNode, guiNode, bulletAppState.getPhysicsSpace(), assetManager, viewPort);

		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
		// game = new Game(niftyDisplay, view, inputManager, cam);
		EventManager.register(this);

		if (view.getMapRend() != null) {
			view.getMapRend().renderTiles();
		}
		MultiplayerGameController game = new MultiplayerGameController(view, niftyDisplay.getNifty(), inputManager, cam);
		stateManager.detach(networkState);
		stateManager.attach(game);
	}

}
