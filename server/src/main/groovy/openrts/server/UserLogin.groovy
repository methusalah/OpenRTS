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

class UserLogin extends AbstractAppState {

	static final Logger logger = Logger.getLogger(UserLogin.class.getName());
	OpenRTSServerTonegodGUI app;
	Screen screen;

	LoginBox loginWindow;

	UserLogin(OpenRTSServerTonegodGUI app ,Screen screen) {
		this.app = app;
		this.screen = screen;
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		initLoginWindow();
	}

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