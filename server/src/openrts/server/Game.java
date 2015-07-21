package openrts.server;

import java.util.HashMap;
import java.util.Map;

import network.Player;
import view.MapView;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

import event.EventManager;

public class Game {

	protected GameController fieldCtrl;
	protected NiftyJmeDisplay clientDisplay;
	private Map<Integer, Player> players = new HashMap<Integer, Player>();
	protected MapView view;

	@Inject
	@Named("RootNode")
	private Node rootNode;

	@Inject
	@Named("GuiNode")
	private Node guiNode;

	@Inject
	private BulletAppState bulletAppState;

	@Inject
	private AssetManager assetManager;

	@Inject
	@Named("viewPort")
	private ViewPort viewPort;
	@Inject
	@Named("GuiViewPort")
	private ViewPort guiViewPort;

	@Inject
	private AudioRenderer audioRenderer;

	@Inject
	private InputManager inputManager;

	@Inject
	private AppStateManager stateManager;

	@Inject
	@Named("camera")
	private Camera cam;

	public Game() {

		view = new MapView(rootNode, guiNode, bulletAppState.getPhysicsSpace(), assetManager, viewPort);

		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
		// game = new Game(niftyDisplay, view, inputManager, cam);
		EventManager.register(this);

		if (view.getMapRend() != null) {
			view.getMapRend().renderTiles();
		}



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

		stateManager.attach(fieldCtrl);
		fieldCtrl.setEnabled(true);
		guiViewPort.addProcessor(niftyDisplay);
	}

	// public GameController getFieldCtrl() {
	// return fieldCtrl;
	// }

}
