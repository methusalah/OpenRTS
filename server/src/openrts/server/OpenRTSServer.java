package openrts.server;

import java.io.IOException;
import java.util.logging.Logger;

import view.MapView;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.system.JmeContext;

import event.EventManager;
import event.MapInputEvent;
import event.ToClientEvent;
import event.ToServerEvent;

public class OpenRTSServer extends SimpleApplication {

	private static final Logger logger = Logger.getLogger(OpenRTSServer.class.getName());

	// protected MapView view;
	protected GameController fieldCtrl;
	protected static Server myServer;
	public static final int PORT = 6143;

	public static void main(String[] args) {
		System.out.println("Server starting...");
		OpenRTSServer app = new OpenRTSServer();
		app.start(JmeContext.Type.Headless); // headless type for servers!
	}

	@Override
	public void simpleInitApp() {

		BulletAppState bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, 0, -1));
		// stateManager.detach(bulletAppState);

		flyCam.setUpVector(new Vector3f(0, 0, 1));
		flyCam.setEnabled(false);

		MapView view = new MapView(rootNode, guiNode, bulletAppState.getPhysicsSpace(), assetManager, viewPort);

		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
		fieldCtrl = new GameController(view, niftyDisplay.getNifty(), cam);
		EventManager.registerForClient(this);

		niftyDisplay.getNifty().setIgnoreKeyboardEvents(true);
		// TODO: validation is needed to be sure everyting in XML is fine. see http://wiki.jmonkeyengine.org/doku.php/jme3:advanced:nifty_gui_best_practices
		try {
			niftyDisplay.getNifty().validateXml("interface/gamescreen.xml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		niftyDisplay.getNifty().fromXml("interface/gamescreen.xml", "hud");

		stateManager.attach(fieldCtrl);
		fieldCtrl.setEnabled(true);
		if (view.getMapRend() != null) {
			view.getMapRend().renderTiles();
		}
		guiViewPort.addProcessor(niftyDisplay);

		try {
			Serializer.registerClass(ToServerEvent.class);
			Serializer.registerClass(ToClientEvent.class);
			Serializer.registerClass(MapInputEvent.class);
			myServer = Network.createServer(PORT, PORT);
			myServer.addMessageListener(new InputEventMessageListener(fieldCtrl), ToServerEvent.class, MapInputEvent.class);
			myServer.addConnectionListener(new ConnectionListener());

			myServer.start();
			logger.info("Server listening at :" + PORT);
			System.out.println("Server listening at :" + PORT);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}