package openrts.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Logger;

import model.EntityManager;
import model.ModelManager;
import model.battlefield.abstractComps.FieldComp;
import network.client.ClientManager;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.jme3.system.JmeContext;

import event.EventManager;
import event.SelectEntityEvent;
import event.SelectEntityServerEvent;
import event.ToClientEvent;

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
		EventManager.register(obj);

		ClientManager.startClient();

		int id = 1;
		FieldComp entity = EntityManager.getEntity(id);
		Assert.assertNotNull(entity);

		SelectEntityEvent evt = new SelectEntityEvent(id);
		ClientManager.getInstance().manageEvent(evt);

		waitUntilClientHasResponse(obj, 0);
		ToClientEvent ev = obj.getEvent();
		Assert.assertTrue(ev instanceof SelectEntityServerEvent);

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