package app.example;
import model.ModelManager;
import model.battlefield.warfare.Faction;
import view.MapView;
import app.OpenRTSApplication;

import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;

import controller.battlefield.BattlefieldController;
import event.EventManager;

public class Game extends OpenRTSApplication {

	protected MapView view;
	protected BattlefieldController fieldCtrl;
	// TODO: I'm not sure, if this is the correct place for faction
	protected Faction faction;

	public static void main(String[] args) {
		OpenRTSApplication.main(new Game());
	}

	@Override
	public void simpleInitApp() {
		BulletAppState bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, 0, -1));
		// stateManager.detach(bulletAppState);

		flyCam.setUpVector(new Vector3f(0, 0, 1));
		flyCam.setEnabled(false);

		view = new MapView(rootNode, guiNode, bulletAppState.getPhysicsSpace(), assetManager, viewPort);

		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
		fieldCtrl = new BattlefieldController(view, niftyDisplay.getNifty(), inputManager, cam);
		EventManager.register(this);

		niftyDisplay.getNifty().setIgnoreKeyboardEvents(true);
		// TODO: validation is needed to be sure everyting in XML is fine. see http://wiki.jmonkeyengine.org/doku.php/jme3:advanced:nifty_gui_best_practices
		// niftyDisplay.getNifty().validateXml("interface/screen.xml");
		niftyDisplay.getNifty().fromXml("interface/screen.xml", "hud");

		stateManager.attach(fieldCtrl);
		fieldCtrl.setEnabled(true);
		if (view.getMapRend() != null) {
			view.getMapRend().renderTiles();
		}
		guiViewPort.addProcessor(niftyDisplay);
	}

	@Override
	public void simpleUpdate(float tpf) {
		float maxedTPF = Math.min(tpf, 0.1f);
		listener.setLocation(cam.getLocation());
		listener.setRotation(cam.getRotation());
		view.getActorManager().render();
		fieldCtrl.update(maxedTPF);
		ModelManager.updateConfigs();
	}
}
