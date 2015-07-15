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
import event.SelectEntityEvent;
import event.SelectEntityServerEvent;

public class ClientAppState extends AbstractAppState {

	private static final Logger logger = Logger.getLogger(ClientAppState.class.getName());
	private Client networkClient;
	private int currentTick = 0;

	private float tickTimer = 0;

	@Override
	public void initialize(AppStateManager stateManager, Application app) {

		Serializer.registerClasses(SelectEntityEvent.class, SelectEntityServerEvent.class);

		try {
			networkClient = Network.connectToServer("localhost", 6143);
			networkClient.addClientStateListener(new ClientStateListener());
			networkClient.addMessageListener(new MessageListener(), SelectEntityServerEvent.class);
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
		// networkClient.start();
	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		// networkClient.close();
	}

	@Subscribe
	public void manageEvent(SelectEntityEvent ev) {
		if (!networkClient.isConnected()) {
			networkClient.start();
		}
		networkClient.send(ev);
	}

}
