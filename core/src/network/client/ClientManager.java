package network.client;

import java.util.logging.Logger;

import com.google.inject.Inject;
import com.jme3.app.Application;

import exception.TechnicalException;

public class ClientManager {

	private static final Logger logger = Logger.getLogger(ClientManager.class.getName());

	private final static ClientManager instance = new ClientManager();

	protected ClientAppState client;


	// @ApplicationRef
	@Inject
	private Application app;

	public void startClient() {
		client = new ClientAppState();
		client.initialize(app.getStateManager(), app);
		app.getStateManager().attach(client);
		instance.waitUntilClientIsConnected(10);
	}

	// public void stopClient() {
	// stateManager.detach(client);
	// }


	private void waitUntilClientIsConnected(int times) {
		int waitingCounter = times;
		boolean waiting = true;
		while (waiting) {

			if (client.isEnabled()) {
				waiting = false;
				return;
			}

			logger.info("Waiting for answer from server...");
			if (times > 0 && waitingCounter > times) {
				throw new TechnicalException("Client are waiting too long..");
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
			waitingCounter++;
		}
	}


}
