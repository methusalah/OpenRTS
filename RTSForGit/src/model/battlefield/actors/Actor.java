/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.actors;

import java.util.ArrayList;
import java.util.List;
import model.battlefield.army.ArmyManager;
import model.builders.actors.ActorBuilder;
import tools.LogUtil;
import view.actorDrawing.ActorViewElements;

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
    
    public final Actor parent;
    public final String trigger;
    public final List<Actor> children;
    public final ArmyManager armyManager;
    
    public String debbug_id = "id not configured";
    
    public final ActorViewElements viewElements = new ActorViewElements();
    boolean acting = false;
    boolean destroyed = false;
    

    public Actor(Actor parent,
            String trigger,
            List<String> childrenTriggers,
            List<ActorBuilder> childrenBuilders,
            ArmyManager armyManager) {
        this.parent = parent;
        this.trigger = trigger;            
        children = new ArrayList<>();
        int i = 0;
        for(ActorBuilder b : childrenBuilders){
            children.add(b.build(childrenTriggers.get(i), this));
            i++;
        }
        this.armyManager = armyManager;
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
//        for(Actor a : children)
//            a.interrupt();
    }
    
    public Actor getParent(){
        return parent;
    }
    
    public boolean containsModel(){
        return false;
    }
    
    public void destroy(){
        interrupt();
        acting = false;
        destroyed = true;
    }
    
    public boolean isDestroyed(){
        return destroyed;
    }
    
    public String getType(){
        return "default";
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
