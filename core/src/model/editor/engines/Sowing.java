package model.editor.engines;

import geometry.geom2d.Point2D;
import geometry.math.AngleUtil;

import java.util.ArrayList;
import java.util.List;

import model.ModelManager;
import model.battlefield.map.Map;
import model.battlefield.map.Tile;
import model.battlefield.map.Trinket;
import model.battlefield.map.atlas.AtlasLayer;
import model.builders.entity.TrinketBuilder;
import model.builders.entity.definitions.BuilderManager;


public class Sowing {

	private double distFromCliff = 0;
	private double slopeMin = 0, slopeMax = 0;
	private List<String> textures = new ArrayList<>();
	private List<Double> textureMin = new ArrayList<>();
	private List<Double> textureMax = new ArrayList<>();

	public List<TrinketBuilder> trinketBuilders = new ArrayList<>();
	public List<Double> probs = new ArrayList<>();
	public List<Double> spacings = new ArrayList<>();
	List<Trinket> toGrow = new ArrayList<>();


	public Sowing() {

	}

	public void addTrinket(String id, double weight, double spacing){
		for (int i = 0; i < weight; i++) {
			trinketBuilders.add(BuilderManager.getTrinketBuilder(id));
			spacings.add(spacing);
		}
	}
	
	public void addTexture(String texture, double min, double max){
		textures.add(texture);
		textureMin.add(min);
		textureMax.add(max);
	}

	public void setMinSlope(double slope){
		slopeMin = slope;
	}

	public void setMaxSlope(double slope){
		slopeMax = slope;
	}

	public void setCliffDist(double dist){
		distFromCliff = dist;
	}

	public boolean isAllowed(Point2D p){
		Map map = ModelManager.getBattlefield().getMap();
		// check if the point is near enough from a cliff.
		if(distFromCliff > 0){
			boolean hasCliff = false;
			// ugly
			for(Tile t : map.getInCircle(p, distFromCliff)) {
				if(t.hasCliff()){
					hasCliff = true;
					break;
				}
			}
			if(!hasCliff) {
				return false;
			}
		}


		// check if the point is on a correctly sloped terrain.
		if(slopeMin > 0 || slopeMax > 0){
			double dist = map.getNormalVectorAt(p).get2D().getLength();
			double angle = AngleUtil.RIGHT - new Point2D(dist, 1).getAngle();
			if (slopeMin > 0 && angle < AngleUtil.toRadians(slopeMin)) {
				return false;
			}
			if (slopeMax > 0 && angle > AngleUtil.toRadians(slopeMax)) {
				return false;
			}
		}

		// check if the point is on allowed ground texture
		if(!textures.isEmpty()){
			int i=0;
			for(String s : textures){
				AtlasLayer l;
				int texIndex = Integer.parseInt(s);
				if(texIndex >= 8)
					l = map.getCover().getLayers().get(texIndex-8);
				else
					l = map.getAtlas().getLayers().get(texIndex);
					
				if(l.getInAtlasSpace(p) < textureMin.get(i)*255 ||
						l.getInAtlasSpace(p) > textureMax.get(i)*255){
					return false;
				}
				i++;
			}
		}
		return true;
	}

}
