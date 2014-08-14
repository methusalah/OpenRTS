/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data;

import java.util.ArrayList;
import model.army.ArmyManager;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class Actor {
    protected static final String ON_MOVE = "onMove";
    protected static final String ON_WAIT = "onWait";
    protected static final String ON_AIM = "onAim";
    protected static final String ON_SHOOT = "onShoot";
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
    public void onShoot(){
        trigger(ON_SHOOT);
    }
    public void onAim(){
        trigger(ON_AIM);
    }
    
    private void trigger(String trigger){
        if(this.trigger.equals(trigger))
            act();
        for(Actor a : children)
            a.trigger(trigger);
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
    
    public boolean containsModel(){
        return false;
    }
}
