package model.battlefield.army;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import model.battlefield.army.components.Unit;
import model.battlefield.army.components.UnitMemento;
import model.battlefield.warfare.Faction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Stores units and factions, and provide a serializable version of the initial situation
 */

public class Engagement {

	private static final Logger logger = Logger.getLogger(Engagement.class.getName());

	@JsonIgnore
	private List<Faction> factions = new ArrayList<>();

	@JsonProperty
	private List<UnitMemento> initialEngagement = new ArrayList<>();

	public Engagement() {
		Faction f1 = new Faction(Color.red, "1");
		Faction f2 = new Faction(Color.blue, "2");
		f1.setEnemy(f2);
		factions.add(f1);
		factions.add(f2);
	}

	public void reset() {
		logger.info("reseting engagement");
		ArmyManager.reset();

		for (UnitMemento su : initialEngagement) {
			ArmyManager.registerUnit(su.getUnit(factions));
		}
	}

	public void save() {
		initialEngagement.clear();
		for (Unit u : ArmyManager.getUnits()) {
			initialEngagement.add(new UnitMemento(u));
		}
	}

	public List<Faction> getFactions() {
		return factions;
	}
}
