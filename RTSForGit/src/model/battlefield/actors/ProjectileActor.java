/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.actors;

import java.util.ArrayList;
import java.util.List;
import model.battlefield.Battlefield;
import model.battlefield.army.ArmyManager;
import model.battlefield.army.components.Movable;
import model.battlefield.army.components.Projectile;
import model.battlefield.army.components.Turret;
import model.battlefield.army.components.Unit;
import model.builders.actors.ActorBuilder;

/**
 *
 * @author Benoît
 */
public class ProjectileActor extends MovableActor {

    public ProjectileActor(Actor parent,
            String trigger,
            List<String> childrenTriggers,
            List<ActorBuilder> childrenBuilders,
            ActorPool pool,
            String modelPath,
            double scale,
            Movable movable) {
        super(parent, trigger, childrenTriggers, childrenBuilders, pool, modelPath, scale, movable);
        act();
    }
    
    public Projectile getProjectile(){
        return (Projectile)movable;
    }

    @Override
    public String getType() {
        return "projectile";
    }
    
    
}
