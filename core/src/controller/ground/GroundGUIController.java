/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.ground;

import controller.Controller;
import controller.GUIController;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;

/**
 *
 * @author Benoît
 */
public class GroundGUIController extends GUIController {
	public GroundGUIController(Nifty nifty, Controller controller) {
		super(controller, nifty);
	}

	@Override
	public void activate(){
//		nifty.gotoScreen("network");
	}

	@Override
	public void update() {
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
