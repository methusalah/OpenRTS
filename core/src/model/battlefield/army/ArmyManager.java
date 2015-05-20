package model.battlefield.army;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import model.battlefield.army.components.Projectile;
import model.battlefield.army.components.Unit;
import model.battlefield.army.effects.PersistentEffect;

/**
 * updates and destroys all armies' elements at each frame
 *
 * @author Benoît
 */
public class ArmyManager {
	private Map<Long, Unit> units = new HashMap<Long, Unit>();
	public ArrayList<PersistentEffect> persistenteffects = new ArrayList<>();
	public ArrayList<Projectile> projectiles = new ArrayList<>();

	public void update(double elapsedTime) {
		Iterator<Entry<Long, Unit>> unitIterator = units.entrySet().iterator();
		while (unitIterator.hasNext()) {
			Entry<Long, Unit> entry = unitIterator.next();
			Unit u = entry.getValue();
			if (u.destroyed()) {
				u.faction.getUnits().remove(u);
				unitIterator.remove();
			} else {
				u.update(elapsedTime);
			}
		}

		// update persistent effects
		ArrayList<PersistentEffect> terminated = new ArrayList<>();
		for (PersistentEffect e : persistenteffects) {
			if (e.terminated) {
				terminated.add(e);
			} else {
				e.update();
			}
		}
		persistenteffects.removeAll(terminated);

		// update projectiles
		ArrayList<Projectile> arrived = new ArrayList<>();
		for (Projectile p : projectiles) {
			if (p.arrived) {
				arrived.add(p);
			} else {
				p.update(elapsedTime);
			}
		}
		projectiles.removeAll(arrived);

	}

	public void addPersistentEffect(PersistentEffect eff) {
		persistenteffects.add(eff);
	}

	public void registerUnit(Unit unit) {
		units.put(unit.getId(), unit);
	}

	public void unregisterUnit(Unit unit) {
		unit.removeFromBattlefield();
		units.remove(unit);
	}

	public void registerProjectile(Projectile projectile) {
		projectiles.add(projectile);
	}

	public void unregisterProjectile(Projectile projectile) {
		projectile.removeFromBattlefield();
	}

	public void reset() {
		for (Unit u : units.values()) {
			u.removeFromBattlefield();
		}
		for (Projectile p : projectiles) {
			p.removeFromBattlefield();
		}
		persistenteffects.clear();
		update(0);
	}

	public Unit getUnit(long id) {
		return units.get(id);
	}

	public Collection<Unit> getUnits() {
		return units.values();
	}
}
