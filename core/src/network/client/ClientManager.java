package network.client;

import java.io.IOException;

import network.server.OpenRTSServer;

import com.google.common.eventbus.Subscribe;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;

import event.Event;
import event.EventManager;
import event.ControllerChangeEvent;

public class ClientManager {

	private final static ClientManager instance = new ClientManager();
	protected static Client client;

	public static void startClient() {
		Serializer.registerClass(Event.class);
		try {
			Thread.sleep(1000);
			client = Network.connectToServer("localhost", OpenRTSServer.PORT);
			client.addClientStateListener(new ClientStateListener());
			client.addMessageListener(new MessageListener(), Event.class);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client.start();
		
		EventManager.register(instance);
	}

	public static void stopClient() {
		client.close();
	}

	public ClientManager getInstance() {
		return instance;
	}

	@Subscribe
	public void manageEvent(ControllerChangeEvent ev) {
		if (client.isConnected()) {
			client.send(ev);
		}
	}

}
