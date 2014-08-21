/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data.actors;

import geometry3D.Point3D;
import java.util.HashMap;
import model.army.data.Actor;
import view.renderers.ActorViewElements;

/**
 *
 * @author Benoît
 */
public class ModelActor extends Actor {
    public String modelPath;
    public double scale;
    
    private HashMap<String, Point3D> boneCoords = new HashMap<>();
    
    public ModelActor(String trigger, Actor parent){
        super(trigger, parent);
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
    
    
}
