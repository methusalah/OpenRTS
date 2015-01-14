/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.actors;

import geometry3D.Point3D;
import java.util.HashMap;
import java.util.List;
import model.battlefield.Battlefield;
import model.battlefield.abstractComps.FieldComp;
import model.battlefield.army.ArmyManager;
import model.builders.actors.ActorBuilder;
import view.actorDrawing.ActorViewElements;

/**
 *
 * @author Benoît
 */
public class ModelActor extends Actor {
    public final String modelPath;
    public final double scaleX;
    public final double scaleY;
    public final double scaleZ;
    final FieldComp comp;

    private HashMap<String, Point3D> boneCoords = new HashMap<>();

    public ModelActor(Actor parent,
            String trigger,
            List<String> childrenTriggers,
            List<ActorBuilder> childrenBuilders,
            ActorPool pool,
            String modelPath,
            double scale,
            FieldComp comp) {
        super(parent, trigger, childrenTriggers, childrenBuilders, pool);
        this.modelPath = modelPath;
        this.scaleX = scale;
        this.scaleY = scale;
        this.scaleZ = scale;
        this.comp = comp;
    }
    
    
    public String getLabel(){
        return "";
    }

    @Override
    public boolean containsModel() {
        return true;
    }
    
    public Point3D getBoneCoord(String boneName){
        Point3D res = boneCoords.get(boneName);
        if(res == null)
            throw new IllegalArgumentException("Can't find bone "+boneName);
        return res;
    }
    
    public void setBone(String name, Point3D coord){
        boneCoords.put(name, coord);
    }
    
    public boolean hasBone(){
        return !boneCoords.isEmpty();
    }

    @Override
    public String getType() {
        return "model";
    }
    
    public FieldComp getComp(){
        return comp;
    }
    
    public Point3D getPos(){
        return comp.getPos();
    }
    
    public double getYaw(){
        return comp.getYaw();
    }
    
    public Point3D getDirection(){
        return comp.direction;
    }
}
