import java.io.IOException;

import model.ModelManager;
import view.View;
import app.OpenRTSApplication;

import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;

import controller.battlefield.BattlefieldController;

public class GameMutliplayer extends OpenRTSApplication {

	protected static String mapfilename;

	public static void main(String[] args) {

		if (args.length > 0) {
			mapfilename = args[0];
		}

		GameMutliplayer app = new GameMutliplayer();
		OpenRTSApplication.main(app);
		try {
			app.startClient();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void simpleInitApp() {
		BulletAppState bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, 0, -1));
		// stateManager.detach(bulletAppState);

		flyCam.setUpVector(new Vector3f(0, 0, 1));
		flyCam.setEnabled(false);

		View view = new View(rootNode, guiNode, bulletAppState.getPhysicsSpace(), assetManager, viewPort);

		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);

		BattlefieldController fieldCtrl = new BattlefieldController(view, niftyDisplay.getNifty(), inputManager, cam);

		niftyDisplay.getNifty().setIgnoreKeyboardEvents(true);
		// TODO: validation is needed to be sure everyting in XML is fine. see http://wiki.jmonkeyengine.org/doku.php/jme3:advanced:nifty_gui_best_practices
		// niftyDisplay.getNifty().validateXml("interface/screen.xml");
		niftyDisplay.getNifty().fromXml("interface/screen.xml", "hud");

		stateManager.attach(fieldCtrl);
		fieldCtrl.setEnabled(true);

		view.mapRend.renderTiles();

		guiViewPort.addProcessor(niftyDisplay);

		if (!mapfilename.isEmpty()) {
			ModelManager.loadBattlefield(mapfilename);
		}
	}
}
