/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.battlefield;

import java.util.ArrayList;
import java.util.List;

import model.CommandManager;
import model.battlefield.army.Unity;
import model.battlefield.army.components.Unit;
import controller.Controller;
import controller.GUIController;
import controller.Reporter;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import event.ControllerChangeEvent;
import event.EventManager;

/**
 *
 * @author Benoît
 */
public class BattlefieldGUIController extends GUIController {
	public BattlefieldGUIController(Nifty nifty, Controller controller) {
		super(controller, nifty);
	}

	@Override
	public void activate(){
		nifty.gotoScreen("hud");
	}

	public void selectAll(){
		CommandManager.selectAll();
	}

	public void orderStop(){
		CommandManager.orderStop();
	}

	public void orderHold(){
		CommandManager.orderHold();
	}

	public void orderAttack(){
		CommandManager.setMoveAttack();
	}

	// Tab switching methods
	public void switchToGameMode(){
		EventManager.post(new ControllerChangeEvent(0));
	}

	public void switchToEditorMode(){
		EventManager.post(new ControllerChangeEvent(1));
	}

	public void switchToNetworkMode(){
		EventManager.post(new ControllerChangeEvent(2));
	}

	private String getGroupHealthString() {
		int current = 0;
		int max = 0;
		for (Unit u : CommandManager.selection) {
			current += u.health;
			max += u.maxHealth;
		}
		int percent = (max > 0) ? (int)((double)current / max * 100) : 0;
		return current + " / " + max + " (" + percent + "%)";
	}

	private String getSelectionBreakdown() {
		java.util.Map<String, Integer> counts = new java.util.HashMap<>();
		for (Unit u : CommandManager.selection) {
			counts.put(u.UIName, counts.getOrDefault(u.UIName, 0) + 1);
		}
		java.util.List<String> list = new java.util.ArrayList<>();
		for (java.util.Map.Entry<String, Integer> entry : counts.entrySet()) {
			list.add(entry.getValue() + " " + entry.getKey() + (entry.getValue() > 1 ? "s" : ""));
		}
		return String.join(", ", list);
	}

	@Override
	public void update() {
		if(!nifty.isActive("interface/screen.xml", "hud")) {
			return;
		}
		String n = System.getProperty("line.separator");

		// update info
		if (CommandManager.selection.size() == 1) {
			Unit u = CommandManager.selection.get(0);
			getElement("unitName").getRenderer(TextRenderer.class).setText(Reporter.getName(u));
			getElement("unitHealth").getRenderer(TextRenderer.class).setText(Reporter.getHealth(u));
			getElement("unitState").getRenderer(TextRenderer.class).setText(Reporter.getState(u));
			getElement("unitOrder").getRenderer(TextRenderer.class).setText(Reporter.getOrder(u));
			getElement("unitHolding").getRenderer(TextRenderer.class).setText(Reporter.getHolding(u));
			getElement("info").show();
		} else if (CommandManager.selection.size() > 1) {
			getElement("unitName").getRenderer(TextRenderer.class).setText(CommandManager.selection.size() + " Units Selected");
			getElement("unitHealth").getRenderer(TextRenderer.class).setText("Group Health: " + getGroupHealthString());
			getElement("unitState").getRenderer(TextRenderer.class).setText("Faction: " + CommandManager.selection.get(0).faction.getName());
			getElement("unitOrder").getRenderer(TextRenderer.class).setText("Composition: " + getSelectionBreakdown());
			getElement("unitHolding").getRenderer(TextRenderer.class).setText("Use Command Card / Hotkeys to issue orders.");
			getElement("info").show();
		} else {
			getElement("info").hide();
		}


	}

	@Override
	public void bind(Nifty nifty, Screen screen) {
	}

	@Override
	public void onStartScreen() {
	}

	@Override
	public void onEndScreen() {
	}
}
