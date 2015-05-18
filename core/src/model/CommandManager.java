/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model;

import geometry.geom2d.AlignedBoundingBox;
import geometry.geom2d.Point2D;
import geometry.tools.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.battlefield.army.Unity;
import model.battlefield.army.components.Unit;
import model.battlefield.army.motion.pathfinding.FlowField;

/**
 * @author Benoît
 */
public class CommandManager {

	public static List<Unit> selection = new ArrayList<>();
	private static HashMap<String, Unity> unitiesInContext = new HashMap<>();
	static boolean moveAttack = false;
	private static final CommandManager instance = new CommandManager();

	private CommandManager() {
	}

	public static void setMoveAttack() {
		moveAttack = true;
	}

	public static void orderHold() {
		for (Unit u : selection) {
			u.ai.orderHold();
		}
	}

	public static void select(long id, Point2D pos) {
		if (pos == null) {
			return;
		}
		if (moveAttack) {
			act(id, pos);
		} else if (EntityManager.isValidId(id)) {
			Unit u = getUnit(id);
			unselect();
			select(u);
		}
	}

	public static void select(List<Unit> units) {
		unselect();
		for (Unit u : units) {
			select(u);
		}
		moveAttack = false;
	}

	private static void select(Unit u) {
		if (u != null) {
			u.selected = true;
			selection.add(u);
		}
	}

	public static void act(Long id, Point2D pos) {
		if (pos == null) {
			return;
		}
		Unit target = getUnit(id);
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

	private static void orderMove(Point2D p) {
		FlowField ff = new FlowField(ModelManager.battlefield.map, p);
		for (Unit u : selection) {
			u.getMover().setDestination(ff);
			if (moveAttack) {
				u.ai.orderMoveAttack();
			} else {
				u.ai.orderMove();
			}
		}
	}

	private static void orderAttack(Unit enemy) {
		FlowField ff = new FlowField(ModelManager.battlefield.map, enemy.getPos2D());
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

	private static void unselect() {
		for (Unit u : selection) {
			u.selected = false;
		}
		selection.clear();
	}

	private static Unit getUnit(Long id) {
		if (EntityManager.isValidId(id)) {
			return ModelManager.battlefield.armyManager.getUnit(id);
		}
		return null;
	}

	public static void selectAll() {
		unselect();
		for (Unit u : ModelManager.battlefield.armyManager.getUnits()) {
			select(u);
		}
	}

	public static void updateSelectables(Point2D visionCenter) {
		unitiesInContext.clear();
		if (visionCenter != null) {
			for (Unit u : ModelManager.battlefield.armyManager.getUnits()) {
				if (u.getPos2D().getDistance(visionCenter) < 10) {
					if (!unitiesInContext.containsKey(u.UIName)) {
						unitiesInContext.put(u.UIName, new Unity());
					}
					unitiesInContext.get(u.UIName).add(u);
				}
			}
		}
	}

	public static void selectUnityInContext(Unity unityID) {
		unselect();
		for (Unit u : unitiesInContext.get(unityID.UIName)) {
			select(u);
		}
	}

	public static ArrayList<Unity> getUnitiesInContext() {
		ArrayList<Unity> res = new ArrayList<>();
		for (Unity unity : unitiesInContext.values()) {
			res.add(unity);
		}
		return res;
	}

	public static CommandManager getInstance() {
		return instance;
	}

}
