package model.battlefield.army;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
	private static Map<Long, Unit> units = new HashMap<Long, Unit>();
	private static List<PersistentEffect> persistenteffects = new ArrayList<>();
	private static List<Projectile> projectiles = new ArrayList<>();

	public static void update(double elapsedTime) {
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
		List<PersistentEffect> terminated = new ArrayList<PersistentEffect>();
		for (PersistentEffect e : persistenteffects) {
			if (e.terminated) {
				terminated.add(e);
			} else {
				e.update();
			}
		}
		persistenteffects.removeAll(terminated);

		// update projectiles
		List<Projectile> arrived = new ArrayList<>();
		for (Projectile p : projectiles) {
			if (p.arrived) {
				arrived.add(p);
			} else {
				p.update(elapsedTime);
			}
		}
		projectiles.removeAll(arrived);

	}

	public static void addPersistentEffect(PersistentEffect eff) {
		persistenteffects.add(eff);
	}

	public static void registerUnit(Unit unit) {
		units.put(unit.getId(), unit);
	}

	public static void unregisterUnit(Unit unit) {
		unit.removeFromBattlefield();
		units.remove(unit);
	}

	public static void registerProjectile(Projectile projectile) {
		projectiles.add(projectile);
	}

	public void unregisterProjectile(Projectile projectile) {
		projectile.removeFromBattlefield();
	}

	public static void reset() {
		for (Unit u : units.values()) {
			u.removeFromBattlefield();
		}
		for (Projectile p : projectiles) {
			p.removeFromBattlefield();
		}
		persistenteffects.clear();
		update(0);
	}

	public static Unit getUnit(long id) {
		return units.get(id);
	}

	public static Collection<Unit> getUnits() {
		return units.values();
	}
}
