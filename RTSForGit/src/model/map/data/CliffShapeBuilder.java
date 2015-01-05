/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.data;

import math.MyRandom;
import model.map.cliff.Cliff;
import ressources.definitions.BuilderLibrary;
import ressources.definitions.DefElement;
import ressources.definitions.Definition;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class CliffShapeBuilder {
    static final String NATURAL_FACE_LINK = "NaturalFaceLink";
    static final String MANMADE_FACE_LINK = "ManmadeFaceLink";
    static final String TRINKET_LIST = "TrinketList";
    static final String RAMP_TRINKET_LIST = "RampTrinketList";
    static final String EDITOR_ICON_PATH = "EditorIconPath";
    static final String LINK = "link";
    static final String PROB = "prob";

    Definition def;
    BuilderLibrary lib;
    
    public String editorIconPath = "textures/editor/defaultcliffshapeicon.png";
            
    public CliffShapeBuilder(Definition def, BuilderLibrary lib){
        this.def = def;
        this.lib = lib;
        for(DefElement de : def.elements)
            switch(de.name){
                case EDITOR_ICON_PATH : editorIconPath = de.getVal();
            }
    }
    
    public void build(Cliff cliff){
        cliff.tile.cliffShapeID = def.id;
        for(DefElement de : def.elements)
            switch(de.name){
                case NATURAL_FACE_LINK : cliff.face = lib.getNaturalFaceBuilder(de.getVal()).build(cliff); break;
                case MANMADE_FACE_LINK : cliff.face = lib.getManmadeFaceBuilder(de.getVal()).build(cliff); break;
                case TRINKET_LIST :
                    if(cliff.tile.ramp == null && MyRandom.next()<de.getDoubleVal(PROB))
                        cliff.trinkets.add(lib.getTrinketBuilder(de.getVal(LINK)).build(cliff));
                    break;
                case RAMP_TRINKET_LIST :
                    if(cliff.tile.ramp != null && MyRandom.next()<de.getDoubleVal(PROB))
                        cliff.trinkets.add(lib.getTrinketBuilder(de.getVal(LINK)).build(cliff));
                    break;
                case EDITOR_ICON_PATH : editorIconPath = de.getVal();
            }
    }
    
    public String getID(){
        return def.id;
    }

}
