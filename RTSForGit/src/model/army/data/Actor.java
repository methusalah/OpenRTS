/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data;

import java.util.ArrayList;
import model.army.ArmyManager;
import tools.LogUtil;
import view.renderers.ActorViewElements;

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
    
    public ActorViewElements viewElements = new ActorViewElements();
    
    ArmyManager armyManager;
    
    
    public Actor(String trigger, Actor parent){
        this.trigger = trigger;
        this.parent = parent;
    }
    
    public void onMove(){
        activate(ON_MOVE);
        desactivate(ON_WAIT);
        desactivate(ON_AIM);
    }
    public void onWait(){
        activate(ON_WAIT);
        desactivate(ON_MOVE);
        desactivate(ON_AIM);
    }
    public void onAim(){
        activate(ON_AIM);
        desactivate(ON_WAIT);
        desactivate(ON_MOVE);
        
    }
    public void onShoot(){
        activate(ON_SHOOT);
    }
    
    private void activate(String trigger){
        if(this.trigger.equals(trigger))
            act();
        for(Actor a : children)
            a.activate(trigger);
    }
    
    private void desactivate(String trigger){
        if(this.trigger.equals(trigger))
            interrupt();
        for(Actor a : children)
            a.desactivate(trigger);
    }
    
    protected void act(){
        armyManager.registerActor(this);
    }
    
    public void interrupt(){
        armyManager.deleteActor(this);
    }
    
    public Actor getParent(){
        return parent;
    }
    
    public boolean containsModel(){
        return false;
    }
}
