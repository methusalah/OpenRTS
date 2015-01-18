/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.actors;

import geometry3D.Point3D;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;

import tools.LogUtil;
import model.battlefield.abstractComps.FieldComp;
import model.builders.actors.ActorBuilder;

/**
 *
 * @author Benoît
 */
public class ModelActor extends Actor {
    public final String modelPath;
    public final double scaleX;
    public final double scaleY;
    public final double scaleZ;
    public final Color color;
    final FieldComp comp;

    private HashMap<String, Point3D> boneCoords = new HashMap<>();

    public ModelActor(Actor parent,
            String trigger,
            List<String> childrenTriggers,
            List<ActorBuilder> childrenBuilders,
            ActorPool pool,
            String modelPath,
            double scaleX,
            double scaleY,
            double scaleZ,
            Color color,
            FieldComp comp) {
        super(parent, trigger, childrenTriggers, childrenBuilders, pool);
        this.modelPath = modelPath;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        this.color = color;
        this.comp = comp;
        act();
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
    public boolean hasBone(String boneName){
        return boneCoords.get(boneName) != null;
    }
    public void debbugWriteBoneNames(){
    	LogUtil.logger.info(""+boneCoords.keySet());
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
