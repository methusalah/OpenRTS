/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data.actors;

import geometry3D.Point3D;
import java.util.HashMap;
import model.army.data.Actor;
import view.renderers.ModelActorViewElement;

/**
 *
 * @author Benoît
 */
public class ModelActor extends Actor {
    public ModelActorViewElement viewElement;
    public String modelPath;
    public double scale;
    
    public HashMap<String, Point3D> boneCoords = new HashMap<>();
    
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
    
    
}
