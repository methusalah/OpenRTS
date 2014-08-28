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
    public String id;
    
    public ActorViewElements viewElements = new ActorViewElements();
    boolean acting = false;
    boolean destroyed = false;
    
    ArmyManager armyManager;
    
    
    public Actor(String trigger, Actor parent){
        this.trigger = trigger;
        this.parent = parent;
    }
    
    public void onMove(boolean cond){
        if(cond)
            activate(ON_MOVE);
        else
            desactivate(ON_MOVE);
    }
    public void onWait(boolean cond){
        if(cond)
            activate(ON_WAIT);
        else
            desactivate(ON_WAIT);
    }
    public void onAim(boolean cond){
        if(cond)
            activate(ON_AIM);
        else
            desactivate(ON_AIM);
    }
    public void onShootEvent(){
        activate(ON_SHOOT);
    }
    public void onDestroyedEvent(){
        activate(ON_DESTROYED);
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
        if(acting)
            return;
        acting = true;
        armyManager.registerActor(this);
    }
    
    protected void interrupt(){
        acting = false;
        armyManager.deleteActor(this);
        for(Actor a : children)
            a.interrupt();
    }
    
    public Actor getParent(){
        return parent;
    }
    
    public boolean containsModel(){
        return false;
    }
    
    public void destroyAfterActing(){
        acting = false;
        destroyed = true;
    }
    
    public boolean isDestroyed(){
        return destroyed;
    }
    
    public boolean isActing(){
        if(acting)
            return true;
        for(Actor a : children)
            if(a.isActing())
                return true;
        return false;
    }
}
