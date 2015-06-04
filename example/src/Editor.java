import model.ModelManager;
import view.EditorView;
import app.OpenRTSApplication;

import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;

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

		EditorView view = new EditorView(rootNode, guiNode, bulletAppState.getPhysicsSpace(), assetManager, viewPort);

		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);

		EditorController editorCtrl = new EditorController(view, niftyDisplay.getNifty(), inputManager, cam);

		niftyDisplay.getNifty().setIgnoreKeyboardEvents(true);
		// TODO: validation is needed to be sure everyting in XML is fine. see http://wiki.jmonkeyengine.org/doku.php/jme3:advanced:nifty_gui_best_practices
		// niftyDisplay.getNifty().validateXml("interface/screen.xml");
		niftyDisplay.getNifty().fromXml("interface/screen.xml", "editor");

		stateManager.attach(editorCtrl);
		editorCtrl.setEnabled(true);

		ModelManager.setNewBattlefield();

		guiViewPort.addProcessor(niftyDisplay);
	}
}
