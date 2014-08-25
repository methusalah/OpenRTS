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
    public enum Facing{Horizontal, Velocity, Camera}
    
    public String spritePath;
    public int nbCol = 1;
    public int nbRow = 1;
    public String emissionNode;
    public String directionNode;
    
    public double velocity = 0;
    public double fanning = 0;
    public boolean randomSprite;
    public int maxCount;
    public int perSecond;
    public double duration = Double.MAX_VALUE;
    public double startSize;
    public double endSize;
    public Color startColor;
    public Color endColor;
    public double minLife;
    public double maxLife;
    public double spinSpeed;
    public boolean randomAngle;
    public double rotationSpeed;
    public boolean gravity = false;
    public double emissionPointVariation;
    public Facing facing = Facing.Camera;
    public boolean add = true;
    public double startVariation = 0;
    
    public boolean launched = false;
    public long startTime = 0;
    
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
        startTime = 0;
        super.act();
    }
    
    


}
