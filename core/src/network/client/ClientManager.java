package network.client;

import java.io.IOException;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;

import event.EventManager;
import event.InputEvent;
import event.ToClientEvent;
import event.ToServerEvent;

public class ClientManager {

	private static final Logger logger = Logger.getLogger(ClientManager.class.getName());

	private final static ClientManager instance = new ClientManager();
	protected static Client client;

	public static void startClient() {
		Serializer.registerClass(ToServerEvent.class);
		Serializer.registerClass(InputEvent.class);
		Serializer.registerClass(ToClientEvent.class);
		EventManager.registerForClient(instance);
		try {
			Thread.sleep(2000);
			client = Network.connectToServer("localhost", 6143);
			client.addClientStateListener(new ClientStateListener());
			client.addMessageListener(new MessageListener(), ToServerEvent.class);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		client.start();
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

}
