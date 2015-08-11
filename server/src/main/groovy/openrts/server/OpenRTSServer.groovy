package openrts.server


import java.util.logging.Logger

import model.ModelManager
import openrts.server.ConnectionListener
import openrts.server.Game
import openrts.server.InputEventMessageListener
import app.OpenRTSApplicationWithDI

import com.jme3.bullet.BulletAppState
import com.jme3.math.Vector3f
import com.jme3.network.Network
import com.jme3.network.Server
import com.jme3.network.serializing.Serializer

import event.network.AckEvent
import event.network.CreateGameEvent
import event.network.MultiSelectEntityEvent
import event.network.SelectEntityEvent

class OpenRTSServer extends OpenRTSApplicationWithDI {

	protected static List<Class> serializerClasses = [
		SelectEntityEvent.class,
		AckEvent.class,
		CreateGameEvent.class
	]


	static String mapfilename = "assets/maps/test.btf";
	static final Logger logger = Logger.getLogger(OpenRTSServer.class.getName());
	static final String gameName = "OpenRTS";
	static int version = 1;

	static Server myServer;
	static final int PORT = 6143;
	Map<Integer, Game> games = new HashMap<Integer, Game>();

	public Game getPlayer(Integer id) {
		return games.get(id);
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
	public void simpleInitApp() {
		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, 0, -1));
		// stateManager.detach(bulletAppState);

		flyCam.setUpVector(new Vector3f(0, 0, 1));
		flyCam.setEnabled(false);

		try {

			Serializer.registerClasses(SelectEntityEvent.class,AckEvent.class,CreateGameEvent.class, MultiSelectEntityEvent.class);
			myServer = Network.createServer(gameName, version, PORT, PORT);
			myServer.addMessageListener(new InputEventMessageListener(), SelectEntityEvent.class, AckEvent.class, CreateGameEvent.class);
			myServer.addConnectionListener(new ConnectionListener());

			myServer.start();
			logger.info("Server listening at :" + PORT);
			System.out.println("Server listening at :" + PORT);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	@Override
	public void simpleUpdate(float tpf) {
		float maxedTPF = Math.min(tpf, 0.1f);
		listener.setLocation(cam.getLocation());
		listener.setRotation(cam.getRotation());
		stateManager.update(maxedTPF);
		// view.getActorManager().render();
		// p1.getFieldCtrl().update(maxedTPF);
		ModelManager.updateConfigs();
	}

}