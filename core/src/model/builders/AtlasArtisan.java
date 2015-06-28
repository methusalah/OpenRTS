package model.builders;

import model.battlefield.map.Map;
import model.battlefield.map.atlas.Atlas;

public class AtlasArtisan {

	public static void buildAtlas(Map m) {
		if(m.getAtlas() == null)
			createNewAtlasOn(m);
		else
			finalizeAtlasOn(m);
	}

	private static void createNewAtlasOn(Map m) {
		m.setAtlas(new Atlas(m.xSize(), m.ySize()));
		m.setCover(new Atlas(m.xSize(), m.ySize()));
		finalizeAtlasOn(m);
	}

	private static void finalizeAtlasOn(Map m) {
		m.getAtlas().finalize();
		m.getCover().finalize();
	}
}
