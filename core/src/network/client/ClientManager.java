package network.client;

import java.util.logging.Logger;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;

import exception.TechnicalException;

public class ClientManager {

	private static final Logger logger = Logger.getLogger(ClientManager.class.getName());

	private final static ClientManager instance = new ClientManager();
	protected static ClientAppState client = new ClientAppState();
	protected static AppStateManager stateManager;

	public static void startClient(AppStateManager stateManager, Application app) {
		client.initialize(stateManager, app);
		ClientManager.stateManager = stateManager;
		stateManager.attach(client);
		instance.waitUntilClientIsConnected(10);
	}

	public static void stopClient() {
		stateManager.detach(client);
	}

	public static ClientManager getInstance() {
		return instance;
	}


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

	public static ClientAppState getClient() {
		return client;
	}

}
