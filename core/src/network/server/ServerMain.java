package network.server;

import java.io.IOException;

import network.msg.HelloMessage;

import com.jme3.app.SimpleApplication;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.system.JmeContext;

public class ServerMain extends SimpleApplication {
  public static void main(String[] args) {
    ServerMain app = new ServerMain();
    app.start(JmeContext.Type.Headless); // headless type for servers!
  }

  public void simpleInitApp() {
	    Server myServer;
		try {
			myServer = Network.createServer(6143);
			myServer.start();
			myServer.addMessageListener(new ServerListener(), HelloMessage.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Serializer.registerClass(HelloMessage.class);
		
	  }
}