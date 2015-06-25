/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders.entity;

import java.util.ArrayList;
import java.util.List;

import model.battlefield.map.Map;
import model.battlefield.map.MapStyle;
import model.builders.entity.definitions.BuilderManager;
import model.builders.entity.definitions.DefElement;
import model.builders.entity.definitions.Definition;

/**
 *
 * @author Benoît
 */
public class MapStyleBuilder extends Builder{
	static final String CLIFF_SHAPE_LINK = "CliffShapeLink";

	static final String GROUND_TEXTURE = "GroundTexture";
	static final String COVER_TEXTURE = "CoverTexture";
	static final String DIFFUSE = "diffuse";
	static final String NORMAL = "normal";
	static final String SCALE = "scale";

	static final String WIDTH = "Width";
	static final String HEIGHT = "Height";

	public int width = 64;
	public int height = 64;
	private List<String> diffuses = new ArrayList<>();
	private List<String> normals = new ArrayList<>();
	private List<Double> scales = new ArrayList<>();

	private List<String> coverDiffuses= new ArrayList<>();
	private List<String> coverNormals = new ArrayList<>();
	private List<Double> coverScales = new ArrayList<>();

	private List<String> cliffShapeBuildersID = new ArrayList<>();
	private List<CliffShapeBuilder> cliffShapeBuilders = new ArrayList<>();

	public MapStyleBuilder(Definition def) {
		super(def);
		coverDiffuses.add("textures/transp.png");
		coverNormals.add(null);
		coverScales.add(1d);
		for (DefElement de : def.getElements()) {
			switch(de.name){
				case CLIFF_SHAPE_LINK : cliffShapeBuildersID.add(de.getVal()); break;
				case WIDTH : width = de.getIntVal(); break;
				case HEIGHT : height = de.getIntVal(); break;
				case GROUND_TEXTURE :
					diffuses.add(de.getVal(DIFFUSE));
					normals.add(de.getVal(NORMAL));
					scales.add(de.getDoubleVal(SCALE));
					break;
				case COVER_TEXTURE :
					coverDiffuses.add(de.getVal(DIFFUSE));
					coverNormals.add(de.getVal(NORMAL));
					coverScales.add(de.getDoubleVal(SCALE));
					break;
			}
		}
	}

	public MapStyle build(){
		return new MapStyle(getId(),
				cliffShapeBuilders,
				diffuses,
				normals,
				scales,
				coverDiffuses,
				coverNormals,
				coverScales,
				width,
				height);
	}

	@Override
	public void readFinalizedLibrary() {
		for(String s : cliffShapeBuildersID) {
			cliffShapeBuilders.add(BuilderManager.getCliffShapeBuilder(s));
		}
	}
}
