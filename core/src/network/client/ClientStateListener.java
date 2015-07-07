package network.client;

import tools.LogUtil;

import com.jme3.network.Client;


public class ClientStateListener implements com.jme3.network.ClientStateListener {

	@Override
	public void clientConnected(Client c) {
		LogUtil.info("connection to Server successfully. There is a game name:"  + c.getGameName());
		
	}

	@Override
	public void clientDisconnected(Client c, DisconnectInfo info) {
		LogUtil.warning("lost connection" + c.getGameName() + " because of " + info.reason);
	}

}
