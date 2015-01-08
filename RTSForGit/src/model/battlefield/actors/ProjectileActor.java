/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.actors;

import java.util.ArrayList;
import model.battlefield.army.components.Projectile;
import model.battlefield.army.components.Turret;
import model.battlefield.army.components.Unit;

/**
 *
 * @author Benoît
 */
public class ProjectileActor extends MovableActor {
    
    public ProjectileActor(String trigger, Actor parent){
        super(trigger, parent);
    }
    
    public void setProjectile(Projectile projectile){
        movable = projectile;
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
