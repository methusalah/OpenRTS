/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.data;

import collections.Ring;
import geometry3D.Point3D;
import java.awt.Color;
import java.util.ArrayList;
import model.army.data.actors.ParticleActor;
import model.map.cliff.Cliff;
import static model.map.cliff.Cliff.Type.Corner;
import model.map.cliff.faces.natural.Dug1Corner;
import model.map.cliff.faces.Face;
import model.map.cliff.faces.natural.NaturalFace;
import model.map.cliff.faces.natural.Dug1Ortho;
import model.map.cliff.faces.natural.Dug1Salient;
import ressources.definitions.BuilderLibrary;
import ressources.definitions.DefElement;
import ressources.definitions.Definition;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class NaturalFaceBuilder {
    static final String STYLE = "Style";
    static final String COLOR = "Color";
    static final String TEXTURE_PATH = "TexturePath";
    static final String NOISE = "Noise";
    static final String NOISE_X = "NoiseX";
    static final String NOISE_Y = "NoiseY";
    static final String NOISE_Z = "NoiseZ";
    static final String RIDGE_DEPTH = "RidgeDepth";
    static final String RIDGE_POSITION = "RidgePosition";

    static final String RED = "R";
    static final String GREEN = "G";
    static final String BLUE = "B";

    static final String STYLE_DUG_1 = "dug1";
    

    Definition def;
    BuilderLibrary lib;
    
    double noiseX, noiseY, noiseZ;
    double ridgeDepth, ridgePos;
    Color color;
    String texturePath;
    String style;

    public NaturalFaceBuilder(Definition def){
        this.def = def;
        this.lib = lib;
    }
    
    public NaturalFace build(Cliff cliff){
        for(DefElement de : def.elements)
            switch(de.name){
                case NOISE :
                    if(!isValidRange(de.getDoubleVal()))
                        break;
                    else
                        noiseX = noiseY = noiseZ = de.getDoubleVal();
                    break;
                case NOISE_X :
                    if(!isValidRange(de.getDoubleVal()))
                        break;
                    else
                        noiseX = de.getDoubleVal();
                    break;
                case NOISE_Y :
                    if(!isValidRange(de.getDoubleVal()))
                        break;
                    else
                        noiseY = de.getDoubleVal();
                    break;
                case NOISE_Z :
                    if(!isValidRange(de.getDoubleVal()))
                        break;
                    else
                        noiseZ = de.getDoubleVal();
                    break;
                case RIDGE_DEPTH :
                    if(!isValidRange(de.getDoubleVal()))
                        break;
                    else
                        ridgeDepth = de.getDoubleVal();
                    break;
                case RIDGE_POSITION :
                    if(!isValidRange(de.getDoubleVal()))
                        break;
                    else
                        ridgePos = de.getDoubleVal();
                    break;
                case STYLE : style = de.getVal(); break;
                case COLOR :
                    color = new Color(de.getIntVal(RED),
                            de.getIntVal(GREEN),
                            de.getIntVal(BLUE));
                    break;
                case TEXTURE_PATH : texturePath = de.getVal(); break;

            }
        if(color == null && texturePath == null){
            LogUtil.logger.warning("Natural face has no specified color or texture. Applying default color");
            color = Color.ORANGE;
        }

        NaturalFace prototype = new NaturalFace(cliff, noiseX, noiseY, noiseZ, ridgeDepth, ridgePos, color, texturePath);
        switch (style){
            case STYLE_DUG_1 : switch (cliff.type){
                case Corner : return new Dug1Corner(prototype);
                case Salient : return new Dug1Salient(prototype);
                case Orthogonal : return new Dug1Ortho(prototype);
                    default:return null;
            }
        }
        return null;
    }
        
    public boolean isValidRange(double val){
        if(val>1 || val<0){
            LogUtil.logger.warning("Range value ("+val+") incorrect for "+def.id+". Must be between 0 an 1.");
            return false;
        } else return true;

    }
    
}
