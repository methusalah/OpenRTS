/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package controller.battlefield;

import java.text.DecimalFormat;

import model.battlefield.army.components.Unit;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import controller.CommandManager;
import controller.GUIController;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;

/**
 * @author Benoît
 */
public class BattlefieldGUIController extends GUIController {

	private static DecimalFormat df = new DecimalFormat("0");

	@Inject
	public BattlefieldGUIController(Nifty nifty) {
		super(nifty);
	}

	@Override
	public void activate() {
		nifty.gotoScreen("hud");
	}

	public void selectAll() {
		CommandManager.selectAll();
	}

	@Override
	public void update() {
		if (!nifty.isActive("interface/screen.xml", "hud")) {
			return;
		}

		// update info
		if (CommandManager.selection.size() == 1) {
			Unit u = CommandManager.selection.get(0);
			getElement("unitName").getRenderer(TextRenderer.class).setText(getName(u));
			getElement("unitHealth").getRenderer(TextRenderer.class).setText(getHealth(u));
			getElement("unitState").getRenderer(TextRenderer.class).setText(getState(u));
			getElement("unitOrder").getRenderer(TextRenderer.class).setText(getOrder(u));
			getElement("unitHolding").getRenderer(TextRenderer.class).setText(getHolding(u));
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

	private String getName(Unit u) {
		return "Name : " + u.UIName + " (" + u.race + ")";
	}

	private String getHealth(Unit u) {
		return "Health : " + u.health + "/" + u.maxHealth + " (" + df.format(u.getHealthRate() * 100) + "%)";
	}

	private String getState(Unit u) {
		return "State : " + u.state;
	}

	private String getOrder(Unit u) {
		String res = "Orders : ";
		for (String order : u.ai.getStates()) {
			res = res.concat(order + " /");
		}
		return res;
	}

	private String getHolding(Unit u) {
		if (u.getMover().holdPosition) {
			return "Holding : Yes";
		}
		return "Holding : No";
	}
}
