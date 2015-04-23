package model.battlefield.map;

import geometry.geom3d.Point3D;
import model.builders.definitions.BuilderLibrary;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class SerializableTrinket {
    @Element
    private final String builderID;
    @Element
    private Point3D pos;
    @Element
    private double yaw;
    @Element
    private double scaleX, scaleY, scaleZ;
    @Element
    private String modelPath;
    
    public SerializableTrinket(Trinket t) {
        builderID = t.builderID;
        pos = t.pos;
        yaw = t.yaw;
        scaleX = t.scaleX;
        scaleY = t.scaleY;
        scaleZ = t.scaleZ;
        modelPath = t.modelPath;
    }
    public SerializableTrinket(@Element(name="builderID")String builderID,
            @Element(name="pos")Point3D pos,
            @Element(name="yaw")double yaw,
            @Element(name="scaleX")double scaleX,
            @Element(name="scaleY")double scaleY,
            @Element(name="scaleZ")double scaleZ,
            @Element(name="modelPath")String modelPath) {
    	this.builderID = builderID;
    	this.pos = pos;
        this.yaw = yaw;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        this.modelPath = modelPath;
    }
    
    public Trinket getTrinket(BuilderLibrary lib){
    	Trinket res = lib.getTrinketBuilder(builderID).build(pos, yaw, modelPath, scaleX, scaleY, scaleZ);
        return res;
        
    }

}
