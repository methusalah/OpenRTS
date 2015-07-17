package app.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.LogManager;

import model.ModelManager;
import model.battlefield.warfare.Faction;
import view.MapView;
import app.OpenRTSApplication;

import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;

import controller.game.MultiplayerGameController;
import event.EventManager;

public class Game extends OpenRTSApplication {

	protected MapView view;
	protected MultiplayerGameController fieldCtrl;
	// TODO: I'm not sure, if this is the correct place for faction
	protected Faction faction;

	private static String NiftyInterfaceFile = "interface/MulitplayerScreen.xml";
	private static String NiftyInterfaceFile2 = "interface/nifty_loading.xml";
	private static String NiftyScreen = "network";

	public static void main(String[] args) {
		Properties preferences = new Properties();
		try {
			FileInputStream configFile = new FileInputStream("logging.properties");
			preferences.load(configFile);
			LogManager.getLogManager().readConfiguration(configFile);
		} catch (IOException ex) {
			System.err.println("WARNING: Could not open configuration file - please create a logging.properties for correct logging");
			System.err.println("WARNING: Logging not configured (console output only)");
		}
		OpenRTSApplication.main(new Game());
	}

	@Override
	public void simpleInitApp() {
		BulletAppState bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, 0, -1));
		// stateManager.detach(bulletAppState);

		flyCam.setUpVector(new Vector3f(0, 0, 1));
		flyCam.setEnabled(false);

		view = new MapView(rootNode, guiNode, bulletAppState.getPhysicsSpace(), assetManager, viewPort);

		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
		fieldCtrl = new MultiplayerGameController(view, niftyDisplay.getNifty(), inputManager, cam);
		stateManager.attach(fieldCtrl);
		fieldCtrl.setEnabled(true);
		EventManager.register(this);

		niftyDisplay.getNifty().setIgnoreKeyboardEvents(true);
		// TODO: validation is needed to be sure everyting in XML is fine. see http://wiki.jmonkeyengine.org/doku.php/jme3:advanced:nifty_gui_best_practices
		try {
			niftyDisplay.getNifty().validateXml(NiftyInterfaceFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		niftyDisplay.getNifty().fromXml(NiftyInterfaceFile, NiftyScreen);
		niftyDisplay.getNifty().addXml(NiftyInterfaceFile2);

		if (view.getMapRend() != null) {
			view.getMapRend().renderTiles();
		}
		guiViewPort.addProcessor(niftyDisplay);
	}

	@Override
	public void simpleUpdate(float tpf) {
		float maxedTPF = Math.min(tpf, 0.1f);
		listener.setLocation(cam.getLocation());
		listener.setRotation(cam.getRotation());
		view.getActorManager().render();
		fieldCtrl.update(maxedTPF);
		ModelManager.updateConfigs();
	}
}
