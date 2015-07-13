package openrts.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import view.MapView;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.niftygui.NiftyJmeDisplay;

import event.EventManager;
import event.SelectEntityEvent;
import event.SelectEntityServerEvent;

public class OpenRTSServer extends SimpleApplication {

	private static final Logger logger = Logger.getLogger(OpenRTSServer.class.getName());

	// protected MapView view;
	protected static Server myServer;
	public static final int PORT = 6143;
	private Map<Integer, Player> players = new HashMap<Integer, Player>();

	public Player getPlayer(Integer id) {
		return players.get(id);
	}

	public static void main(String[] args) {
		System.out.println("Server starting...");
		OpenRTSServer app = new OpenRTSServer();
		// app.start(JmeContext.Type.Headless); // headless type for servers!
		app.start();
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

		EventManager.register(this);

		NiftyJmeDisplay clientDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
		Player p1 = new Player(clientDisplay, view, cam);
		stateManager.attach(p1.getFieldCtrl());

		if (view.getMapRend() != null) {
			view.getMapRend().renderTiles();
		}

		guiViewPort.addProcessor(clientDisplay);

		try {
			Serializer.registerClasses(SelectEntityEvent.class, SelectEntityServerEvent.class);
			myServer = Network.createServer(PORT, PORT);
			myServer.addMessageListener(new InputEventMessageListener(), SelectEntityEvent.class, SelectEntityServerEvent.class);
			myServer.addConnectionListener(new ConnectionListener());

			myServer.start();
			logger.info("Server listening at :" + PORT);
			System.out.println("Server listening at :" + PORT);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}