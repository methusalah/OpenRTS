package openrts.server


import model.ModelManager
import tonegod.gui.core.Screen
import event.network.AckEvent
import event.network.CreateGameEvent
import event.network.SelectEntityEvent

class OpenRTSServerTonegodGUI extends OpenRTSServerWithDI {

	protected static List<Class> serializerClasses = [
		SelectEntityEvent.class,
		AckEvent.class,
		CreateGameEvent.class
	]

	Screen screen


	//	static String mapfilename = "assets/maps/test.btf";
	//	static final Logger logger = Logger.getLogger(OpenRTSServerTonegodGUI.class.getName());
	//	static final String gameName = "OpenRTS";
	//	static int version = 1;
	//
	//	static Server myServer;
	//	static final int PORT = 6143;
	//	Map<Integer, Game> games = new HashMap<Integer, Game>();

	//	public Game getPlayer(Integer id) {
	//		return games.get(id);
	//	}

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

		def serverStart = new ServerStartAppState(this, screen);
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

		stateManager
		def severControl = new ServerControlAppState(this, screen);
		stateManager.attach(severControl);
		severControl.enabled = true
	}

}