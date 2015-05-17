package network.server;

import geometry.tools.LogUtil;

import java.io.IOException;

import network.msg.HelloMessage;

import com.jme3.app.SimpleApplication;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.system.JmeContext;

public class OpenRTSServer extends SimpleApplication {

	public static final int PORT = 6143;

	public static void main(String[] args) {
		OpenRTSServer app = new OpenRTSServer();
		app.start(JmeContext.Type.Headless); // headless type for servers!
	}

	@Override
	public void simpleInitApp() {
		Server myServer;
		try {
			myServer = Network.createServer(PORT, PORT);
			myServer.start();
			myServer.addMessageListener(new ServerListener(), HelloMessage.class);
			LogUtil.logger.info("Server listening at :" + PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Serializer.registerClass(HelloMessage.class);
	}
}