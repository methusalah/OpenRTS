package brainless.openrts.server.states

import brainless.openrts.event.ClientDisconnectedEvent;
import brainless.openrts.event.ClientLoggedOutEvent;
import brainless.openrts.event.ClientTrysToLoginEvent;
import brainless.openrts.event.EventManager
import brainless.openrts.model.Player
import brainless.openrts.server.ServerMain;

import com.google.common.eventbus.Subscribe;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

import groovy.transform.CompileStatic

@CompileStatic
class ServerLogicAppState extends AbstractAppState {
	
	 Map<Integer,String> loggedInPlayer = [:]
	
	ServerLogicAppState(){
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		EventManager.register(this);
	}
	
	@Subscribe
	def logSeverEvents(ClientTrysToLoginEvent evt) {
		loggedInPlayer.put(evt.connectionId, evt.getUser())
	}
	
	@Subscribe
	def logSeverEvents(ClientLoggedOutEvent evt) {
		loggedInPlayer.remove(evt.getId())
	}
	
	@Subscribe
	def logSeverEvents(ClientDisconnectedEvent evt) {
		EventManager.post(new ClientLoggedOutEvent(evt.id, loggedInPlayer.get(evt.id)))
		loggedInPlayer.remove(evt.id)
	}
	

}
