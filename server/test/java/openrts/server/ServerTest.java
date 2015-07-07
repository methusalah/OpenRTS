package openrts.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Logger;

import model.ModelManager;
import network.client.ClientManager;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.jme3.system.JmeContext;

import controller.game.MultiplayerGameInputInterpreter;
import event.EventManager;
import event.MapInputEvent;
import event.ServerEvent;
import event.WorldChangedEvent;
import geometry.geom3d.Point3D;

/**
 * @author mario
 */
public class ServerTest {

	private static final Logger logger = Logger.getLogger(ServerTest.class.getName());
	protected static String mapfilename = "assets/maps/test.btf";

	@Test
	public void testInput() throws Exception {

		OpenRTSServer app = new OpenRTSServer();
		app.start(JmeContext.Type.Headless);
		if (!mapfilename.isEmpty()) {
			ModelManager.loadBattlefield(mapfilename);
		}

		waitUntilServerIsStarted();

		ClientEventListenerMock obj = new ClientEventListenerMock();
		EventManager.registerForClient(obj);

		ClientManager.startClient();
		// see map => tank position hardcoded
		// "pos" : {
		// "x" : 8.292814254760742,
		// "y" : 52.28246307373047,
		// "z" : 0.0
		// },

		Point3D origin = new Point3D(8.292814254760742, 52.28246307373047, 0.0);
		MapInputEvent evt = new MapInputEvent(MultiplayerGameInputInterpreter.SELECT, origin, false);
		ClientManager.getInstance().manageEvent(evt);

		waitUntilClientHasResponse(obj, 0);
		ServerEvent ev = obj.getEvent();
		Assert.assertTrue(ev instanceof WorldChangedEvent);

	}


	private void waitUntilClientHasResponse(ClientEventListenerMock mock, int times) throws IOException {
		int waitingCounter = times;
		boolean waiting = true;
		while (waiting) {
			waitingCounter++;
			if (mock.getEvent() != null) {
				waiting = false;
			} else {
				logger.info("Waiting for answer from server...");
			}
			if (times > 0 && waitingCounter > 10) {
				Assert.fail("Client are waiting too long..");
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}

	private void waitUntilServerIsStarted() throws IOException {
		boolean scanning = true;
		while (scanning) {
			Socket socket = new Socket();
			try {
				InetSocketAddress sa = new InetSocketAddress("localhost", OpenRTSServer.PORT);

				socket.connect(sa, 500);
				scanning = false;
			} catch (IOException e) {
				logger.severe("Connect failed, waiting and trying again");
			} finally {
				socket.close();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@BeforeMethod
	public void setUpMethod() throws Exception {
	}

	@AfterMethod
	public void tearDownMethod() throws Exception {
	}
}