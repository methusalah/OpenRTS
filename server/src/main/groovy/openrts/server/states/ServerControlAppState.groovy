package openrts.server.states
;

import java.util.logging.Logger

import openrts.event.ServerEvent
import openrts.server.OpenRTSServerTonegodGUI
import openrts.server.controls.EventBox
import openrts.server.controls.UserBox
import tonegod.gui.core.Screen

import com.google.common.eventbus.Subscribe
import com.google.inject.Inject
import com.jme3.app.Application
import com.jme3.app.state.AbstractAppState
import com.jme3.app.state.AppStateManager
import com.jme3.math.Vector2f

import event.ClientLoggedOutEvent
import event.ClientTrysToLoginEvent
import event.EventManager
import event.network.NetworkEvent
import groovy.transform.CompileStatic

@CompileStatic
class ServerControlAppState extends AbstractAppState {

	static final Logger logger = Logger.getLogger(ServerControlAppState.class.getName());
	OpenRTSServerTonegodGUI app;
	Screen screen;

	EventBox eventBox;
	
	UserBox userBox;

	@Inject
	ServerControlAppState( OpenRTSServerTonegodGUI app , Screen screen) {
		this.app = app;
		this.screen = screen;
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		initControlWindow();
		EventManager.register(this);
	}

	public void initControlWindow() {
		eventBox = new EventBox(screen, "Events", new Vector2f((Float) (screen.getWidth() / 2 - 175), (Float) (screen.getHeight() / 2 - 125))) {

					@Override
					public void onClientConnected(String msg) {
						this.receiveClientConnection(msg);
					}

					@Override
					public void onEventReceived(String msg) {
						this.receiveEvent(msg);
					}
				}
		userBox  = new UserBox(screen, "Users", new Vector2f((Float) (screen.getWidth() / 2 - 35), (Float) (screen.getHeight() / 2 - 15)),new Vector2f((Float) (100), (Float) (100))) {					
					@Override
					public void onClientConnected(String msg) {
						this.addClient(msg);
					}
					
					@Override
					public void onClientDisconnected(String msg) {
						this.addClient(msg);
					}
				}
		screen.addElement(eventBox);
		screen.addElement(userBox);
	}

	@Override
	public void cleanup() {
		super.cleanup();

		screen.removeElement(eventBox);
		EventManager.unregister(this);
	}

	public void finalizeUserLogin() {
		// Some call to your app to unload this AppState and load the next AppState
		//app.switchToServerControlAppStates();
		logger.info("Login was pressed");
	}

	@Subscribe
	def logNetworkEvents(NetworkEvent evt) {
		eventBox.receiveEvent("receive Networkmessage:" + evt)
	}


	@Subscribe
	def logSeverEvents(ServerEvent evt) {
		eventBox.receiveClientConnection("receive Networkmessage:" + evt)
	}
	
	@Subscribe
	def logSeverEvents(ClientTrysToLoginEvent evt) {
		userBox.addClient(evt.getUser())
	}
	
	@Subscribe
	def logSeverEvents(ClientLoggedOutEvent evt) {
		userBox.removeClient(evt.getUser())
	}
}
