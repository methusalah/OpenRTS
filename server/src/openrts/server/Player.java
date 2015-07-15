package openrts.server;

import view.MapView;

import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;

public class Player {

	protected GameController fieldCtrl;
	protected NiftyJmeDisplay clientDisplay;

	public Player(NiftyJmeDisplay clientDisplay, MapView view, InputManager inputManager, Camera cam) {
		this.clientDisplay = clientDisplay;

		fieldCtrl = new GameController(view, clientDisplay.getNifty(), inputManager, cam);

		clientDisplay.getNifty().setIgnoreKeyboardEvents(true);
		// TODO: validation is needed to be sure everyting in XML is fine. see http://wiki.jmonkeyengine.org/doku.php/jme3:advanced:nifty_gui_best_practices
		try {
			clientDisplay.getNifty().validateXml("interface/gamescreen.xml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		clientDisplay.getNifty().fromXml("interface/gamescreen.xml", "hud");

		fieldCtrl.setEnabled(true);
	}

	public GameController getFieldCtrl() {
		return fieldCtrl;
	}

}
