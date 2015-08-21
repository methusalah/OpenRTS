package openrts.server


import java.util.logging.Logger

import model.ModelManager
import openrts.event.ServerCouldNotStartetEvent
import tonegod.gui.core.Screen

import com.jme3.network.Network
import com.jme3.network.kernel.KernelException
import com.jme3.network.serializing.Serializer

import event.EventManager
import event.network.AckEvent
import event.network.CreateGameEvent
import event.network.MultiSelectEntityEvent
import event.network.SelectEntityEvent
import groovy.transform.CompileStatic

@CompileStatic
class OpenRTSServerTonegodGUI extends OpenRTSServerWithDI {

	static final Logger logger = Logger.getLogger(OpenRTSServerTonegodGUI.class.getName());

	protected static List<Class> serializerClasses = [
		SelectEntityEvent.class,
		AckEvent.class,
		CreateGameEvent.class
	]

	Screen screen
	ServerStartAppState serverStart

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
		OpenRTSServerTonegodGUI app = new OpenRTSServerTonegodGUI();
		// app.start(JmeContext.Type.Headless); // headless type for servers!
		app.start();
	}

	@Override
	public void simpleInitApp() {
		//		flyCam.setDragToRotate(true);
		inputManager.setCursorVisible(true);

		screen = new Screen(this);
		guiNode.addControl(screen);

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
		ModelManager.updateConfigs();
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

			Serializer.registerClasses(SelectEntityEvent.class,AckEvent.class,CreateGameEvent.class, MultiSelectEntityEvent.class);
			myServer = Network.createServer(gameName, version, PORT, PORT);
			myServer.addMessageListener(new InputEventMessageListener(), SelectEntityEvent.class, AckEvent.class, CreateGameEvent.class);
			myServer.addConnectionListener(new ConnectionListener());

			myServer.start();
			logger.info("Server listening at :" + PORT);
			System.out.println("Server listening at :" + PORT);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KernelException e) {
			EventManager.post(new ServerCouldNotStartetEvent(message: e.message));
			e.printStackTrace();
		}
	}
}