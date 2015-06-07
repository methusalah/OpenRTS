package map;

import model.ModelManager;
import model.battlefield.Battlefield;
import model.battlefield.BattlefieldFactory;
import model.battlefield.map.Map;
import model.battlefield.map.parcel.ParcelManager;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author wuendsch
 */
public class BattleFieldFactoryTest {

	public BattleFieldFactoryTest() {
	}

	@Test
	public void loading() {

		ModelManager.updateConfigs();

		BattlefieldFactory bff = new BattlefieldFactory();
		Battlefield field = bff.load("assets/maps/tiny03.btf");
		Map map = field.getMap();
		assert map != null;
		assert map.width == 4;
		assert map.height == 4;

		ParcelManager.createParcelMeshes(map);

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