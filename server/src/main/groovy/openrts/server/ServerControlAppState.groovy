package openrts.server
;

import java.util.logging.Logger

import openrts.server.gui.EventBox
import tonegod.gui.core.Screen

import com.jme3.app.Application
import com.jme3.app.state.AbstractAppState
import com.jme3.app.state.AppStateManager
import com.jme3.math.Vector2f

class ServerControlAppState extends AbstractAppState {

	static final Logger logger = Logger.getLogger(ServerControlAppState.class.getName());
	OpenRTSServerTonegodGUI app;
	Screen screen;

	EventBox eventPanel;

	ServerControlAppState(OpenRTSServerTonegodGUI app ,Screen screen) {
		this.app = app;
		this.screen = screen;
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		initControlWindow();
	}

	public void initControlWindow() {
		eventPanel = new EventBox(screen, "Events", new Vector2f((Float) (screen.getWidth() / 2 - 175), (Float) (screen.getHeight() / 2 - 125))) {

					@Override
					public void onSendMsg(String msg) {
						finalizeUserLogin()
					}
				}

		//		loginWindow = new LoginBox(screen, "loginWindow", new Vector2f((Float) (screen.getWidth() / 2 - 175), (Float) (screen.getHeight() / 2 - 125))) {
		//					@Override
		//					public void onButtonLoginPressed(MouseButtonEvent evt, boolean toggled) {
		//						// Some call to the server to log the client in
		//						finalizeUserLogin();
		//					}
		//
		//					@Override
		//					public void onButtonCancelPressed(MouseButtonEvent arg0, boolean arg1) {
		//						// TODO Auto-generated method stub
		//
		//					}
		//				};
		screen.addElement(eventPanel);
	}

	@Override
	public void cleanup() {
		super.cleanup();

		screen.removeElement(eventPanel);
	}

	public void finalizeUserLogin() {
		// Some call to your app to unload this AppState and load the next AppState
		//app.switchToServerControlAppStates();
		logger.info("Login was pressed");
	}
}