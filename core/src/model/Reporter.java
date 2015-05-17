/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.text.DecimalFormat;

import model.battlefield.army.components.Unit;

/**
 *
 * @author Benoît
 */
public class Reporter {
	private static DecimalFormat df = new DecimalFormat("0");


	public static String getName(Unit u) {
		return "Name : "+u.UIName+" ("+u.race+")";
	}

	public static String getHealth(Unit u) {
		return "Health : "+u.health+"/"+u.maxHealth+" ("+df.format(u.getHealthRate()*100)+"%)";
	}

	public static String getState(Unit u) {
		return "State : "+u.state;
	}

	public static String getOrder(Unit u) {
		String res = "Orders : ";
		for(String order : u.ai.getStates()) {
			res = res.concat(order+" /");
		}
		return res;
	}

	public static String getHolding(Unit u) {
		if(u.getMover().holdPosition) {
			return "Holding : Yes";
		} else {
			return "Holding : No";
		}
	}

	public Reporter() {
	}

	public boolean reportSingleUnit(){
		if (CommandManager.selection.size() == 1) {
			return true;
		}
		return false;
	}

	public boolean reportNothing(){
		if (CommandManager.selection.isEmpty()) {
			return true;
		}
		return false;
	}

}
