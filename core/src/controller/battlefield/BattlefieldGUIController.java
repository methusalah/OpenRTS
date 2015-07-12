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
