/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders;

import java.util.ArrayList;
import java.util.List;
import math.MyRandom;
import model.battlefield.map.cliff.Cliff;
import ressources.definitions.BuilderLibrary;
import ressources.definitions.DefElement;
import ressources.definitions.Definition;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class CliffShapeBuilder extends Builder{
    private static final String NATURAL_FACE_LINK = "NaturalFaceLink";
    private static final String MANMADE_FACE_LINK = "ManmadeFaceLink";
    private static final String TRINKET_LIST = "TrinketList";
    private static final String RAMP_TRINKET_LIST = "RampTrinketList";
    private static final String EDITOR_ICON_PATH = "EditorIconPath";
    private static final String LINK = "link";
    private static final String PROB = "prob";

    private NaturalFaceBuilder naturalFaceBuilder = null;
    private String naturalFaceBuilderID;
    private ManmadeFaceBuilder manmadeFaceBuilder = null;
    private String manmadeFaceBuilderID;
    private List<TrinketBuilder> trinketBuilders = new ArrayList<>();
    private List<String> trinketBuildersID = new ArrayList<>();
    private List<Double> trinketProbs = new ArrayList<>();
    private List<TrinketBuilder> rampTrinketBuilders = new ArrayList<>();
    private List<String> rampTrinketBuildersID = new ArrayList<>();
    private List<Double> rampTrinketProbs = new ArrayList<>();
    private String editorIconPath = "textures/editor/defaultcliffshapeicon.png";
            
    public CliffShapeBuilder(Definition def, BuilderLibrary lib){
        super(def, lib);
        for(DefElement de : def.elements)
            switch(de.name){
                case NATURAL_FACE_LINK : naturalFaceBuilderID = de.getVal(); break;
                case MANMADE_FACE_LINK : manmadeFaceBuilderID = de.getVal(); break;
                case TRINKET_LIST :
                    trinketBuildersID.add(de.getVal(LINK));
                    trinketProbs.add(de.getDoubleVal(PROB));
                    break;
                case RAMP_TRINKET_LIST :
                    rampTrinketBuildersID.add(de.getVal(LINK));
                    rampTrinketProbs.add(de.getDoubleVal(PROB));
                    break;
                case EDITOR_ICON_PATH : editorIconPath = de.getVal();
            }
    }
    
    public void build(Cliff cliff){
        cliff.tile.cliffShapeID = def.id;
        if(naturalFaceBuilder != null)
            cliff.face = naturalFaceBuilder.build(cliff);
        else
            cliff.face = manmadeFaceBuilder.build(cliff);
        int i = 0;
        cliff.trinkets.clear();
        if(cliff.tile.ramp == null){
            for(TrinketBuilder tb : trinketBuilders)
                if(MyRandom.next()<trinketProbs.get(i))
                    cliff.trinkets.add(tb.build(cliff));
            i++;
        } else {
            for(TrinketBuilder tb : rampTrinketBuilders)
                if(MyRandom.next()<rampTrinketProbs.get(i))
                    cliff.trinkets.add(tb.build(cliff));
            i++;
        }
    }
    
    public String getID(){
        return def.id;
    }
    
    public String getIconPath(){
        return editorIconPath;
    }

    @Override
    public void readFinalizedLibrary() {
        naturalFaceBuilder = lib.getNaturalFaceBuilder(naturalFaceBuilderID);
        manmadeFaceBuilder = lib.getManmadeFaceBuilder(manmadeFaceBuilderID);
        for(String s : trinketBuildersID)
            trinketBuilders.add(lib.getTrinketBuilder(s));
        for(String s : rampTrinketBuildersID)
            rampTrinketBuilders.add(lib.getTrinketBuilder(s));
    }
}
