/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.data;

import ressources.definitions.BuilderLibrary;
import ressources.definitions.DefElement;
import ressources.definitions.Definition;

/**
 *
 * @author Benoît
 */
public class MapStyleBuilder {
    static final String CLIFF_SHAPE_LINK = "CliffShapeLink";
    
    static final String GROUND_TEXTURE = "GroundTexture";
    static final String DIFFUSE = "diffuse";
    static final String NORMAL = "normal";
    static final String SCALE = "scale";

    Definition def;
    BuilderLibrary lib;
            
    public MapStyleBuilder(Definition def, BuilderLibrary lib){
        this.def = def;
        this.lib = lib;
    }
    
    public MapStyle build(){
        MapStyle res = new MapStyle();
        for(DefElement de : def.elements)
            switch(de.name){
                case CLIFF_SHAPE_LINK : res.cliffShapes.add(lib.getCliffShapeBuilder(de.getVal())); break;
                case GROUND_TEXTURE :
                    res.textures.add(de.getVal(DIFFUSE));
                    res.normals.add(de.getVal(NORMAL));
                    res.scales.add(de.getDoubleVal(SCALE));
                    break;
            }
        return res;
    }
}
