package network;

import network.server.OpenRTSServer;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 *
 * @author wuendsch
 */
public class ServerTest {

	public ServerTest() {
	}
	// TODO add test methods here.
	// The methods must be annotated with annotation @Test. For example:
	//

	// @Test

	public void network() {

		OpenRTSServer server = new OpenRTSServer();
		server.start();

		// OpenRTSClient client = new OpenRTSClient();
		// client.start();

		// assert ((new LifeLeechComponent()) instanceof Component);
		// assert e.hasComponent(LifeLeechComponent.class);
		//        assert false;

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