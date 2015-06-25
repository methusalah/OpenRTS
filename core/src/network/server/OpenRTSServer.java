package network.server;

import java.io.IOException;
import java.util.logging.Logger;

import com.jme3.app.SimpleApplication;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.system.JmeContext;

import event.NetworkEvent;

public class OpenRTSServer extends SimpleApplication {

	private static final Logger logger = Logger.getLogger(OpenRTSServer.class.getName());

	protected static Server myServer;
	public static final int PORT = 6143;

	public static void main(String[] args) {
		OpenRTSServer app = new OpenRTSServer();
		app.start(JmeContext.Type.Headless); // headless type for servers!
	}

	@Override
	public void simpleInitApp() {
		try {
			myServer = Network.createServer(PORT, PORT);
			myServer.addMessageListener(new MessageListener(), NetworkEvent.class);
			myServer.addConnectionListener(new ConnectionListener());
			Serializer.registerClass(NetworkEvent.class);

			myServer.start();
			logger.info("Server listening at :" + PORT);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}