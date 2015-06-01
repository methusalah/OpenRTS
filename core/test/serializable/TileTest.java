package serializable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import model.battlefield.map.Map;
import model.battlefield.map.Tile;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;

/**
 *
 * @author wuendsch
 */
public class TileTest {

	@Test
	public void binaryTiles() throws IOException {

		Map m = new Map();
		m.tiles.add(new Tile(20, 20, 1, 30.4, "test"));

		SmileFactory f = new SmileFactory();
		// can configure instance with 'SmileParser.Feature' and 'SmileGenerator.Feature'
		ObjectMapper mapper = new ObjectMapper(f);
		// and then read/write data as usual

		byte[] smileData = mapper.writeValueAsBytes(m.tiles);
		OutputStream out = new FileOutputStream("tiles.openrts");
		out.write(smileData);
		out.flush();
		out.close();

		InputStream in = new FileInputStream("map1.tiles");

		List<Tile> otherValue = mapper.readValue(in, List.class);
		in.close();
		System.out.println(otherValue);
		assert otherValue.size() == 64 * 64;

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