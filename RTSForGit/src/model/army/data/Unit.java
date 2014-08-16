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
    public enum State {MOVE, ATTACK, IDLE, DESTROYED, STUCK};

    // final data
    public String id;
    public String UIName;
    public String race;
    public String modelPath;
    public int maxHealth;
    double sight;
    ArrayList<Turret> turrets = new ArrayList<>();
    ArrayList<Weapon> weapons = new ArrayList<>();
    UnitActor actor;

    public TacticalAI ai;
    public String label = "label"+this.toString();
    
    // variables
    public Faction faction;
    public int health;
    public State state = State.IDLE;
    
    public Unit(Faction faction){
        ai = new TacticalAI(this);
        setFaction(faction);
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
        weapons.get(0).update(faction.enemies.get(0).units);
        
        ai.update();
        
        if(weapons.get(0).isAttacking()){
            state = State.ATTACK;
            head(weapons.get(0).getTargetAngle());
        }
        
        mover.updatePosition(elapsedTime);
        
        if(mover.hasMoved)
            state = State.MOVE;

        if(hasTurret())
            turrets.get(0).update(elapsedTime, mover.hasMoved);
        
        if(!state.equals(lastState))
            switch (state){
                case MOVE : actor.onMove(); break;
                case IDLE : actor.onWait(); break;
                case ATTACK : actor.onAim(); break;
            }
    }
    
    private void head(double angle){
        if(hasTurret())
            turrets.get(0).setYaw(angle-mover.orientation);
        else
            mover.targetOrientation = angle;
    }
    
    public void linkActors(){
        for(Weapon w : weapons)
            if(w.actor != null)
                w.actor.parent = actor;
    }
    
    public void idle(){
        if(state != State.STUCK)
            state = State.IDLE;
    }
    
    private void findNearbyMovers() {
        mover.toFlockWith.clear();
        mover.toAvoid.clear();
        for(Unit u : faction.units)
            if(u != this &&
//                    !u.isHoldingPosition() &&
                    getBoundsDistance(u) <= 0
                    && u.mover.heightmap.equals(mover.heightmap))
                mover.toFlockWith.add(u.mover);
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
        mover.z = -0.5;
        state = State.DESTROYED;
        actor.interrupt();
    }
    
    public boolean destroyed(){
        return state == State.DESTROYED;
    }
    
    public double getHealthRate(){
        return (double)health/maxHealth;
    }
    
    public boolean hasTurret(){
        return !turrets.isEmpty() && turrets.get(0) != null;
    }
    
    public Point2D getPos(){
        return mover.pos;
    }
    
    @Override
    public Point3D getPos3D(){
        return new Point3D(mover.pos, mover.z+0.25, 1);
    }

    public double getTurretOrientation(){
        if(hasTurret())
            return turrets.get(0).yaw;
        else
            throw new RuntimeException("can't get turret orientation if unit has no turret");
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
    
    public Weapon getWeapon(){
        return weapons.get(0);
    }
    
    public ArrayList<Turret> getTurrets(){
        return turrets;
    }
}
