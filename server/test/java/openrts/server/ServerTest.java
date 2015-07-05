package openrts.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

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
import event.InputEvent;
import event.ServerEvent;
import event.WorldChangedEvent;
import geometry.geom2d.Point2D;

/**
 * @author mario
 */
public class ServerTest {

	@Test
	public void testInput() throws Exception {

		OpenRTSServer app = new OpenRTSServer();
		app.start(JmeContext.Type.Headless);

		waitUntilServerIsStarted();

		ClientEventListenerMock obj = new ClientEventListenerMock();
		EventManager.registerForClient(obj);

		ClientManager.startClient();
		InputEvent evt = new InputEvent(MultiplayerGameInputInterpreter.SELECT, new Point2D(1, 1), true);
		ClientManager.getInstance().manageEvent(evt);

		waitUntilClientHasResponse(obj);
		ServerEvent ev = obj.getEvent();
		Assert.assertTrue(ev instanceof WorldChangedEvent);

	}

	private void waitUntilClientHasResponse(ClientEventListenerMock mock) throws IOException {
		int waitingCounter = 0;
		boolean waiting = true;
		while (waiting) {
			waitingCounter++;
			if (mock.getEvent() != null) {
				waiting = false;
			}
			if (waitingCounter > 10) {
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
				System.out.println("Connect failed, waiting and trying again");
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