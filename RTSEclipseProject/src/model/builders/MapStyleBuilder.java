/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders;

import java.util.ArrayList;
import java.util.List;

import model.battlefield.map.Map;
import model.builders.definitions.BuilderLibrary;
import model.builders.definitions.DefElement;
import model.builders.definitions.Definition;

/**
 *
 * @author Benoît
 */
public class MapStyleBuilder extends Builder{
    static final String CLIFF_SHAPE_LINK = "CliffShapeLink";
    
    static final String GROUND_TEXTURE = "GroundTexture";
    static final String DIFFUSE = "diffuse";
    static final String NORMAL = "normal";
    static final String SCALE = "scale";

    static final String WIDTH = "Width";
    static final String HEIGHT = "Height";
    
    public int width = 64;
    public int height = 64;
    private List<String> diffuses= new ArrayList<>();
    private List<String> normals = new ArrayList<>();
    private List<Double> scales = new ArrayList<>();
    private List<String> cliffShapeBuildersID = new ArrayList<>();
    private List<CliffShapeBuilder> cliffShapeBuilders = new ArrayList<>();

    public MapStyleBuilder(Definition def, BuilderLibrary lib){
        super(def, lib);
        for(DefElement de : def.elements)
            switch(de.name){
	            case CLIFF_SHAPE_LINK : cliffShapeBuildersID.add(de.getVal()); break;
	            case WIDTH : width = de.getIntVal(); break;
	            case HEIGHT : height = de.getIntVal(); break;
                case GROUND_TEXTURE :
                    diffuses.add(de.getVal(DIFFUSE));
                    normals.add(de.getVal(NORMAL));
                    scales.add(de.getDoubleVal(SCALE));
                    break;
            }
    }
    
    public void build(Map map){
        map.mapStyleID = getId();
        map.style.cliffShapeBuilders = cliffShapeBuilders;
        map.style.textures = diffuses;
        map.style.normals = normals;
        map.style.scales = scales;
    }

    @Override
    public void readFinalizedLibrary() {
    	for(String s : cliffShapeBuildersID)
    		cliffShapeBuilders.add(lib.getCliffShapeBuilder(s));
    }
}
