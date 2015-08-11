package openrts.server

import model.ModelManager
import tonegod.gui.core.Screen
import app.OpenRTSApplicationWithDI
import event.network.AckEvent
import event.network.CreateGameEvent
import event.network.SelectEntityEvent

class OpenRTSServerTonegodGUI extends OpenRTSApplicationWithDI {

	protected static List<Class> serializerClasses = [
		SelectEntityEvent.class,
		AckEvent.class,
		CreateGameEvent.class
	]


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
		flyCam.setDragToRotate(true);
		inputManager.setCursorVisible(true);

		Screen screen = new Screen(this);
		guiNode.addControl(screen);
		//
		//		Menu subMenu = new Menu(screen,new Vector2f(0,0),false) {
		//					@Override
		//					public void onMenuItemClicked(int index, Object value, boolean isToggled) {
		//
		//
		//
		//					}
		//				};
		//		// Add a menu item
		//		subMenu.addMenuItem("server running", null, null);
		//		// Add a toggle-able menu item (checkbox)
		//		subMenu.addMenuItem("listen on events", null, null, true);
		//		// Add a toggle-able menu item and set the default state of the checkbox to checked
		//		subMenu.addMenuItem("Some string caption 3", null, null, true, true);
		//		screen.addElement(subMenu);
		//
		//		final Menu menu = new Menu(screen,new Vector2f(0,0),false) {
		//					@Override
		//					public void onMenuItemClicked(int index, Object value, boolean isToggled) {  }
		//				};
		//		// Add subMenu as a sub-menu to this menu item
		//		menu.addMenuItem("Some caption", null, subMenu);
		//		screen.addElement(menu);
		//
		//		ButtonAdapter b = new ButtonAdapter(screen, new Vector2f(50,50)) {
		//					@Override
		//					public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean isToggled) {
		//						menu.showMenu(null, getAbsoluteX(), (float) (getAbsoluteY()- menu.getHeight()));
		//					}
		//				};
		//		b.setText("Show Menu");
		//		screen.addElement(b);


		def xmlScreenSampeAppState = new XMLScreenSample(screen);
		stateManager.attach(xmlScreenSampeAppState);
		xmlScreenSampeAppState.enabled = true
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