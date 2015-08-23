package network.client;

import java.util.logging.Logger;

import com.google.inject.Inject;
import com.jme3.app.Application;

import exception.TechnicalException;

public class ClientManager {

	private static final Logger logger = Logger.getLogger(ClientManager.class.getName());

	protected ClientAppState client;


	// @ApplicationRef
	@Inject
	private Application app;

	public void startClient(String host) {
		client = new ClientAppState(host);
		app.getStateManager().attach(client);
	}


	// public void stopClient() {
	// stateManager.detach(client);
	// }




}
