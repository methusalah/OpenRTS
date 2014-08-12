/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.tacticalIA;

import geometry.Point2D;
import java.util.ArrayList;
import math.Precision;
import model.army.tacticalIA.AttackEvent;
import model.army.data.Unit;
import model.army.data.Mover;
import model.warfare.Faction;
import tools.LogUtil;

/**
 *a
 * @author Benoît
 */
public class TacticalAI {
    protected static final String AUTO_ATTACK = "autoattack";
    protected static final String WAIT_ORDERS = "waitorders";
    protected static final String MOVE = "move";
    protected static final String ATTACK = "attack";
    protected static final String RETURN_POST = "returnpost";
    protected static final String ATTACK_BACK = "attackback";
    protected static final String WAIT = "wait";
    protected static final String MOVE_ATTACK = "moveattack";
    protected static final String HOLD = "hold";
    protected static final String SUPPORT = "support";
    
    private static double PURSUE_RADIUS = 8;
    private static double FREE_MOVE_RADIUS = 3;
    private static double POST_TOLERANCE = 0.8;
    private static double DISTURB_DURATION = 1000;
    private static double TAUNT_DURATION = 5000;
    private static double SUPPORT_DIST = 3;
    
    Unit unit;
    FSM stateMachine;

    Point2D post;
    Point2D aggressionPlace;

    double disturbTime;
    public boolean holdposition;
    
    ArrayList<AttackEvent> aggressions = new ArrayList<>();
    ArrayList<Unit> neighbors = new ArrayList<>();
    
    public TacticalAI(Unit unit) {
        this.unit = unit;
        stateMachine = new FSM(this);
        stateMachine.pushState(WAIT_ORDERS);
    }
    
    public void orderMove(){
        aggressions.clear();
        stateMachine.popAll();
        stateMachine.pushState(WAIT_ORDERS);
        stateMachine.pushState(MOVE);
    }

    public void orderMoveAttack(){
        aggressions.clear();
        stateMachine.popAll();
        stateMachine.pushState(WAIT_ORDERS);
        stateMachine.pushState(MOVE_ATTACK);
    }

    public void orderAttack(Unit enemy){
        aggressions.clear();
        stateMachine.popAll();
        stateMachine.pushState(WAIT_ORDERS);
        stateMachine.pushState(ATTACK, enemy);
    }

    public void orderHold(){
        aggressions.clear();
        stateMachine.popAll();
        stateMachine.pushState(HOLD);
    }
    
    public void update(){
        // TODO moche
        if(post == null)
            post = unit.getPos();
        filterAttackers();
        neighbors = getNeighbors();
        holdposition = false;
        stateMachine.update();
    }

    void doWaitOrders(){
        unit.idle();
        // let allies pass
        unit.getMover().separate();
        
        // return to post if disturbed
        if(getPostDistance() > FREE_MOVE_RADIUS){
            stateMachine.pushState(RETURN_POST);
            stateMachine.pushState(WAIT, DISTURB_DURATION);
        }
       
        // attack nearby enemies
        if(unit.getWeapon().hasTargetOnScan()){
            stateMachine.pushState(RETURN_POST);
            stateMachine.pushState(AUTO_ATTACK, new ArrayList<>());
        }
        
        // attack back an attacker
        // note that attackers are also registered on nearby allies for support
        if(isAttacked()){
            stateMachine.pushState(RETURN_POST);
            stateMachine.pushState(ATTACK_BACK);
        }
    }
    
    void doWait(double duration){
        if(disturbTime == 0)
            disturbTime = System.currentTimeMillis();
        else if(disturbTime+duration < System.currentTimeMillis()){
            disturbTime = 0;
            stateMachine.popState();
        }
    }
    
    void doReturnPost(){
        if(getPostDistance() < POST_TOLERANCE)
            stateMachine.popState();
        else if(isAttacked())
            stateMachine.pushState(ATTACK_BACK);
        else
            unit.getMover().seek(post);
    }
    
    void doAttackBack() {
        if(!isAttacked() || getAggressionPlaceDistance() > PURSUE_RADIUS)
            stateMachine.popState();
        else if(unit.getWeapon().hasTargetAtRange()){
            holdposition = true;
            unit.getWeapon().attack();
        } else if(unit.getWeapon().hasTargetOnScan())
            unit.getMover().seek(unit.getWeapon().getTarget().getMover());
        else if(getValidNearest(getAttackers()) != null)
            unit.getMover().seek(getValidNearest(getAttackers()).getMover());
        else
            stateMachine.popState();
    }
    
    void doAutoAttack(ArrayList<Unit> enemies) {
        if(isAttacked())
            stateMachine.pushState(ATTACK_BACK);
        else if(getPostDistance() > PURSUE_RADIUS)
            stateMachine.popState();
        else if(unit.getWeapon().hasTargetAtRange()){
            holdposition = true;
            unit.getWeapon().attack();
        } else if(unit.getWeapon().hasTargetOnScan())
            unit.getMover().seek(unit.getWeapon().getTarget().getMover());
        else if(getValidNearest(enemies) != null)
            unit.getMover().seek(getValidNearest(enemies).getMover());
        else
            stateMachine.popState();
    }
    
    void doAttack(Unit u) {
        if(u.destroyed()){
            post = unit.getPos();
            stateMachine.popState();
        } else if(unit.getWeapon().hasTargetAtRange(u)) {
            holdposition = true;
            unit.getWeapon().attack(u);
        } else if(!unit.getMover().hasDestination())
            unit.getMover().seek(u.getMover());
        else
            unit.getMover().followPath();
    }
    
    void doMove(){
        if(!unit.getMover().hasDestination()){
            post = unit.getPos();
            stateMachine.popState();
        } else
            unit.getMover().followPath();
    }

    void doMoveAttack(){
        post = unit.getPos();
        if(!unit.getMover().hasDestination()){
            post = unit.getPos();
            stateMachine.popState();
        } else {
            if(unit.getWeapon().hasTargetOnScan())
                stateMachine.pushState(AUTO_ATTACK, new ArrayList<>());
            else
                unit.getMover().followPath();
        }
    }
    
    void doHold(){
        unit.idle();
        holdposition = true;
        if(unit.getWeapon().hasTargetAtRange())
            unit.getWeapon().attack();
    }
    
    private double getPostDistance(){
        return unit.getPos().getDistance(post);
    }

    private double getAggressionPlaceDistance(){
        return unit.getPos().getDistance(aggressionPlace);
    }
    
    private Unit getValidNearest(ArrayList<Unit> units){
        Unit res = null;
        for(Unit u : units)
            if(!u.destroyed())
                res = res == null? u : unit.getNearest(u, res);
        return res;
    }
    
    private ArrayList<Unit> getAttackers(){
        ArrayList<Unit> res = new ArrayList<>();
        for(AttackEvent ae : aggressions)
            if(!ae.enemy.destroyed())
                res.add(ae.enemy);
        return res;
    }
    

    public void registerAsAttacker(Unit attacker) {
        aggressions.add(new AttackEvent(attacker));
        aggressionPlace = unit.getPos();
        for(Unit u : neighbors)
            u.ai.askForSupport(attacker);
    }
    
    public void askForSupport(Unit attacker){
        aggressions.add(new AttackEvent(attacker));
        aggressionPlace = unit.getPos();
    }
    
    private ArrayList<Unit> getNeighbors() {
        ArrayList<Unit> res = new ArrayList<>();
        for(Unit u : unit.faction.units)
            if(unit.getDistance(u) <= SUPPORT_DIST)
                res.add(u);
        return res;
    }
    
    private boolean isAttacked(){
        return !aggressions.isEmpty();
    }
    
    private void filterAttackers(){
        ArrayList<AttackEvent> forgotten = new ArrayList<>();
        for(AttackEvent ae : aggressions)
            if(ae.enemy.destroyed() || ae.time+TAUNT_DURATION < System.currentTimeMillis())
                forgotten.add(ae);
        aggressions.removeAll(forgotten);
    }
    
    public ArrayList<String> getStates() {
        return stateMachine.states;
    }

}
