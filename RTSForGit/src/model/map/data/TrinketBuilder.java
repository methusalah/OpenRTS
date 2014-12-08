/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.data;

import geometry.Point2D;
import geometry3D.Point3D;
import java.awt.Color;
import java.util.ArrayList;
import math.Angle;
import math.MyRandom;
import model.map.cliff.Cliff;
import static model.map.cliff.Cliff.Type.Orthogonal;
import model.map.cliff.Trinket;
import static model.map.data.NaturalFaceBuilder.COLOR;
import ressources.definitions.BuilderLibrary;
import ressources.definitions.DefElement;
import ressources.definitions.Definition;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class TrinketBuilder {
    static final String MODEL_LIST = "ModelList";
    static final String COLOR = "Color";

    static final String ROTATION_X = "RotationX";
    static final String ROTATION_Y = "RotationY";
    static final String ROTATION_Z = "RotationZ";

    static final String POSITION_X = "PositionX";
    static final String POSITION_Y = "PositionY";
    static final String POSITION_Z = "PositionZ";
    
    static final String SCALE_X = "ScaleX";
    static final String SCALE_Y = "ScaleY";
    static final String SCALE_Z = "ScaleZ";

    static final String MIN = "min";
    static final String MAX = "max";
 
    static final String RED = "R";
    static final String GREEN = "G";
    static final String BLUE = "B";

    

    Definition def;
    BuilderLibrary lib;
            
    public TrinketBuilder(Definition def){
        this.def = def;
    }
    
    public Trinket build(Cliff cliff){
        ArrayList<String> models = new ArrayList<>();
        double rotX=0, rotY=0, rotZ=0, posX=0, posY=0, posZ=0, scaleX=0, scaleY=0, scaleZ=0;
        Color color = null;
        for(DefElement de : def.elements)
            switch(de.name){
                case MODEL_LIST : models.add(de.getVal()); break;
                case ROTATION_X : rotX = Angle.toRadians(MyRandom.between(de.getDoubleVal(MIN), de.getDoubleVal(MAX))); break;
                case ROTATION_Y : rotY = Angle.toRadians(MyRandom.between(de.getDoubleVal(MIN), de.getDoubleVal(MAX))); break;
                case ROTATION_Z : rotZ = Angle.toRadians(MyRandom.between(de.getDoubleVal(MIN), de.getDoubleVal(MAX))); break;

                case POSITION_X : posX = MyRandom.between(de.getDoubleVal(MIN), de.getDoubleVal(MAX)); break;
                case POSITION_Y : posY = MyRandom.between(de.getDoubleVal(MIN), de.getDoubleVal(MAX)); break;
                case POSITION_Z : posZ = MyRandom.between(de.getDoubleVal(MIN), de.getDoubleVal(MAX)); break;

                case SCALE_X : scaleX = MyRandom.between(de.getDoubleVal(MIN), de.getDoubleVal(MAX)); break;
                case SCALE_Y : scaleY = MyRandom.between(de.getDoubleVal(MIN), de.getDoubleVal(MAX)); break;
                case SCALE_Z : scaleZ = MyRandom.between(de.getDoubleVal(MIN), de.getDoubleVal(MAX)); break;
                case COLOR :
                    color = new Color(de.getIntVal(RED),
                            de.getIntVal(GREEN),
                            de.getIntVal(BLUE));
                    break;
                    
            }

        Point3D pos = new Point3D(posX, 0, posZ);
        switch (cliff.type){
            case Orthogonal :
                pos = pos.getAddition(0, posY, 0);
                break;
            case Salient :
                pos = pos.getRotationAroundZ(Angle.RIGHT*posY);
            break;
            case Corner :
                pos = new Point3D(1-pos.x, pos.y, pos.z);
                pos = pos.getRotationAroundZ(Angle.RIGHT*posY);
                break;
        }
        pos = pos.getRotationAroundZ(cliff.angle, new Point2D(0.5, 0.5));

        
        int index = 0;
        if(models.size()>1)
            index = MyRandom.nextInt(models.size()-1);
        Trinket res = new Trinket(models.get(index));
        res.pos = pos;
        res.rotX = rotX;
        res.rotY = rotY;
        res.rotZ = rotZ;
        res.scaleX = scaleX;
        res.scaleY = scaleY;
        res.scaleZ = scaleZ;
        res.color = color;
        return res;
    }

}
