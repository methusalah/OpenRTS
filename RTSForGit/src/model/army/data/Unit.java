/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data;

import geometry.Point2D;
import geometry3D.Point3D;
import java.util.ArrayList;
import math.Angle;
import model.army.data.actors.UnitActor;
import model.army.tacticalIA.TacticalAI;
import model.warfare.Faction;

/**
 *
 * @author Benoît
 */
public class Unit extends Movable {
    public enum State {MOVING, AIMING, IDLING, DESTROYED, STUCK};

    // final data
    public String id;
    public String UIName;
    public String race;
    public String modelPath;
    public int maxHealth;
    double sight;
    UnitActor actor;

    public Arming arming;
    public TacticalAI ai;
    public String label = "label"+this.toString();
    
    // variables
    public Faction faction;
    public ArrayList<Unit> group = new ArrayList<>();
    public int health;
    public State state = State.IDLING;
    
    public Unit(Faction faction){
        ai = new TacticalAI(this);
        setFaction(faction);
        arming = new Arming(this);
    }
    
    void setMaxHealth(int maxHealth){
        this.maxHealth = maxHealth;
        health = maxHealth;
    }
    
    public void setFaction(Faction faction){
        if(this.faction != null)
            this.faction.units.remove(this);
        this.faction = faction;
        faction.units.add(this);
    }
    
    public void update(double elapsedTime){
        State lastState = state;
        if(destroyed())
            return;
        
        findNearbyMovers();
        arming.updateWeapons();
        
        ai.update();

        if(arming.isAiming())
            state = State.AIMING;
        
        mover.updatePosition(elapsedTime);
        
        if(mover.hasMoved)
            state = State.MOVING;

        arming.updateTurrets(elapsedTime);
        
        if(!state.equals(lastState))
            switch (state){
                case MOVING : actor.onMove(); break;
                case IDLING : actor.onWait(); break;
                case AIMING : actor.onAim(); break;
            }
    }
    
    protected boolean isMoving(){
        return state == State.MOVING;
    }
    
    protected void setYaw(double yaw){
        mover.desiredYaw = yaw;
    }
    
    public void linkActors(){
        for(Weapon w : arming.weapons)
            if(w.actor != null)
                w.actor.parent = actor;
    }
    
    public void idle(){
        if(state != State.STUCK)
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
            if(u != this && u.isHoldingPosition())
                res.add(u.mover);
        return res;
    }
    
    public boolean isHoldingPosition(){
        return ai.holdposition;
    }
    
    public void damage(int amount) {
        health -= amount;
        if(health <= 0)
            destroy();
    }
    
    private void destroy(){
        state = State.DESTROYED;
        actor.interrupt();
    }
    
    public boolean destroyed(){
        return state == State.DESTROYED;
    }
    
    public double getHealthRate(){
        return (double)health/maxHealth;
    }
    
    @Override
    public Point3D getPos(){
        return mover.pos;
    }

    public double getDistance(Unit other){
        return mover.getDistance(other.mover);
    }
    
    public double getBoundsDistance(Unit other){
        return getDistance(other)-mover.getSpacing(other.mover);
    }
    
    public Unit getNearest(Unit o1, Unit o2){
        if(getDistance(o1) < getDistance(o2))
            return o1;
        else
            return o2;
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
}
