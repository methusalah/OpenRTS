package network.server;

import java.util.logging.Logger;

import com.jme3.network.HostedConnection;
import com.jme3.network.Server;


public class ConnectionListener implements com.jme3.network.ConnectionListener {

	private static final Logger logger = Logger.getLogger(ConnectionListener.class.getName());

	@Override
	public void connectionAdded(Server server, HostedConnection conn) {
		logger.info(server.getGameName() + " has a new connection:" + conn.getId());

	}

	@Override
	public void connectionRemoved(Server server, HostedConnection conn) {
		logger.info(server.getGameName() + " lost a connection:" + conn.getId());

	}

}
