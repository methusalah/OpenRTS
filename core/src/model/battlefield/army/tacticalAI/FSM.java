package model.battlefield.army.tacticalAI;

import java.util.ArrayList;
import java.util.List;

import model.battlefield.army.components.Unit;

/**
 * A simple Final State Machine for the Tactical AI states
 */
public class FSM {
	protected ArrayList<String> states = new ArrayList<>();
	private ArrayList<Object> args = new ArrayList<>();
	private TacticalAI ia;

	public FSM(TacticalAI ia) {
		this.ia = ia;
	}

	public void update() {
		if (states.isEmpty()) {
			return;
		}
		Object arg = args.get(0);
		switch (states.get(0)) {
			case TacticalAI.AUTO_ATTACK:
				ia.doAutoAttack((List<Unit>) arg);
				break;
			case TacticalAI.WAIT_ORDERS:
				ia.doWaitOrders();
				break;
			case TacticalAI.MOVE:
				ia.doMove();
				break;
			case TacticalAI.STOP:
				ia.doStop();
				break;
			case TacticalAI.ATTACK:
				ia.doAttack((Unit) arg);
				break;
			case TacticalAI.RETURN_POST:
				ia.doReturnPost();
				break;
			case TacticalAI.ATTACK_BACK:
				ia.doAttackBack();
				break;
			case TacticalAI.WAIT:
				ia.doWait((double) arg);
				break;
			case TacticalAI.MOVE_ATTACK:
				ia.doMoveAttack();
				break;
			case TacticalAI.HOLD:
				ia.doHold();
				break;
			default:
				throw new IllegalArgumentException("not valide method " + states.get(0));
		}
	}

	public void popState() {
		states.remove(0);
		args.remove(0);
	}

	public void pushState(String state) {
		pushState(state, null);
	}

	public void pushState(String state, Object arg) {
		if (states.isEmpty() || states.get(0) != state) {
			states.add(0, state);
			args.add(0, arg);
		}
	}

	public void popAll() {
		states.clear();
		args.clear();
	}

}
