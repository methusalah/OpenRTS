/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data.actors;

import java.util.ArrayList;
import model.army.data.Actor;
import model.army.data.Projectile;
import model.army.data.Turret;
import model.army.data.Unit;

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
}
