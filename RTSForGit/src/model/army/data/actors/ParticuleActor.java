/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data.actors;

import geometry3D.Point3D;
import model.army.data.Actor;

/**
 *
 * @author Benoît
 */
public class ParticuleActor extends Actor {
    String spritePath;
    int nbCol;
    int nbRow;
    String emmissionNode;
    
    boolean randomSprite;
    int maxCount;
    int perSecond;
    double startSize;
    double endSize;
    double startColor;
    double endColor;
    Point3D velocity;
    double fanning;
    double minLife;
    double maxLife;
    double spinSpeed;
    boolean randomAngle;
    boolean gravity;
    double emissionPointVariation;
    
    public ParticuleActor(String trigger, Actor parent){
        super(trigger, parent);
    }
}
