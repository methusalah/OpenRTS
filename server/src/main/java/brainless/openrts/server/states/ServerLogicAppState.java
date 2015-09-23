package brainless.openrts.server.states;

import java.util.HashMap;
import java.util.Map;

import brainless.openrts.event.ClientDisconnectedEvent;
import brainless.openrts.event.ClientLoggedOutEvent;
import brainless.openrts.event.ClientTrysToLoginEvent;
import brainless.openrts.event.EventManager;

import com.google.common.eventbus.Subscribe;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

public class ServerLogicAppState extends AbstractAppState {
	
	 Map<Integer,String> loggedInPlayer = new HashMap<Integer, String>();
	
	public ServerLogicAppState(){
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		EventManager.register(this);
	}
	
	@Subscribe
	private void logSeverEvents(ClientTrysToLoginEvent evt) {
		loggedInPlayer.put(evt.getConnectionId(), evt.getUser());
	}
	
	@Subscribe
	private void logSeverEvents(ClientLoggedOutEvent evt) {
		loggedInPlayer.remove(evt.getId());
	}
	
	@Subscribe
	private void logSeverEvents(ClientDisconnectedEvent evt) {
		EventManager.post(new ClientLoggedOutEvent(evt.getId(), loggedInPlayer.get(evt.getId())));
		loggedInPlayer.remove(evt.getId());
	}
	

}
