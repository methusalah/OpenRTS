package network.client;

import java.io.IOException;

import network.msg.HelloMessage;

import com.jme3.app.SimpleApplication;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import com.jme3.system.JmeContext;

public class ClientMain extends SimpleApplication {
	public static void main(String[] args) {
		ClientMain app = new ClientMain();
		app.start(JmeContext.Type.Display); // standard display type
	}

	public void simpleInitApp() {
		Client myClient;
		try {
			myClient = Network.connectToServer("localhost", 6143);
			myClient.start();
			myClient.addMessageListener(new ClientListener(), HelloMessage.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Serializer.registerClass(HelloMessage.class);

	}
}