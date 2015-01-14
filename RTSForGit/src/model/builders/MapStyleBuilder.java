/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders;

import model.battlefield.map.Map;
import ressources.definitions.BuilderLibrary;
import ressources.definitions.DefElement;
import ressources.definitions.Definition;

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

    public MapStyleBuilder(Definition def, BuilderLibrary lib){
        super(def, lib);
    }
    
    public void build(Map map){
        map.mapStyleID = def.id;
        for(DefElement de : def.elements)
            switch(de.name){
                case CLIFF_SHAPE_LINK : map.style.cliffShapes.add(lib.getCliffShapeBuilder(de.getVal())); break;
                case GROUND_TEXTURE :
                    map.style.textures.add(de.getVal(DIFFUSE));
                    map.style.normals.add(de.getVal(NORMAL));
                    map.style.scales.add(de.getDoubleVal(SCALE));
                    break;
            }
    }

    @Override
    public void readFinalizedLibrary() {
    }
}
