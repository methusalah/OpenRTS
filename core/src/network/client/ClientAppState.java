package network.client;

import java.io.IOException;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;

import event.EventManager;
import event.network.AckEvent;
import event.network.CreateGameEvent;
import event.network.SelectEntityEvent;

public class ClientAppState extends AbstractAppState {

	private static final Logger logger = Logger.getLogger(ClientAppState.class.getName());
	private static final String gameName = "OpenRTS";
	private static String host = "localhost";
	private static final int version = 1;
	private Client networkClient;
	private int currentTick = 0;

	private float tickTimer = 0;

	public ClientAppState(String host) {
		ClientAppState.host = host;
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {

		Serializer.registerClasses(SelectEntityEvent.class, AckEvent.class, CreateGameEvent.class);

		try {
			networkClient = Network.connectToServer(gameName, version, host, 6143);
			networkClient.addClientStateListener(new ClientStateListener());
			networkClient.addMessageListener(new MessageListener(), AckEvent.class);
		} catch (IOException e) {
			logger.severe(e.getLocalizedMessage());
		}

		EventManager.register(this);
		super.initialize(stateManager, app);
	};

	@Override
	public void update(float tpf) {
		tickTimer += tpf;
		if (tickTimer > 1f) {
			currentTick++;
			tickTimer = 0;
		}
	}

	@Override
	public void stateAttached(AppStateManager stateManager) {
		super.stateAttached(stateManager);
		networkClient.start();
	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		networkClient.close();
	}

	@Subscribe
	public void manageEvent(SelectEntityEvent ev) {
		if (!networkClient.isConnected()) {
			networkClient.start();
		}
		networkClient.send(ev);
	}

	@Subscribe
	public void manageEvent(CreateGameEvent ev) {
		if (!networkClient.isConnected()) {
			networkClient.start();
		}
		networkClient.send(ev);
	}


}
