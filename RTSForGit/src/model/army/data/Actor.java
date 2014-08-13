/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data;

import java.util.ArrayList;
import model.army.ArmyManager;

/**
 *
 * @author Benoît
 */
public abstract class Actor {
    protected static final String ON_MOVE = "onMove";
    protected static final String ON_WAIT = "onWait";
    protected static final String ON_ATTACK = "onAttack";
    protected static final String ON_DESTROYED = "onDestroyed";
    protected static final String ON_EXPLODED = "onExploded";
    protected static final String ON_ALL_TIME = "onAllTime";
    
    Actor parent;
    String trigger;
    ArrayList<Actor> children = new ArrayList<>();
    public boolean destroyed = false;
    
    ArmyManager armyManager;
    
    
    public Actor(String trigger, Actor parent){
        this.trigger = trigger;
        this.parent = parent;
    }
    
    public void onMove(){
        trigger(ON_MOVE);
    }
    
    public void onWait(){
        trigger(ON_WAIT);
    }
    
    private void trigger(String trigger){
        for(Actor a : children)
            if(a.trigger.equals(trigger))
                a.act();
            else
                a.interrupt();
    }
    
    public void act(){
        trigger(ON_ALL_TIME);
        armyManager.registerActor(this);
    }
    public void interrupt(){
        armyManager.deleteActor(this);
    }
    
    public void destroy(){
        destroyed = true;
        armyManager.deleteActor(this);
    }
    
    public Actor getParent(){
        return parent;
    }
}
