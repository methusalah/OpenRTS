/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.map;

import geometry3D.Point3D;
import java.awt.Color;
import model.battlefield.abstractComps.FieldComp;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 * @author Benoît
 */
@Root
public class Trinket extends FieldComp{
    @Element
    public final boolean editable;
    @Element
    public final String modelPath;
    @Element
    public final double scaleX;
    @Element
    public final double scaleY;
    @Element
    public final double scaleZ;
    @Element
    public final int colorSerial;

    public final Color color;
    public final String label = "label"+this.toString();

    public Trinket(boolean editable, String modelPath, Point3D pos, double scaleX, double scaleY, double scaleZ, double rotX, double rotY, double rotZ, Color color) {
        super(pos, rotZ);
        this.editable = editable;
        this.modelPath = modelPath;
        this.pos = pos;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        this.roll = rotX;
        this.pitch = rotY;
        this.color = color;
        if(color != null)
            colorSerial = color.getRGB();
        else
            colorSerial = 0;
    }
    
    public Trinket(@Element(name="editable")boolean editable,
            @Element(name="modelPath")String modelPath,
            @Element(name="pos")Point3D pos,
            @Element(name="scaleX")double scaleX,
            @Element(name="scaleY")double scaleY,
            @Element(name="scaleZ")double scaleZ,
            @Element(name="rotX")double rotX,
            @Element(name="rotY")double rotY,
            @Element(name="rotZ")double rotZ,
            @Element(name="colorSerial")int colorSerial) {
        super(pos, rotZ);
        this.editable = editable;
        this.modelPath = modelPath;
        this.pos = pos;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        this.roll = rotX;
        this.pitch = rotY;
        this.colorSerial = colorSerial;
        if(colorSerial == 0)
            color = null;
        else 
            color = new Color(colorSerial, true);
    }
    

}
