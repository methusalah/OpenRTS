/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders.actors;

import model.battlefield.actors.Actor;
import ressources.definitions.BuilderLibrary;
import ressources.definitions.DefElement;
import model.battlefield.abstractComps.FieldComp;
import model.battlefield.actors.ModelActor;
import static model.builders.actors.ActorBuilder.TRIGGER;
import static model.builders.actors.ActorBuilder.TYPE;
import ressources.definitions.Definition;

/**
 *
 * @author Benoît
 */
public class ModelActorBuilder extends ActorBuilder{
    private static final String MODEL_PATH = "ModelPath";
    private static final String SCALE = "Scale";

    private String modelPath;
    private double scale;
    
    public ModelActorBuilder(Definition def, BuilderLibrary lib){
        super(def, lib);
        for(DefElement de : def.elements)
            switch(de.name){
                case TYPE :
                case TRIGGER :
                case ACTOR_LIST : break;
                case MODEL_PATH : modelPath = de.getVal(); break;
                case SCALE : scale = de.getDoubleVal(); break;
                default:printUnknownElement(de.name);
            }        
    }
    
    public ModelActor build(FieldComp comp){
        return build("", comp, null);
    }

    @Override
    public ModelActor build(String trigger, Actor parent) {
        throw new RuntimeException("Can't create Model actor without a Movable");
    }
    
    public ModelActor build(String trigger, FieldComp comp, Actor parent){
        readFinalizedLibrary();
        ModelActor res = new ModelActor(parent, trigger, childrenTriggers, childrenActorBuilders, lib.battlefield.actorPool, modelPath, scale, comp);
        res.debbug_id = getId();
        return res;
        
    }
}
