/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data.actors;

import geometry3D.Point3D;
import java.awt.Color;
import model.army.data.Actor;

/**
 *
 * @author Benoît
 */
public class ParticleActor extends Actor {
    public String spritePath;
    public int nbCol;
    public int nbRow;
    public String emissionNode;
    public String directionNode;
    
    public boolean randomSprite;
    public int maxCount;
    public int perSecond;
    public boolean emitAll;
    public double startSize;
    public double endSize;
    public Color startColor;
    public Color endColor;
    public Point3D velocity;
    public double fanning;
    public double minLife;
    public double maxLife;
    public double spinSpeed;
    public boolean randomAngle;
    public boolean gravity;
    public double emissionPointVariation;
    
    public boolean launched = false;
    
    public ParticleActor(String trigger, Actor parent){
        super(trigger, parent);
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

    @Override
    protected void act() {
        launched = false;
        super.act();
    }
    
    


}
