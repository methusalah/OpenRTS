package network.client;

import java.util.logging.Logger;

import com.jme3.network.Client;


public class ClientStateListener implements com.jme3.network.ClientStateListener {

	private static final Logger logger = Logger.getLogger(ClientStateListener.class.getName());

	@Override
	public void clientConnected(Client c) {
		logger.info("connection to Server successfully. There is a game name:" + c.getGameName());

	}

	@Override
	public void clientDisconnected(Client c, DisconnectInfo info) {
		logger.warning("lost connection" + c.getGameName() + " because of " + info.reason);
	}

}
