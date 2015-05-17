package network.client;

import java.io.IOException;

import network.msg.HelloMessage;
import network.server.OpenRTSServer;

import com.google.common.eventbus.Subscribe;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;

import event.EventManager;
import event.InputEvent;

public class ClientManager {

	private final static ClientManager instance = new ClientManager();
	protected static Client client;

	public static void startClient() throws IOException {
		Serializer.registerClass(HelloMessage.class);
		client = Network.connectToServer("localhost", OpenRTSServer.PORT);
		client.start();
		client.addMessageListener(new ClientListener(), HelloMessage.class);
		EventManager.register(instance);
	}

	public static void stopClient() {
		client.close();
	}

	public ClientManager getInstance() {
		return instance;
	}

	@Subscribe
	public void manageEvent(InputEvent ev) {
		if (client.isConnected()) {
			client.send(new HelloMessage(ev.getActionCommand()));
		}
	}

}
