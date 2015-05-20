package model.battlefield.warfare;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import model.battlefield.army.components.Unit;

/**
 * Very simple class to hold armies and their relations
 */
public class Faction {

	private List<Faction> allies = new ArrayList<Faction>();
	private List<Faction> enemies = new ArrayList<Faction>();
	private List<Faction> neutrals = new ArrayList<Faction>();

	private List<Unit> units = new ArrayList<Unit>();
	private Color color;
	private String name;

	public Faction(Color c, String name) {
		this.color = c;
		this.name = name;
	}

	public List<Faction> getAllies() {
		return allies;
	}

	public List<Faction> getEnemies() {
		return enemies;
	}

	public List<Faction> getNeutrals() {
		return neutrals;
	}

	public List<Unit> getUnits() {
		return units;
	}

	public String getName() {
		return name;
	}

	public void setAlly(Faction o) {
		remove(o);
		allies.add(o);
		o.allies.add(this);
	}

	public void setEnemy(Faction o) {
		remove(o);
		enemies.add(o);
		o.enemies.add(this);
	}

	public void setNeutral(Faction o) {
		remove(o);
		neutrals.add(o);
		o.neutrals.add(this);
	}

	private void remove(Faction o) {
		allies.remove(o);
		enemies.remove(o);
		neutrals.remove(o);
		o.allies.remove(this);
		o.enemies.remove(this);
		o.neutrals.remove(this);
	}
}
