package network.client;

import java.io.IOException;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;

import event.EventManager;
import event.MapInputEvent;
import event.ToClientEvent;
import event.ToServerEvent;
import exception.TechnicalException;

public class ClientManager {

	private static final Logger logger = Logger.getLogger(ClientManager.class.getName());

	private final static ClientManager instance = new ClientManager();
	protected static Client client;

	public static void startClient() {
		Serializer.registerClass(ToServerEvent.class);
		Serializer.registerClass(MapInputEvent.class);
		Serializer.registerClass(ToClientEvent.class);
		EventManager.registerForClient(instance);
		try {
			client = Network.connectToServer("localhost", 6143);
			client.addClientStateListener(new ClientStateListener());
			client.addMessageListener(new MessageListener(), ToServerEvent.class);
		} catch (IOException e) {
			logger.severe(e.getLocalizedMessage());
		}
		client.start();
		instance.waitUntilClientIsConnected(10);
	}

	public static void stopClient() {
		client.close();
	}

	public static ClientManager getInstance() {
		return instance;
	}

	@Subscribe
	public void manageEvent(ToServerEvent ev) {
		if (client.isConnected()) {
			client.send(ev);
		}
	}

	private void waitUntilClientIsConnected(int times) {
		int waitingCounter = times;
		boolean waiting = true;
		while (waiting) {

			if (client.isConnected()) {
				waiting = false;
				return;
			} else {
				logger.info("Waiting for answer from server...");
			}
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
