/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data.actors;

import model.army.data.Actor;

/**
 *
 * @author Benoît
 */
public class AnimationActor extends Actor {
    public enum Cycle{Once, Loop, Cycle};
    
    public String animName;
    public Cycle cycle;
    public double speed;
    
    public boolean launched = false;
    public AnimationActor(String trigger, Actor parent){
        super(trigger, parent);
    }

    @Override
    public void interrupt() {
        super.interrupt();
        launched = false;
    }
    
    
}
