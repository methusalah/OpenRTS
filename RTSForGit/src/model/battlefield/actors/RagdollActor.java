/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.actors;

import geometry3D.Point3D;
import model.battlefield.army.components.Movable;

/**
 *
 * @author Benoît
 */
public class RagdollActor extends ModelActor {
    public double startLife = 1;
    public double life;
    public double mass = 1;
    public String massCenterBone;
    public long timer;
    
    public boolean launched = false;
    
    public RagdollActor(String trigger, Actor parent){
        super(trigger, parent);
    }
    
    @Override
    public String getType() {
        return "physic";
    }
    
    public ModelActor getParentModelActor(){
        Actor parent = this;
        do {
            parent = parent.getParent();
            if(parent == null)
                throw new RuntimeException(this.getClass().getSimpleName()+" seems to miss a modelActor parent");
            
        } while(!parent.containsModel());
        return (ModelActor)parent;
    }
    
    public void renderingDone(){
        interrupt();
    }
    
    public boolean alive(){
        return life > 0;
    }

    
    
    
}
