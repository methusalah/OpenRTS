package openrts.server
;

import java.util.logging.Logger

import tonegod.gui.controls.windows.LoginBox
import tonegod.gui.core.Screen

import com.jme3.app.Application
import com.jme3.app.state.AbstractAppState
import com.jme3.app.state.AppStateManager
import com.jme3.input.event.MouseButtonEvent
import com.jme3.math.Vector2f

class ServerStartAppState extends AbstractAppState {

	static final Logger logger = Logger.getLogger(ServerStartAppState.class.getName());
	OpenRTSServerTonegodGUI app;
	Screen screen;

	LoginBox loginWindow;

	ServerStartAppState(OpenRTSServerTonegodGUI app ,Screen screen) {
		this.app = app;
		this.screen = screen;
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		initLoginWindow();
		//		initMenu()
	}


	//	def initMenu() {
	//		Menu subMenu = new Menu(screen,new Vector2f(0,0),false) {
	//					@Override
	//					public void onMenuItemClicked(int index, Object value, boolean isToggled) {
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
	//	}

	public void initLoginWindow() {
		loginWindow = new LoginBox(screen, "loginWindow", new Vector2f((Float) (screen.getWidth() / 2 - 175), (Float) (screen.getHeight() / 2 - 125))) {
					@Override
					public void onButtonLoginPressed(MouseButtonEvent evt, boolean toggled) {
						// Some call to the server to log the client in
						finalizeUserLogin();
					}

					@Override
					public void onButtonCancelPressed(MouseButtonEvent arg0, boolean arg1) {
						// TODO Auto-generated method stub

					}
				};
		screen.addElement(loginWindow);
	}

	@Override
	public void cleanup() {
		super.cleanup();

		screen.removeElement(loginWindow);
	}

	public void finalizeUserLogin() {
		// Some call to your app to unload this AppState and load the next AppState
		app.switchToServerControlAppStates();
		logger.info("Login was pressed");
	}
}