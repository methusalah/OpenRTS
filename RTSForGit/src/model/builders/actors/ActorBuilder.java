/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders.actors;

import model.battlefield.actors.Actor;
import ressources.definitions.BuilderLibrary;
import java.util.ArrayList;
import java.util.List;
import ressources.definitions.DefElement;
import model.builders.Builder;
import ressources.definitions.Definition;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class ActorBuilder extends Builder{
    public static final String TYPE = "Type"; 

    public static final String TYPE_MODEL = "Model";
    public static final String TYPE_PARTICLE = "Particle";
    public static final String TYPE_ANIMATION = "Animation";
    public static final String TYPE_PHYSIC = "Physic";
    
    protected static final String ACTOR_LIST = "ActorList";
    protected static final String TRIGGER = "Trigger";
    protected static final String ACTOR_LINK = "ActorLink";

    protected String type;
    private List<String> childrenActorBuildersID = new ArrayList<>();
    protected List<ActorBuilder> childrenActorBuilders = new ArrayList<>();
    protected List<String> childrenTriggers = new ArrayList<>();
    
    public ActorBuilder(Definition def, BuilderLibrary lib){
        super(def, lib);
        for(DefElement de : def.elements)
            switch(de.name){
                case TYPE : type = de.getVal(); break;
                case ACTOR_LIST :
                    childrenActorBuildersID.add(de.getVal(ACTOR_LINK));
                    childrenTriggers.add(de.getVal(TRIGGER));
                    break;
            }
    }
    
    public Actor build(String trigger, Actor parent){
        return new Actor(parent, trigger, childrenTriggers, childrenActorBuilders, lib.battlefield.actorPool);
    }

    @Override
    public void readFinalizedLibrary() {
        for(String s : childrenActorBuildersID)
            childrenActorBuilders.add(lib.getActorBuilder(s));
        if(childrenActorBuilders.size() != childrenTriggers.size())
            LogUtil.logger.info("fuck "+def.id);
    }

    
}
