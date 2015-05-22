package model.battlefield.army;

import geometry.tools.LogUtil;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import model.battlefield.army.components.SerializableUnit;
import model.battlefield.army.components.Unit;
import model.battlefield.warfare.Faction;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * Stores units and factions, and provide a serializable version of the initial situation
 */
@Root
public class Engagement {
	public List<Faction> factions = new ArrayList<>();

	@ElementList
	public List<SerializableUnit> initialEngagement = new ArrayList<>();

	public Engagement() {
		Faction f1 = new Faction(Color.red, "1");
		Faction f2 = new Faction(Color.blue, "2");
		f1.setEnemy(f2);
		factions.add(f1);
		factions.add(f2);
	}

	public void addUnit(Unit u) {
		ArmyManager.registerUnit(u);
	}

	public void removeUnit(Unit u) {
		ArmyManager.unregisterUnit(u);
	}

	public void resetEngagement() {
		LogUtil.logger.info("reseting engagement");
		ArmyManager.reset();

		for (SerializableUnit up : initialEngagement) {
			ArmyManager.registerUnit(up.getUnit(factions));
		}
	}

	public void saveEngagement() {
		initialEngagement.clear();
		for (Unit u : ArmyManager.getUnits()) {
			initialEngagement.add(new SerializableUnit(u));
		}
	}
}
