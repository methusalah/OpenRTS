package controller.game;


import network.client.ClientManager;
import app.OpenRTSApplicationWithDI;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

public class NetworkAppState extends AbstractAppState {

	private ViewPort viewPort;
	private Node rootNode;
	private Node guiNode;

	private AssetManager assetManager;
	private Node localRootNode = new Node("Start Screen RootNode");
	private Node localGuiNode = new Node("Start Screen GuiNode");
	private final ColorRGBA backgroundColor = ColorRGBA.Gray;

	private ClientManager clientManager;

	public NetworkAppState(OpenRTSApplicationWithDI app) {
		this.rootNode     = app.getRootNode();
		this.viewPort     = app.getViewPort();
		this.guiNode      = app.getGuiNode();
		this.assetManager = app.getAssetManager();

		// // this = any JME Application
		// Screen screen = new Screen(this);
		// guiNode.addControl(screen);
		// AlertBox box = new AlertBox
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		rootNode.attachChild(localRootNode);
		guiNode.attachChild(localGuiNode);
		viewPort.setBackgroundColor(backgroundColor);

		/** init the screen */
	}

	@Override
	public void update(float tpf) {
		/** any main loop action happens here */

	}

	@Override
	public void cleanup() {
		rootNode.detachChild(localRootNode);
		guiNode.detachChild(localGuiNode);

		super.cleanup();
	}

	// public void initLoginWindow() {
	// loginWindow = new LoginBox(screen, "loginWindow", new Vector2f(screen.getWidth() / 2 - 175, screen.getHeight() / 2 - 125)) {
	// @Override
	// public void onButtonLoginPressed(MouseButtonEvent evt, boolean toggled) {
	// // Some call to the server to log the client in
	// finalizeUserLogin();
	// }
	//
	// @Override
	// public void onBtnCancelInterval() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onBtnCancelMouseLeftDown(MouseButtonEvent arg0, boolean arg1) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onBtnCancelMouseLeftUp(MouseButtonEvent arg0, boolean arg1) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onBtnCancelMouseRightDown(MouseButtonEvent arg0, boolean arg1) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onBtnCancelMouseRightUp(MouseButtonEvent arg0, boolean arg1) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onBtnLoginInterval() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onBtnLoginMouseLeftDown(MouseButtonEvent arg0, boolean arg1) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onBtnLoginMouseLeftUp(MouseButtonEvent arg0, boolean arg1) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onBtnLoginMouseRightDown(MouseButtonEvent arg0, boolean arg1) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onBtnLoginMouseRightUp(MouseButtonEvent arg0, boolean arg1) {
	// // TODO Auto-generated method stub
	//
	// }
	// };
	// screen.addElement(loginWindow);
	//
	//
	// }

}