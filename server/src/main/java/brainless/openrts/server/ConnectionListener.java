package brainless.openrts.server;

import java.util.logging.Logger;

import brainless.openrts.event.ClientConnectedEvent;
import brainless.openrts.event.ClientDisconnectedEvent;
import brainless.openrts.event.EventManager;

import com.jme3.network.HostedConnection;
import com.jme3.network.Server;


public class ConnectionListener implements com.jme3.network.ConnectionListener {

	private static final Logger logger = Logger.getLogger(ConnectionListener.class.getName());

	@Override
	public void connectionAdded(Server server, HostedConnection conn) {
		logger.info(server.getGameName() + " has a new connection:" + conn.getId());
		EventManager.post(new ClientConnectedEvent(id: conn.id,address: conn.address));
	}

	@Override
	public void connectionRemoved(Server server, HostedConnection conn) {
		logger.info(server.getGameName() + " lost a connection:" + conn.getId());
		EventManager.post(new ClientDisconnectedEvent(id: conn.id,address: conn.address));
	}
}
