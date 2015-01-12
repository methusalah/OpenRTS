/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.army.components;

import model.battlefield.abstractComps.Hiker;
import geometry.Point2D;
import geometry3D.Point3D;
import java.util.ArrayList;
import math.Angle;
import model.battlefield.abstractComps.FieldComp;
import model.battlefield.actors.UnitActor;
import model.battlefield.army.effects.EffectSource;
import model.battlefield.army.effects.EffectTarget;
import model.battlefield.army.tacticalAI.TacticalAI;
import model.builders.actors.ActorBuilder;
import model.builders.MoverBuilder;
import model.builders.TurretBuilder;
import model.builders.WeaponBuilder;
import model.battlefield.warfare.Faction;
import model.builders.actors.ModelActorBuilder;
import sun.font.EAttribute;

/**
 *
 * @author Benoît
 */
public class Unit extends Hiker implements EffectSource, EffectTarget{

    public enum State {MOVING, AIMING, IDLING, DESTROYED, STUCK};

    // final data
    public final String UIName;
    public final String race;
    public final int maxHealth;
    public final UnitActor actor;
    public final Arming arming;
    public final TacticalAI ai;
    public final String label = "label"+this.toString();
    
    // variables
    public Faction faction;
    public ArrayList<Unit> group = new ArrayList<>();
    public int health;
    public State state = State.IDLING;
    public boolean selected = false;

    public Unit(double radius,
            double speed,
            double mass,
            Point3D pos,
            double yaw,
            MoverBuilder moverBuilder,
            String UIName,
            String race,
            int maxHealth,
            Faction faction,
            ModelActorBuilder actorBuilder) {
        super(radius, speed, mass, pos, yaw, moverBuilder);
        this.UIName = UIName;
        this.race = race;
        this.maxHealth = maxHealth;
        ai = new TacticalAI(this);
        arming = new Arming(this);
        setFaction(faction);
        health = maxHealth;
        actor = (UnitActor)actorBuilder.build(this);
    }
    public Unit(Unit o) {
        super(o.radius, o.speed, o.mass, o.pos, o.yaw, o.mover);
        this.UIName = o.UIName;
        this.race = o.race;
        this.maxHealth = o.maxHealth;
        ai = new TacticalAI(this);
        arming = new Arming(this);
        setFaction(o.faction);
        health = maxHealth;
        actor = o.actor;
    }
    
    private void setFaction(Faction faction){
        if(this.faction != null)
            this.faction.units.remove(this);
        this.faction = faction;
        faction.units.add(this);
    }
    
    public void update(double elapsedTime){
        if(destroyed())
            return;
        findNearbyMovers();
        arming.updateWeapons();
        
        ai.update();

        actor.onAim(arming.isAiming());
        
        mover.updatePosition(elapsedTime);
        
        if(mover.hasMoved){
            actor.onMove(true);
            actor.onWait(false);
        }else{
            actor.onMove(false);
            actor.onWait(true);
        }
        arming.updateTurrets(elapsedTime);
    }
    
    protected boolean isMoving(){
        return state == State.MOVING;
    }
    
    protected void setYaw(double yaw){
        mover.desiredYaw = yaw;
    }
    
    public void idle(){
        state = State.IDLING;
    }
    
    private void findNearbyMovers() {
        mover.toFlockWith.clear();
        mover.toLetPass.clear();
        mover.toAvoid.clear();
        for(Unit u : group)
            if(u != this)
                mover.toFlockWith.add(u.getMover());
        for(Unit u : faction.units)
            if(u != this &&
                    getBoundsDistance(u) <= 0
                    && u.mover.heightmap.equals(mover.heightmap))
                mover.toLetPass.add(u.mover);
        mover.toAvoid = getHoldingNeighbors();
    }
    
    private ArrayList<Mover> getHoldingNeighbors(){
        ArrayList<Mover> res = new ArrayList<>();
        for(Faction f : faction.enemies)
            for(Unit u : f.units)
                res.add(u.mover);
        
        for(Unit u : faction.units)
            if(u != this && u.mover.holdPosition)
                res.add(u.mover);
        return res;
    }
    
    private void destroy(){
        state = State.DESTROYED;
        actor.onMove(false);
        actor.onAim(false);
        actor.onWait(false);
        actor.onDestroyedEvent();
        actor.stopActing();
    }
    
    public void removeFromBattlefield(){
        state = State.DESTROYED;
        actor.stopActingAndChildren();
    }
    
    public boolean destroyed(){
        return state == State.DESTROYED;
    }
    
    public double getHealthRate(){
        return (double)health/maxHealth;
    }
    
    public Mover getMover(){
        return mover;
    }
    
    public Point2D getPos2D(){
        return getPos().get2D();
    }
    
    public ArrayList<Turret> getTurrets(){
        return arming.turrets;
    }
    
    public void addWeapon(WeaponBuilder weaponBuilder, TurretBuilder turretBuilder){
        Turret t = null;
        if(turretBuilder != null){
            t = turretBuilder.build(this);
            arming.turrets.add(t);
        }
        arming.weapons.add(weaponBuilder.build(this, t));
    }
    
    @Override
    public boolean isStillActiveSource() {
        return !destroyed();
    }

    @Override
    public Point3D getDirection() {
        throw new RuntimeException("mustn't call this.");
    }

    @Override
    public void damage(EffectSource source, int amount) {
        health -= amount;
        if(health <= 0)
            destroy();

        ai.registerAsAttacker(source.getUnit());
    }

    @Override
    public Unit getUnit() {
        return this;
    }

//    @Override
    public Unit getNearest(Unit o1, Unit o2) {
        return (Unit)super.getNearest(o1, o2);
    }
    
    
}
