package network.client;

import java.util.logging.Logger;

import com.google.inject.Inject;
import com.jme3.network.Client;

import event.EventManager;
import event.client.ClientIsConnectedEvent;

public class ClientStateListener implements com.jme3.network.ClientStateListener {

	private static final Logger logger = Logger.getLogger(ClientStateListener.class.getName());

	@Inject
	private EventManager eventManager;

	@Override
	public void clientConnected(Client c) {
		logger.info("connection to Server successfully. There is a game name:" + c.getGameName());
		ClientIsConnectedEvent evt = new ClientIsConnectedEvent(c.getVersion(), c.getGameName(), c.getId());
		eventManager.post(evt);
	}

	@Override
	public void clientDisconnected(Client c, DisconnectInfo info) {
		logger.warning("lost connection" + c.getGameName() + " because of " + info.reason);
	}

}
