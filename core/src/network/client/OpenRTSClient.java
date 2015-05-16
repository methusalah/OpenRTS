package network.client;

import java.io.IOException;

import network.msg.HelloMessage;
import network.server.OpenRTSServer;

import com.jme3.app.SimpleApplication;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import com.jme3.system.JmeContext;

public class OpenRTSClient extends SimpleApplication {

	public static void main(String[] args) {
		OpenRTSClient app = new OpenRTSClient();
		app.start(JmeContext.Type.Display); // standard display type
	}

	@Override
	public void simpleInitApp() {
		Client myClient;
		try {
			myClient = Network.connectToServer("localhost", OpenRTSServer.PORT);
			myClient.start();
			myClient.addMessageListener(new ClientListener(), HelloMessage.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Serializer.registerClass(HelloMessage.class);

	}
}