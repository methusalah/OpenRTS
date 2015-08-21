package model.battlefield.army.components;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Set of weapons and turrets of a unit.
 *
 * the role of this class is mainly to manage all weapons as one.
 *
 */
public class Arming {
	private static final Logger logger = Logger.getLogger(Mover.class.getName());
	Unit holder;
	ArrayList<Weapon> weapons = new ArrayList<>();
	ArrayList<Turret> turrets = new ArrayList<>();

	boolean aiming;
	boolean onScan;
	boolean atRange;


	public Arming(Unit holder){
		this.holder = holder;
	}

	protected void updateWeapons(){
		onScan = false;
		atRange = false;
		for(Weapon w : weapons){
			w.update(holder.faction.getEnemies().get(0).getUnits());
			if(w.isAtRange()) {
				atRange = true;
			}
			if(w.scanning()) {
				onScan = true;
			}
		}
		aiming = false;
	}

	protected void updateTurrets(double elapsedTime, boolean holderIsMoving){
		for(Turret t : turrets) {
			t.update(elapsedTime, holderIsMoving);
		}
	}

	public boolean isAiming(){
		return aiming;
	}

	protected boolean hasTurret(){
		return !turrets.isEmpty();
	}

	public boolean scanning(){
		return onScan;
	}

	public boolean acquiring(){
		return atRange;
	}

	public boolean acquiring(Unit unit){
		for(Weapon w : weapons) {
			if(w.hasTargetAtRange(unit)) {
				return true;
			}
		}
		return false;
	}

	public void attack(){
		if(!atRange) {
			throw new RuntimeException("Asking to attack but no target at range.");
		}
		aiming = true;
		for(Weapon w : weapons) {
			if(w.isAtRange()) {
				w.attack();
			}
		}
	}

	public void attack(Unit unit){
		aiming = true;
		boolean found = false;
		for(Weapon w : weapons) {
			if(w.hasTargetAtRange(unit)){
				w.attack(unit);
				found = true;
			}
		}
		if(!found) {
			throw new IllegalArgumentException("Specific unit to attack is not at range.");
		}
	}

	public Unit getNearestScanned(){
		Unit res = null;
		for(Weapon w : weapons) {
			if(w.getTarget() != null) {
				res = res == null? w.getTarget():holder.getNearest(res, w.getTarget());
			}
		}
		if(res == null) {
			throw new RuntimeException("Asking the nearest scanned but nothing on scan.");
		}
		return res;
	}


}
