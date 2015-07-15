package openrts.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import model.ModelManager;
import openrts.guice.GuiceApplication;
import view.MapView;

import com.google.inject.Inject;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.niftygui.NiftyJmeDisplay;

import event.EventManager;
import event.SelectEntityEvent;
import event.SelectEntityServerEvent;

public class OpenRTSServer extends GuiceApplication {

	protected static String mapfilename = "assets/maps/test.btf";
	private static final Logger logger = Logger.getLogger(OpenRTSServer.class.getName());

	protected MapView view;
	protected Player p1;

	protected static Server myServer;
	public static final int PORT = 6143;
	private Map<Integer, Player> players = new HashMap<Integer, Player>();

	@Inject
	private BulletAppState bulletAppState;

	public Player getPlayer(Integer id) {
		return players.get(id);
	}

	public static void main(String[] args) {

		// Properties preferences = new Properties();
		// try {
		// FileInputStream configFile = new FileInputStream("logging.properties");
		// preferences.load(configFile);
		// LogManager.getLogManager().readConfiguration(configFile);
		// } catch (IOException ex) {
		// System.err.println("WARNING: Could not open configuration file");
		// System.err.println("WARNING: Logging not configured (console output only)");
		// }

		System.out.println("Server starting...");
		OpenRTSServer app = new OpenRTSServer();
		// app.start(JmeContext.Type.Headless); // headless type for servers!
		app.start();
	}

	@Override
	public void guiceAppInit() {

		stateManager.attach(bulletAppState);
		bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, 0, -1));
		// stateManager.detach(bulletAppState);

		flyCam.setUpVector(new Vector3f(0, 0, 1));
		flyCam.setEnabled(false);

		view = new MapView(rootNode, guiNode, bulletAppState.getPhysicsSpace(), assetManager, viewPort);

		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
		p1 = new Player(niftyDisplay, view, inputManager, cam);
		EventManager.register(this);

		if (view.getMapRend() != null) {
			view.getMapRend().renderTiles();
		}

		stateManager.attach(p1.getFieldCtrl());
		p1.getFieldCtrl().setEnabled(true);
		guiViewPort.addProcessor(niftyDisplay);

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

		if (!mapfilename.isEmpty()) {
			ModelManager.loadBattlefield(mapfilename);
		}
	}

	@Override
	public void simpleUpdate(float tpf) {
		float maxedTPF = Math.min(tpf, 0.1f);
		listener.setLocation(cam.getLocation());
		listener.setRotation(cam.getRotation());
		view.getActorManager().render();
		p1.getFieldCtrl().update(maxedTPF);
		ModelManager.updateConfigs();
	}

}