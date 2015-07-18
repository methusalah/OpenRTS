package app.example;

import model.ModelManager;
import openrts.guice.GuiceApplication;

import com.google.inject.Inject;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;

import controller.game.NetworkNiftyController;
import controller.game.NetworkScreenState;
import event.EventManager;

public class Game extends GuiceApplication {

	// protected MapView view;
	protected NetworkScreenState networkState;

	@Inject
	private BulletAppState bulletAppState;
	// @Inject
	// private MessageManager messageManager;;

	private static String NiftyInterfaceFile = "interface/MulitplayerScreen.xml";
	private static String NiftyInterfaceFile2 = "interface/nifty_loading.xml";
	@Inject
	private NetworkNiftyController networkNiftyController;
	private static String NiftyScreen = "network";

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
		Game app = new Game();
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

		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
		niftyDisplay.getNifty().setIgnoreKeyboardEvents(true);
		// TODO: validation is needed to be sure everyting in XML is fine. see http://wiki.jmonkeyengine.org/doku.php/jme3:advanced:nifty_gui_best_practices
		try {
			niftyDisplay.getNifty().validateXml(NiftyInterfaceFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		niftyDisplay.getNifty().fromXml(NiftyInterfaceFile, NiftyScreen, networkNiftyController);
		niftyDisplay.getNifty().addXml(NiftyInterfaceFile2);

		networkState = new NetworkScreenState(this);
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

	// @Override
	// public void start() {
	// // set some default settings in-case
	// // settings dialog is not shown
	// boolean loadSettings = false;
	// if (settings == null) {
	// setSettings(new AppSettings(true));
	// loadSettings = true;
	// }
	//
	// // show settings dialog
	// if (showSettings) {
	// if (!JmeSystem.showSettingsDialog(settings, loadSettings)) {
	// return;
	// }
	// }
	// // re-setting settings they can have been merged from the registry.
	// setSettings(settings);
	// super.start();
	// }
}
