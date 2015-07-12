package model.builders;

import geometry.geom2d.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import model.battlefield.map.Map;
import model.battlefield.map.atlas.Atlas;
import model.battlefield.map.atlas.AtlasLayer;

public abstract class AtlasArtisanUtil {

	private static final Logger logger = Logger.getLogger(AtlasArtisanUtil.class.getName());

	public static void buildAtlas(Map m) {
		if(m.getAtlas() == null) {
			createNewAtlasOn(m);
		} else {
			finalizeAtlasOn(m);
		}
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

	public static void incrementPixel(Atlas atlas, Point2D p, AtlasLayer layer, double increment) {
		int x = (int) Math.round(p.x);
		int y = (int) Math.round(p.y);

		double valueToDitribute = increment;
		ArrayList<AtlasLayer> availableLayers = new ArrayList<>();
		for (AtlasLayer l : atlas.getLayers()) {
			if (l == layer) {
				valueToDitribute -= l.addAndReturnExcess(x, y, increment);
			} else {
				if(l.get(x, y) > 0) {
					availableLayers.add(l);
				}
			}
		}
		int secur = 0;
		while (valueToDitribute > 0 && !availableLayers.isEmpty()) {
			ArrayList<AtlasLayer> unavailableLayers = new ArrayList<>();
			double shared = Math.round(valueToDitribute / availableLayers.size());
			valueToDitribute = 0;
			for (AtlasLayer l : availableLayers) {
				valueToDitribute += l.withdrawAndReturnExcess(x, y, shared);
				if (l.get(x, y) == 0) {
					unavailableLayers.add(l);
				}
			}
			availableLayers.removeAll(unavailableLayers);
			if(secur++ > 40){
				logger.warning("Impossible to distribute value");
				break;
			}
		}
		atlas.updatePixel(x, y);
	}

	public static void decrementPixel(Atlas atlas, Point2D p, AtlasLayer layer, double increment) {
		int x = (int) Math.round(p.x);
		int y = (int) Math.round(p.y);

		double valueToDitribute = increment;
		ArrayList<AtlasLayer> availableLayers = new ArrayList<>();
		for (AtlasLayer l : atlas.getLayers()) {
			if (l == layer) {
				valueToDitribute -= l.withdrawAndReturnExcess(x, y, increment);
			} else if (l.get(x, y) > 0) {
				availableLayers.add(l);
			}
		}
		if (availableLayers.isEmpty()) {
			availableLayers.add(atlas.getLayers().get(0));
		}

		int secur = 0;
		while (valueToDitribute > 0 && !availableLayers.isEmpty()) {
			ArrayList<AtlasLayer> unavailableLayers = new ArrayList<>();
			double shared = valueToDitribute / availableLayers.size();
			valueToDitribute = 0;
			for (AtlasLayer l : availableLayers) {
				valueToDitribute += l.addAndReturnExcess(x, y, shared);
				if (l.get(x, y) == 255) {
					unavailableLayers.add(l);
				}
			}
			availableLayers.removeAll(unavailableLayers);
			if(secur++ > 40){
				logger.warning("Impossible to distribute value");
				break;
			}
		}
		atlas.updatePixel(x, y);
	}

	public static void smoothPixel(Atlas atlas, Point2D p, double increment){
		int x = (int) Math.round(p.x);
		int y = (int) Math.round(p.y);
		List<AtlasLayer> painted = new ArrayList<>();
		for (AtlasLayer l : atlas.getLayers()) {
			if (l.get(x, y) != 0) {
				painted.add(l);
			}
		}
		double targetVal = 255 / painted.size();
		for (AtlasLayer l : painted) {
			double diff = targetVal - l.get(x, y);
			if (diff < 0) {
				l.set(x, y, l.get(x, y) + Math.max(diff, -increment));
			} else if (diff > 0) {
				l.set(x, y, l.get(x, y) + Math.min(diff, increment));
			}
		}
		atlas.updatePixel(x, y);
	}
}
