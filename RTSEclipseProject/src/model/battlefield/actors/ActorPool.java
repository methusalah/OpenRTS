/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.actors;

import java.util.ArrayList;
import java.util.List;

import model.builders.actors.ActorBuilder;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class ActorPool {
    
    public List<Actor> activeActors = new ArrayList<>();
    public List<Actor> deletedActors = new ArrayList<>();
    
    public void registerActor(Actor actor){
//    	if(actor instanceof PhysicActor)
//    		LogUtil.logger.info("register actor"+actor.getType()+" of id "+actor.debbug_id+" class "+actor.getClass().getSimpleName());	
        activeActors.add(actor);
    }
    
    public void deleteActor(Actor actor){
//    	if(actor instanceof PhysicActor)
//    		LogUtil.logger.info("unregister actor"+actor.getType()+" of id "+actor.debbug_id+" class "+actor.getClass().getSimpleName());	
        activeActors.remove(actor);
        deletedActors.add(actor);
    }
    public ArrayList<Actor> grabDeletedActors(){
        ArrayList<Actor> res = new ArrayList<>(deletedActors);
        deletedActors.clear();
        return res;
    }
    public ArrayList<Actor> getActors(){
        ArrayList<Actor> res = new ArrayList<>(activeActors);
        return res;
    }
}
