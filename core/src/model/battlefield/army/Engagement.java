package model.battlefield.army;

import geometry.tools.LogUtil;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import model.battlefield.army.components.SerializableUnit;
import model.battlefield.army.components.Unit;
import model.battlefield.warfare.Faction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Stores units and factions, and provide a serializable version of the initial situation
 */

public class Engagement {
	@JsonIgnore
	private List<Faction> factions = new ArrayList<>();

	@JsonProperty
	private List<SerializableUnit> initialEngagement = new ArrayList<>();

	public Engagement() {
		Faction f1 = new Faction(Color.red, "1");
		Faction f2 = new Faction(Color.blue, "2");
		f1.setEnemy(f2);
		factions.add(f1);
		factions.add(f2);
	}

	public void reset() {
		LogUtil.logger.info("reseting engagement");
		ArmyManager.reset();

		for (SerializableUnit su : initialEngagement) {
			ArmyManager.registerUnit(su.getUnit(factions));
		}
	}

	public void save() {
		initialEngagement.clear();
		for (Unit u : ArmyManager.getUnits()) {
			initialEngagement.add(new SerializableUnit(u));
		}
	}

	public List<Faction> getFactions() {
		return factions;
	}
}
