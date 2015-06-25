package model.builders;

import model.battlefield.Battlefield;
import model.battlefield.map.Map;
import model.builders.entity.MapStyleBuilder;
import model.builders.entity.definitions.BuilderManager;

public class MapBuilder {

	
	public void buildMap(Battlefield b){
		if(b.getMap() == null)
			createMapOn(b);
		else
			finalizeMapOn(b);
	}
	
	
	public void createMapOn(Battlefield b){
		Map m = new Map(BuilderManager.getMapStyleBuilder("StdMapStyle").build());

	}

	public void finalizeMapOn(Battlefield b){
		
	}
}
