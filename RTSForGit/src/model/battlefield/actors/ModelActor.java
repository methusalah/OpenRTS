/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.actors;

import geometry3D.Point3D;
import java.util.HashMap;
import java.util.List;
import model.battlefield.army.ArmyManager;
import model.builders.actors.ActorBuilder;
import view.actorDrawing.ActorViewElements;

/**
 *
 * @author Benoît
 */
public class ModelActor extends Actor {
    public final String modelPath;
    public final double scale;
    
    private HashMap<String, Point3D> boneCoords = new HashMap<>();

    public ModelActor(Actor parent,
            String trigger,
            List<String> childrenTriggers,
            List<ActorBuilder> childrenBuilders,
            ArmyManager armyManager,
            String modelPath,
            double scale) {
        super(parent, trigger, childrenTriggers, childrenBuilders, armyManager);
        this.modelPath = modelPath;
        this.scale = scale;
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
    
    
    
    
}
