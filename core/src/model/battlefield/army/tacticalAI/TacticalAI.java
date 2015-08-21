package model.battlefield.army.tacticalAI;

import geometry.geom3d.Point3D;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import model.battlefield.army.components.Mover;
import model.battlefield.army.components.Unit;

/**
 * Provides decision on what to do at each frame, for each unit.
 *
 * This AI algorithm is working as a final state machine, stacking states to
 * get the job done and react to its environment at the same time.
 *
 * the different states are defined by a unique method describing the behavior.
 * Each behavior may :
 *  - stack a new behavior over itself
 *  - pop itself
 *
 * When a behavior is no more needed and pop itself, the previous stacked behavior
 * begin again.
 *
 * It may also receive direct orders from the player. In this special case, all stacked
 * states are pop and the state machine is emptied before desired behaviors are stacked.
 *
 * For example, if the player order to move, the AI stacks the state "wait orders" and
 * the state "move" over it. When move is done, the AI fall to the "wait orders" state.
 *
 */
public class TacticalAI {
	private static final Logger logger = Logger.getLogger(Mover.class.getName());
	protected static final String AUTO_ATTACK = "autoattack";
	protected static final String WAIT_ORDERS = "waitorders";
	protected static final String MOVE = "move";
	protected static final String STOP = "stop";
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

	Point3D post;
	Point3D aggressionPlace;

	double disturbTime;

	List<AttackEvent> aggressions = new ArrayList<>();
	List<Unit> neighbors = new ArrayList<>();

	public TacticalAI(Unit unit) {
		this.unit = unit;
		stateMachine = new FSM(this);
		stateMachine.pushState(WAIT_ORDERS);
	}

	public void orderMove(){
		abandonAll();
		stateMachine.pushState(WAIT_ORDERS);
		stateMachine.pushState(MOVE);
		stateMachine.pushState(STOP);
	}

	public void orderMoveAttack(){
		abandonAll();
		stateMachine.pushState(WAIT_ORDERS);
		stateMachine.pushState(MOVE_ATTACK);
		stateMachine.pushState(STOP);
	}

	public void orderAttack(Unit enemy){
		abandonAll();
		stateMachine.pushState(WAIT_ORDERS);
		stateMachine.pushState(ATTACK, enemy);
	}

	public void orderHold(){
		abandonAll();
		stateMachine.pushState(HOLD);
	}

	public void update(){
		// TODO moche
		filterAttackers();
		unit.getMover().tryHold = false;
		neighbors = getNeighbors();
		stateMachine.update();
	}

	void doWaitOrders(){
		if(!unit.getMover().hasFoundPost) {
			post = unit.getPos();
		}
		unit.idle();
		// let allies pass
		unit.getMover().letPass();

		// return to post if disturbed
		if(post != null && getPostDistance() > FREE_MOVE_RADIUS){
			stateMachine.pushState(RETURN_POST);
			stateMachine.pushState(WAIT, DISTURB_DURATION);
		}

		// attack nearby enemies
		if(unit.arming.scanning()){
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
		// let allies pass
		unit.getMover().letPass();

		if(disturbTime == 0) {
			disturbTime = System.currentTimeMillis();
		} else if(disturbTime+duration < System.currentTimeMillis()){
			disturbTime = 0;
			stateMachine.popState();
		}
	}

	void doReturnPost(){
		if(getPostDistance() < POST_TOLERANCE) {
			stateMachine.popState();
		} else if(isAttacked()) {
			stateMachine.pushState(ATTACK_BACK);
		} else{
			unit.getMover().letPass();
			unit.getMover().seek(post);
		}
	}

	void doAttackBack() {
		if(!isAttacked() || getAggressionPlaceDistance() > PURSUE_RADIUS) {
			stateMachine.popState();
		} else if(unit.arming.acquiring()){
			unit.arming.attack();
		} else if(unit.arming.scanning()) {
			unit.getMover().seek(unit.arming.getNearestScanned().getMover());
		} else if(getValidNearest(getAttackers()) != null) {
			unit.getMover().seek(getValidNearest(getAttackers()).getMover());
		} else {
			stateMachine.popState();
		}
	}

	void doAutoAttack(List<Unit> enemies) {
		if(isAttacked()) {
			stateMachine.pushState(ATTACK_BACK);
		} else if(getPostDistance() > PURSUE_RADIUS) {
			stateMachine.popState();
		} else if(unit.arming.acquiring()){
			unit.arming.attack();
		} else if(unit.arming.scanning()) {
			unit.getMover().seek(unit.arming.getNearestScanned().getMover());
		} else if(getValidNearest(enemies) != null) {
			unit.getMover().seek(getValidNearest(enemies).getMover());
		} else {
			stateMachine.popState();
		}
	}

	void doAttack(Unit u) {
		if(u.destroyed()){
			post = unit.getPos();
			stateMachine.popState();
		} else if(unit.arming.acquiring(u)) {
			unit.arming.attack(u);
			unit.getMover().setDestinationReached();
		} else if(!unit.getMover().hasDestination()) {
			unit.getMover().seek(u.getMover());
		} else {
			unit.getMover().followPath(u.getMover());
		}
	}

	void doMove(){
		post = unit.getPos();
		if(!unit.getMover().hasDestination()){
			stateMachine.popState();
		} else {
			unit.getMover().followPath();
		}
	}

	void doStop(){
		unit.decelerateStrongly();
		if(unit.isStopped()) {
			stateMachine.popState();
		}
	}

	void doMoveAttack(){
		post = unit.getPos();
		if(!unit.getMover().hasDestination()){
			stateMachine.popState();
		} else {
			if(unit.arming.scanning()) {
				stateMachine.pushState(AUTO_ATTACK, new ArrayList<>());
			} else {
				unit.getMover().followPath();
			}
		}
	}

	void doHold(){
		post = unit.getPos();
		unit.idle();
		unit.getMover().tryToHoldPositionSoftly();
		unit.getMover().letPass();
		//        holdposition = true;
		if(unit.arming.acquiring()) {
			unit.arming.attack();
		}
	}

	private double getPostDistance(){
		if(post == null) {
			return 0;
		}
		return unit.getPos().getDistance(post);
	}

	private double getAggressionPlaceDistance(){
		return unit.getPos().getDistance(aggressionPlace);
	}

	private Unit getValidNearest(List<Unit> units) {
		Unit res = null;
		for(Unit u : units) {
			if(!u.destroyed()) {
				res = res == null? u : unit.getNearest(u, res);
			}
		}
		return res;
	}

	private List<Unit> getAttackers() {
		List<Unit> res = new ArrayList<>();
		for(AttackEvent ae : aggressions) {
			if(!ae.enemy.destroyed()) {
				res.add(ae.enemy);
			}
		}
		return res;
	}


	public void registerAsAttacker(Unit attacker) {
		aggressions.add(new AttackEvent(attacker));
		aggressionPlace = unit.getPos();
		for(Unit u : neighbors) {
			u.ai.askForSupport(attacker);
		}
	}

	public void askForSupport(Unit attacker){
		aggressions.add(new AttackEvent(attacker));
		aggressionPlace = unit.getPos();
	}

	private List<Unit> getNeighbors() {
		List<Unit> res = new ArrayList<>();
		for (Unit u : unit.faction.getUnits()) {
			if(unit.getDistance(u) <= SUPPORT_DIST) {
				res.add(u);
			}
		}
		return res;
	}

	private boolean isAttacked(){
		return !aggressions.isEmpty();
	}

	private void filterAttackers(){
		ArrayList<AttackEvent> forgotten = new ArrayList<>();
		for(AttackEvent ae : aggressions) {
			if(ae.enemy.destroyed() || ae.time+TAUNT_DURATION < System.currentTimeMillis()) {
				forgotten.add(ae);
			}
		}
		aggressions.removeAll(forgotten);
	}

	public ArrayList<String> getStates() {
		return stateMachine.states;
	}

	public void abandonAll(){
		post = null;
		aggressionPlace = null;
		aggressions.clear();
		stateMachine.popAll();
	}
}
