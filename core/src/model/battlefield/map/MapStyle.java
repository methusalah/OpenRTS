package model.battlefield.map;

import java.util.ArrayList;
import java.util.List;

import model.builders.entity.CliffShapeBuilder;

/**
 *
 */
public class MapStyle {
	public final String id;
    public final List<CliffShapeBuilder> cliffShapeBuilders;
    
    public final List<String> diffuses;
    public final List<String> normals;
    public final List<Double> scales;

    public final List<String> coverDiffuses;
    public final List<String> coverNormals;
    public final List<Double> coverScales;
    
    public final int width, height;
    
    public MapStyle(String id,
    		List<CliffShapeBuilder> cliffShapeBuilders,
    		List<String> diffuses,
    		List<String> normals,
    		List<Double> scales,
    		List<String> coverDiffuses,
    		List<String> coverNormals,
    		List<Double> coverScales,
    		int width, int height
    		){
    	this.id = id;
    	this.cliffShapeBuilders = cliffShapeBuilders;
    	this.diffuses = diffuses;
    	this.normals = normals;
    	this.scales = scales;
    	this.coverDiffuses = coverDiffuses;
    	this.coverNormals = coverNormals;
    	this.coverScales = coverScales;
    	this.width = width;
    	this.height = height;
    }
}
