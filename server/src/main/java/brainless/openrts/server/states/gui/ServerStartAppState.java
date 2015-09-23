package brainless.openrts.server.states.gui
;

import java.util.logging.Logger;

import tonegod.gui.core.Screen;
import brainless.openrts.server.controls.ServerStartBox;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.sun.corba.se.impl.activation.ServerMain;


class ServerStartAppState extends AbstractAppState {

	static final Logger logger = Logger.getLogger(ServerStartAppState.class.getName());
	private ServerMain app;
	private Screen screen;

	private ServerStartBox loginWindow;

	ServerStartAppState(ServerMain app ,Screen screen) {
		this.app = app;
		this.screen = screen;
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		initLoginWindow();
	}

	public void initLoginWindow() {
		loginWindow = new ServerStartBox(screen, "loginWindow", new Vector2f((Float) (screen.getWidth() / 2 - 175), (Float) (screen.getHeight() / 2 - 125))) {

					@Override
					public void onButtonStartPressed(MouseButtonEvent evt, boolean toggled) {
						// Some call to the server to log the client in
						finalizeStart();
					}

					@Override
					public void onButtonCancelPressed(MouseButtonEvent arg0, boolean arg1) {
						logger.info("bye bye Server");
						System.exit(0);

					}
				};
		screen.addElement(loginWindow);
	}
	
	public void stateDetached(AppStateManager stateManager) {
		screen.removeElement(loginWindow);
		loginWindow.cleanup();
		super.stateDetached(stateManager);
	}

	private void finalizeStart() {
		// Some call to your app to unload this AppState and load the next AppState
		app.switchToServerControlAppStates();
		logger.info("Login was pressed");
	}
}