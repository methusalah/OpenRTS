package brainless.openrts.server


import groovy.transform.CompileStatic

import java.util.logging.Logger

import model.ModelManager
import tonegod.gui.core.Screen
import brainless.openrts.event.ClientLoggedOutEvent
import brainless.openrts.event.ClientTrysToLoginEvent
import brainless.openrts.event.EventManager
import brainless.openrts.event.ServerCouldNotStartetEvent
import brainless.openrts.event.network.AckEvent
import brainless.openrts.event.network.CreateGameEvent
import brainless.openrts.event.network.MultiSelectEntityEvent
import brainless.openrts.event.network.SelectEntityEvent
import brainless.openrts.server.states.ServerControlAppState
import brainless.openrts.server.states.ServerLogicAppState
import brainless.openrts.server.states.gui.ServerStartAppState;

import com.jme3.network.Network
import com.jme3.network.kernel.KernelException
import com.jme3.network.serializing.Serializer

@CompileStatic
class ServerMain extends OpenRTSServer {

	static final Logger logger = Logger.getLogger(ServerMain.class.getName());

	protected static List<Class> serializerClasses = [
		SelectEntityEvent.class,
		AckEvent.class,
		CreateGameEvent.class,
		MultiSelectEntityEvent.class,
		ClientTrysToLoginEvent.class,
		ClientLoggedOutEvent.class
	]

	Screen screen
	ServerStartAppState serverStart
	ServerLogicAppState serverLogic

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
		ServerMain app = new ServerMain();
		// app.start(JmeContext.Type.Headless); // headless type for servers!
		app.start();
	}

	@Override
	public void simpleInitApp() {
		//		flyCam.setDragToRotate(true);
		inputManager.setCursorVisible(true);
		pauseOnLostFocus = false
		
		screen = new Screen(this);
		guiNode.addControl(screen);

		serverLogic = new ServerLogicAppState(this);
		stateManager.attach(serverLogic);
		
		serverStart = new ServerStartAppState(this, screen);
		stateManager.attach(serverStart);
		serverStart.enabled = true
	}


	@Override
	public void simpleUpdate(float tpf) {
		float maxedTPF = Math.min(tpf, 0.1f);
		listener.setLocation(cam.getLocation());
		listener.setRotation(cam.getRotation());
		stateManager.update(maxedTPF);
		// view.getActorManager().render();
		// p1.getFieldCtrl().update(maxedTPF);
//		modelManager.updateConfigs();
	}

	def switchToServerControlAppStates() {

		serverStart.enabled = false
		stateManager.detach(serverStart)
		startServer()

		def severControl = new ServerControlAppState(this, screen);
		stateManager.attach(severControl);
		severControl.enabled = true
	}

	def startServer() {
		try {
			//@TODO use static property here
			Serializer.registerClasses(SelectEntityEvent.class,AckEvent.class,CreateGameEvent.class, MultiSelectEntityEvent.class, ClientTrysToLoginEvent.class, ClientLoggedOutEvent.class);
			myServer = Network.createServer(gameName, version, PORT, PORT);
			myServer.addMessageListener(new InputEventMessageListener(), SelectEntityEvent.class, AckEvent.class, CreateGameEvent.class, ClientTrysToLoginEvent.class, ClientLoggedOutEvent.class);
			myServer.addConnectionListener(new ConnectionListener());

			myServer.start();
			logger.info("Server listening at :" + PORT);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KernelException e) {
			EventManager.post(new ServerCouldNotStartetEvent(message: e.message));
			e.printStackTrace();
		}
	}
}