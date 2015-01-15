/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.map;

import geometry3D.Point3D;
import java.awt.Color;
import model.battlefield.abstractComps.FieldComp;
import model.battlefield.actors.ModelActor;
import model.builders.actors.ModelActorBuilder;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class Trinket extends FieldComp{
    public final boolean editable;
    private ModelActor actor;
    
    public final String label = "label"+this.toString();

    public Trinket(boolean editable, String modelPath, Point3D pos, double scaleX, double scaleY, double scaleZ, double rotX, double rotY, double rotZ, Color color, ModelActorBuilder actorBuilder) {
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
        actor = actorBuilder.build(this);
    }
    
    public void removeFromBattlefield(){
    	actor.stopActingAndChildren();
    }
}
