/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders;

import geometry.Point2D;
import geometry3D.Point3D;
import java.awt.Color;
import java.util.ArrayList;
import math.Angle;
import math.MyRandom;
import model.battlefield.map.Tile;
import model.battlefield.map.cliff.Cliff;
import static model.battlefield.map.cliff.Cliff.Type.Corner;
import static model.battlefield.map.cliff.Cliff.Type.Orthogonal;
import static model.battlefield.map.cliff.Cliff.Type.Salient;
import model.battlefield.map.Trinket;
import static model.builders.NaturalFaceBuilder.COLOR;
import ressources.definitions.BuilderLibrary;
import ressources.definitions.DefElement;
import ressources.definitions.Definition;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class TrinketBuilder {
    static final String EDITABLE = "Editable";
    static final String MODEL_LIST = "ModelList";
    static final String COLOR = "Color";

    static final String ROTATION_X = "RotationX";
    static final String ROTATION_Y = "RotationY";
    static final String ROTATION_Z = "RotationZ";

    static final String POSITION_X = "PositionX";
    static final String POSITION_Y = "PositionY";
    static final String POSITION_Z = "PositionZ";
    
    static final String SCALE = "Scale";
    static final String SCALE_X = "ScaleX";
    static final String SCALE_Y = "ScaleY";
    static final String SCALE_Z = "ScaleZ";

    static final String MIN = "min";
    static final String MAX = "max";
 
    static final String RED = "R";
    static final String GREEN = "G";
    static final String BLUE = "B";

    public String id;
    public boolean editable = true;

    Definition def;
    BuilderLibrary lib;
            
    public TrinketBuilder(Definition def){
        this.def = def;
        id = def.id;
        for(DefElement de : def.elements)
            if(de.name.equals(EDITABLE))
                editable = de.getBoolVal();
    }
    
    public Trinket build(Point3D position){
        Trinket res = new Trinket();
        ArrayList<String> models = new ArrayList<>();
        double posX=0, posY=0, posZ=0;
        for(DefElement de : def.elements)
            switch(de.name){
                case EDITABLE : res.editable = de.getBoolVal(); break;
                case MODEL_LIST : models.add(de.getVal()); break;
                case ROTATION_X : res.rotX = Angle.toRadians(MyRandom.between(de.getDoubleVal(MIN), de.getDoubleVal(MAX))); break;
                case ROTATION_Y : res.rotY = Angle.toRadians(MyRandom.between(de.getDoubleVal(MIN), de.getDoubleVal(MAX))); break;
                case ROTATION_Z : res.rotZ = Angle.toRadians(MyRandom.between(de.getDoubleVal(MIN), de.getDoubleVal(MAX))); break;

                case POSITION_X : posX = MyRandom.between(de.getDoubleVal(MIN), de.getDoubleVal(MAX)); break;
                case POSITION_Y : posY = MyRandom.between(de.getDoubleVal(MIN), de.getDoubleVal(MAX)); break;
                case POSITION_Z : posZ = MyRandom.between(de.getDoubleVal(MIN), de.getDoubleVal(MAX)); break;

                case SCALE : res.scaleX = res.scaleY = res.scaleZ = MyRandom.between(de.getDoubleVal(MIN), de.getDoubleVal(MAX)); break;
                case SCALE_X : res.scaleX = MyRandom.between(de.getDoubleVal(MIN), de.getDoubleVal(MAX)); break;
                case SCALE_Y : res.scaleY = MyRandom.between(de.getDoubleVal(MIN), de.getDoubleVal(MAX)); break;
                case SCALE_Z : res.scaleZ = MyRandom.between(de.getDoubleVal(MIN), de.getDoubleVal(MAX)); break;
                case COLOR :
                    res.color = new Color(de.getIntVal(RED),
                            de.getIntVal(GREEN),
                            de.getIntVal(BLUE));
                    break;
            }
        
        res.pos = new Point3D(posX, posY, posZ).getAddition(position);
        int index = 0;
        if(models.size()>1)
            index = MyRandom.nextInt(models.size()-1);
        res.modelPath = models.get(index);
        return res;
    }
    
    public Trinket build(Cliff cliff){
        Trinket res = build(Point3D.ORIGIN);
        double posY = res.pos.y;
        res.pos.y = 0;
        switch (cliff.type){
            case Orthogonal :
                res.pos = res.pos.getAddition(0, posY, 0);
                break;
            case Salient :
                res.pos = res.pos.getRotationAroundZ(Angle.RIGHT*posY);
                break;
            case Corner :
                res.pos = new Point3D(1-res.pos.x, res.pos.y, res.pos.z);
                res.pos = res.pos.getRotationAroundZ(Angle.RIGHT*posY);
                break;
        }
        if(cliff.tile.ramp != null){
            double ratio = cliff.tile.ramp.getCliffSlopeRate(cliff.tile);
            res.pos = res.pos.getAddition(0, 0, -Tile.STAGE_HEIGHT*ratio*res.pos.z/Tile.STAGE_HEIGHT);
        }
        res.pos = res.pos.getRotationAroundZ(cliff.angle, new Point2D(0.5, 0.5));
        res.pos = res.pos.getAddition(cliff.tile.x, cliff.tile.y, cliff.tile.level*Tile.STAGE_HEIGHT);
        return res;
    }

}
