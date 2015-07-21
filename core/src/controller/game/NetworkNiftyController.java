package controller.game;

import java.text.DecimalFormat;
import java.util.logging.Logger;

import model.ModelManager;
import model.battlefield.army.components.Unit;
import network.client.ClientManager;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import controller.CommandManager;
import controller.GUIController;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import event.EventManager;
import event.client.ClientIsConnectedEvent;
import event.client.ClientIsDisconnectedEvent;
import event.network.CreateGameEvent;

public class NetworkNiftyController extends GUIController {

	private static final Logger logger = Logger.getLogger(NetworkNiftyController.class.getName());
	private static DecimalFormat df = new DecimalFormat("0");
	protected static String mapfilename = "assets/maps/test.btf";

	@Inject
	private ClientManager clientManager;

	public NetworkNiftyController() {
		EventManager.register(this);
	}
	// public NetworkNiftyController(Nifty nifty, Controller ctrl) {
	// super(ctrl, nifty);
	// }

	@Override
	public void activate() {
		nifty.gotoScreen("network");
	}

	@Override
	public void update() {
		if (!nifty.isActive("interface/MultiplayerScreen.xml", "network")) {
			return;
		}

		// update info
		if (CommandManager.selection.size() == 1) {
			Unit u = CommandManager.selection.get(0);
			getElement("unitName").getRenderer(TextRenderer.class).setText(getName(u));
			getElement("unitHealth").getRenderer(TextRenderer.class).setText(getHealth(u));
			getElement("unitState").getRenderer(TextRenderer.class).setText(getState(u));
			getElement("unitOrder").getRenderer(TextRenderer.class).setText(getOrder(u));
			getElement("unitHolding").getRenderer(TextRenderer.class).setText(getHolding(u));
			getElement("info").show();
		} else {
			getElement("info").hide();
		}

	}

	@Override
	public void bind(Nifty nifty, Screen screen) {
		this.nifty = nifty;

		Button b2 = nifty.getCurrentScreen().findNiftyControl("createBtn", Button.class);
		b2.disable();

		Button b3 = nifty.getCurrentScreen().findNiftyControl("joinBtn", Button.class);
		b3.disable();
	}

	@Override
	public void onStartScreen() {
	}

	@Override
	public void onEndScreen() {
	}

	public void connectToServer() {
		logger.info("connect to Server was clicked");
		TextField textField = nifty.getCurrentScreen().findNiftyControl("serveradress", TextField.class);

		String textValue = textField.getRealText();
		clientManager.startClient(textValue);
	}

	public void create() {
		logger.info("create was clicked");
		nifty.gotoScreen("loadmap");
		CreateGameEvent evt = new CreateGameEvent(mapfilename);
		EventManager.post(evt);
		ModelManager.loadBattlefield(mapfilename);

	}

	public void join() {
		logger.info("join was clicked");
	}

	public void exit() {
		logger.info("exit was clicked");
	}

	private String getName(Unit u) {
		return "Name : " + u.UIName + " (" + u.race + ")";
	}

	private String getHealth(Unit u) {
		return "Health : " + u.health + "/" + u.maxHealth + " (" + df.format(u.getHealthRate() * 100) + "%)";
	}

	private String getState(Unit u) {
		return "State : " + u.state;
	}

	private String getOrder(Unit u) {
		String res = "Orders : ";
		for (String order : u.ai.getStates()) {
			res = res.concat(order + " /");
		}
		return res;
	}

	private String getHolding(Unit u) {
		if (u.getMover().holdPosition) {
			return "Holding : Yes";
		}
		return "Holding : No";
	}

	@Subscribe
	public void clientIsConnected(ClientIsConnectedEvent evt) {
		// find old image
		// Element niftyElement = nifty.getCurrentScreen().findElementByName("serveradress");
		// TextField textField = nifty.getCurrentScreen().findNiftyControl("serveradress", TextField.class);
		//
		// String textValue = textField.getRealText();

		Button b1 = nifty.getCurrentScreen().findNiftyControl("connectBtn", Button.class);
		b1.disable();

		Button b2 = nifty.getCurrentScreen().findNiftyControl("createBtn", Button.class);
		b2.enable();

	}

	@Subscribe
	public void clientIsConnected(ClientIsDisconnectedEvent evt) {

		Button b1 = nifty.getCurrentScreen().findNiftyControl("connectBtn", Button.class);
		b1.enable();

		Button b2 = nifty.getCurrentScreen().findNiftyControl("createBtn", Button.class);
		b2.disable();

	}

}
