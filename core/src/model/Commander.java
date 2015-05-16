/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model;

import geometry.geom2d.AlignedBoundingBox;
import geometry.geom2d.Point2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.battlefield.army.ArmyManager;
import model.battlefield.army.Unity;
import model.battlefield.army.components.Unit;
import model.battlefield.army.motion.pathfinding.FlowField;
import model.battlefield.map.Map;

/**
 * @author Benoît
 */
public class Commander {

	ArmyManager armyManager;
	Map map;

	public ArrayList<Unit> selection = new ArrayList<>();
	public HashMap<String, Unity> unitiesInContext = new HashMap<>();
	boolean moveAttack = false;

	ArrayList<ReportEventListener> listeners = new ArrayList<>();

	public Commander(ArmyManager um, Map map) {
		this.armyManager = um;
		this.map = map;
	}

	public void setMoveAttack() {
		moveAttack = true;
	}

	public void orderHold() {
		for (Unit u : selection) {
			u.ai.orderHold();
		}
	}

	public void select(String label, Point2D pos) {
		if (pos == null) {
			return;
		}
		if (moveAttack) {
			act(label, pos);
		} else if (isValid(label)) {
			for (Unit u : armyManager.units) {
				if (u.label.matches(label)) {
					unselect();
					select(u);
					break;
				}
			}
		}
		sendReportOrder();
	}

	public void select(List<Unit> units) {
		unselect();
		for (Unit u : units) {
			select(u);
		}
		moveAttack = false;
		sendReportOrder();
	}

	public void select(Point2D corner1, Point2D corner2) {
		unselect();
		AlignedBoundingBox rect = new AlignedBoundingBox(corner1, corner2);
		for (Unit u : armyManager.units) {
			if (rect.contains(u.getPos2D())) {
				select(u);
			}
		}
		moveAttack = false;
		sendReportOrder();
	}

	private void select(Unit u) {
		u.selected = true;
		selection.add(u);
	}

	public void act(String label, Point2D pos) {
		if (pos == null) {
			return;
		}
		Unit target = getUnit(label);
		for (Unit u : selection) {
			u.group.clear();
			u.group.addAll(selection);
		}
		if (target != null && target.faction != selection.get(0).faction) {
			orderAttack(target);
		} else {
			orderMove(pos);
		}
		moveAttack = false;
	}

	private void orderMove(Point2D p) {
		FlowField ff = new FlowField(map, p);
		for (Unit u : selection) {
			u.getMover().setDestination(ff);
			if (moveAttack) {
				u.ai.orderMoveAttack();
			} else {
				u.ai.orderMove();
			}
		}
	}

	private void orderAttack(Unit enemy) {
		FlowField ff = new FlowField(map, enemy.getPos2D());
		for (Unit u : selection) {
			u.getMover().setDestination(ff);
			if (moveAttack) {
				// TODO moveattacking an enemy must take care of the enemy movements
				u.ai.orderMoveAttack();
			} else {
				u.ai.orderAttack(enemy);
			}
		}
	}

	private void unselect() {
		for (Unit u : selection) {
			u.selected = false;
		}
		selection.clear();
		sendReportOrder();
	}

	private boolean isValid(String label) {
		return label != null && !label.isEmpty();
	}

	private Unit getUnit(String label) {
		if (isValid(label)) {
			for (Unit u : armyManager.units) {
				if (u.label.matches(label)) {
					return u;
				}
			}
		}
		return null;
	}

	public void selectAll() {
		unselect();
		for (Unit u : armyManager.units) {
			select(u);
		}
		sendReportOrder();
	}

	public void sendReportOrder() {
		for (ReportEventListener l : listeners) {
			l.manageEvent();
		}
	}

	public void registerListener(ReportEventListener l) {
		listeners.add(l);
	}

	public void updateSelectables(Point2D visionCenter) {
		unitiesInContext.clear();
		if (visionCenter != null) {
			for (Unit u : armyManager.units) {
				if (u.getPos2D().getDistance(visionCenter) < 10) {
					if (!unitiesInContext.containsKey(u.UIName)) {
						unitiesInContext.put(u.UIName, new Unity());
					}
					unitiesInContext.get(u.UIName).add(u);
				}
			}
		}
		sendReportOrder();
	}

	public void selectUnityInContext(Unity unityID) {
		unselect();
		for (Unit u : unitiesInContext.get(unityID.UIName)) {
			select(u);
		}
	}

	public ArrayList<Unity> getUnitiesInContext() {
		ArrayList<Unity> res = new ArrayList<>();
		for (Unity unity : unitiesInContext.values()) {
			res.add(unity);
		}
		return res;
	}

}
