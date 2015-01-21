/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.actors;

import java.util.ArrayList;
import java.util.List;

import model.builders.actors.ActorBuilder;
import view.actorDrawing.ActorViewElements;

/**
 *
 * @author Benoît
 */
public class Actor {
    public final static String TYPE = "Actor";
    
    protected static final String ON_MOVE = "onMove";
    protected static final String ON_WAIT = "onWait";
    protected static final String ON_AIM = "onAim";
    protected static final String ON_SHOOT = "onShoot";
    protected static final String ON_DESTROYED = "onDestroyed";
    protected static final String ON_EXPLODED = "onExploded";
    protected static final String ON_ALL_TIME = "onAllTime";
    
    public final Actor parent;
    public final String trigger;
    public final List<Actor> children;
    public final ActorPool pool;
    
    public String debbug_id = "id not configured";
    
    public final ActorViewElements viewElements = new ActorViewElements();
    boolean acting = false;

    public Actor(Actor parent,
            String trigger,
            List<String> childrenTriggers,
            List<ActorBuilder> childrenBuilders,
            ActorPool pool) {
        this.parent = parent;
        this.trigger = trigger;            
        children = new ArrayList<>();
        int i = 0;
        for(ActorBuilder b : childrenBuilders){
            children.add(b.build(childrenTriggers.get(i), this));
            i++;
        }
        this.pool = pool;
    }
    
    public void onMove(boolean cond){
        if(cond)
            activateTrigger(ON_MOVE);
        else
            desactivateTrigger(ON_MOVE);
    }
    public void onWait(boolean cond){
        if(cond)
            activateTrigger(ON_WAIT);
        else
            desactivateTrigger(ON_WAIT);
    }
    public void onAim(boolean cond){
        if(cond)
            activateTrigger(ON_AIM);
        else
            desactivateTrigger(ON_AIM);
    }
    public void onShootEvent(){
        desactivateTrigger(ON_SHOOT);
        activateTrigger(ON_SHOOT);
    }
    public void onDestroyedEvent(){
        desactivateTrigger(ON_DESTROYED);
        activateTrigger(ON_DESTROYED);
    }
    
    private void activateTrigger(String trigger){
        if(this.trigger.equals(trigger))
            act();
        for(Actor a : children)
            a.activateTrigger(trigger);
    }
    
    private void desactivateTrigger(String trigger){
        if(this.trigger.equals(trigger))
            stopActing();
        for(Actor a : children)
            a.desactivateTrigger(trigger);
    }
    
    protected void act(){
        if(acting)
            return;
        acting = true;
        pool.registerActor(this);
    }
    
    public void stopActing(){
        acting = false;
        pool.deleteActor(this);
    }
    
    public void stopActingAndChildren(){
        stopActing();
        for(Actor child : children)
            child.stopActing();
    }
    
    public Actor getParent(){
        return parent;
    }
    
    public boolean containsModel(){
        return false;
    }
    
    public String getType(){
        return "default";
    }
    
    @Override
    public String toString() {
    	return "("+getClass().getSimpleName()+")"+debbug_id;
    }
    
//    public boolean isActing(){
//        if(acting)
//            return true;
//        for(Actor a : children)
//            if(a.isActing())
//                return true;
//        return false;
//    }
}
