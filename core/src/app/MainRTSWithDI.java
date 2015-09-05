package app;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import model.ModelManager;
import model.editor.ToolManager;
import view.EditorView;
import view.mapDrawing.MapDrawer;
import brainless.openrts.event.EventManager;
import brainless.openrts.event.client.ControllerChangeEvent;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Module;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;

import controller.Controller;
import controller.battlefield.BattlefieldController;
import controller.editor.EditorController;
import controller.ground.GroundController;

public class MainRTSWithDI extends OpenRTSApplicationWithDI {

	private static final Logger logger = Logger.getLogger(MainRTSWithDI.class.getName());

	EditorView view;
	MapDrawer tr;
	private BattlefieldController fieldCtrl;
	private EditorController editorCtrl;
	private GroundController groundCtrl;
	Controller actualCtrl;
	protected NiftyJmeDisplay niftyDisplay;


	public static void main(String[] args) {
		OpenRTSApplicationWithDI.main(new MainRTSWithDI());
	}

	@Override
	public void simpleInitApp() {
		
		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, 0, -1));
		// stateManager.detach(bulletAppState);

//		flyCam.setUpVector(new Vector3f(0, 0, 1));
//		flyCam.setEnabled(false);

		niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
		niftyDisplay.getNifty().setIgnoreKeyboardEvents(true);
		// TODO: validation is needed to be sure everyting in XML is fine. see http://wiki.jmonkeyengine.org/doku.php/jme3:advanced:nifty_gui_best_practices
		// niftyDisplay.getNifty().validateXml("interface/screen.xml");
		niftyDisplay.getNifty().fromXml("interface/screen.xml", "editor");
		
		List<Module> modules = new ArrayList<Module>();
		modules.add(new MainGuiceModule(niftyDisplay));
		initGuice(modules);

		view = injector.getInstance(EditorView.class);
		
		editorCtrl = injector.getInstance(EditorController.class);
		fieldCtrl = injector.getInstance(BattlefieldController.class);
		groundCtrl = injector.getInstance(GroundController.class);

		// fieldCtrl = new BattlefieldController(view, niftyDisplay.getNifty(), inputManager, cam);
		// editorCtrl = new EditorController(view, niftyDisplay.getNifty(), inputManager, cam);
		// groundCtrl = new GroundController(view, niftyDisplay.getNifty(), inputManager, cam);
		EventManager.register(this);

		actualCtrl = editorCtrl;
		stateManager.attach(actualCtrl);
		actualCtrl.setEnabled(true);
		guiViewPort.addProcessor(niftyDisplay);
		
		
		injector.getInstance(ModelManager.class).setNewBattlefield();
		
	}



	@Override
	public void simpleUpdate(float tpf) {
		float maxedTPF = Math.min(tpf, 0.1f);
		listener.setLocation(cam.getLocation());
		listener.setRotation(cam.getRotation());
		view.getActorManager().render();
		actualCtrl.update(maxedTPF);
//		injector.getInstance(ModelManager.class).updateConfigs();
	}

	@Override
	public void destroy() {
		injector.getInstance(ToolManager.class).killSower();
	}

	@Subscribe
	public void handleEvent(ControllerChangeEvent e) {
		Controller desiredCtrl;
		switch (e.getControllerIndex()) {
			case 0:
				desiredCtrl = fieldCtrl;
				break;
			case 1:
				desiredCtrl = editorCtrl;
				break;
			case 2:
				desiredCtrl = groundCtrl;
				break;
			default:
				return;
		}
		logger.info("switching controller to " + desiredCtrl.getClass().getSimpleName());

		stateManager.detach(actualCtrl);
		actualCtrl.setEnabled(false);
		actualCtrl = desiredCtrl;
		stateManager.attach(actualCtrl);
		actualCtrl.setEnabled(true);

	}

}
