import model.Model;
import view.View;
import app.OpenRTSApplication;

import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.system.AppSettings;

import controller.editor.EditorController;

public class Editor extends OpenRTSApplication {

	public static void main(String[] args) {
		Editor app = new Editor();
		OpenRTSApplication.main(app);
	}

	@Override
	public void simpleInitApp() {
		BulletAppState bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, 0, -1));
		// stateManager.detach(bulletAppState);

		flyCam.setUpVector(new Vector3f(0, 0, 1));
		flyCam.setEnabled(false);

		Model model = new Model();
		View view = new View(rootNode, guiNode, bulletAppState.getPhysicsSpace(), assetManager, viewPort, model);

		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);

		EditorController editorCtrl = new EditorController(model, view, niftyDisplay.getNifty(), inputManager, cam);

		niftyDisplay.getNifty().setIgnoreKeyboardEvents(true);
		// TODO: validation is needed to be sure everyting in XML is fine. see http://wiki.jmonkeyengine.org/doku.php/jme3:advanced:nifty_gui_best_practices
		// niftyDisplay.getNifty().validateXml("interface/screen.xml");
		niftyDisplay.getNifty().fromXml("interface/screen.xml", "editor");

		stateManager.attach(editorCtrl);
		editorCtrl.setEnabled(true);

		view.mapRend.renderTiles();

		guiViewPort.addProcessor(niftyDisplay);
	}

	@Override
	public AppSettings getDefaultSetting() {
		AppSettings settings = new AppSettings(true);
		settings.setBitsPerPixel(32);
		settings.setWidth(1920);
		settings.setHeight(960);
		settings.setTitle("OpenRTS Example Game");
		settings.setVSync(true);
		settings.putBoolean("isCustomized", true);
		settings.setFullscreen(true);
		return settings;
	}
}
