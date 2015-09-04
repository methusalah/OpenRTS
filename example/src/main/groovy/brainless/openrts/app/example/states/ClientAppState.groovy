package brainless.openrts.app.example.states;

import java.util.logging.Logger

import brainless.openrts.app.example.ClientStateListener
import brainless.openrts.app.example.MessageListener
import brainless.openrts.app.example.MultiplayerGame
import brainless.openrts.event.ClientLoggedOutEvent
import brainless.openrts.event.ClientTrysToLoginEvent
import brainless.openrts.event.EventManager
import brainless.openrts.event.network.AckEvent
import brainless.openrts.event.network.CreateGameEvent
import brainless.openrts.event.network.MultiSelectEntityEvent
import brainless.openrts.event.network.SelectEntityEvent

import com.google.common.eventbus.Subscribe
import com.google.inject.Inject
import com.jme3.app.Application
import com.jme3.app.state.AbstractAppState
import com.jme3.app.state.AppStateManager
import com.jme3.network.Client
import com.jme3.network.Network
import com.jme3.network.serializing.Serializer

import exception.TechnicalException
import groovy.transform.CompileStatic

@CompileStatic
public class ClientAppState extends AbstractAppState {

	private static final Logger logger = Logger.getLogger(ClientAppState.class.getName());
	private static final String gameName = "OpenRTS";
	private static String host = "localhost";
	private static final int version = 1;
	private Client networkClient;
	private int currentTick = 0;

	private float tickTimer = 0;

	@Inject
	MultiplayerGame main
	
	public ClientAppState() {
		
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		Serializer.registerClasses(SelectEntityEvent.class,AckEvent.class,CreateGameEvent.class, MultiSelectEntityEvent.class, ClientTrysToLoginEvent.class, ClientLoggedOutEvent.class);

		try {
			networkClient = Network.connectToServer(gameName, version, host, 6143);
		} catch (IOException e) {
			throw new TechnicalException(e.getLocalizedMessage());
		}
		networkClient.addClientStateListener(new ClientStateListener());
		networkClient.addMessageListener(new MessageListener(), AckEvent.class);

		networkClient.start();
		waitUntilClientIsConnected(10);
		
		EventManager.register(this);
		ClientTrysToLoginEvent evt1 = new ClientTrysToLoginEvent(main.game.players.first().name);
		EventManager.post(evt1);
		
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
//		if (networkClient != null) {
//			networkClient.start();
//		}
	}

	@Override
	public void stateDetached(AppStateManager stateManager) {
		networkClient.close();
	}

	@Subscribe
	public void manageEvent(SelectEntityEvent ev) {
		networkClient.send(ev);
	}

	@Subscribe
	public void manageEvent(CreateGameEvent ev) {
		networkClient.send(ev);
	}
	
	@Subscribe
	public void sendUserData(ClientTrysToLoginEvent ev){
		networkClient.send(ev);
	}
	
	@Subscribe
	public void manageEvent(ClientLoggedOutEvent ev){
		networkClient.send(ev);
	}

	private void waitUntilClientIsConnected(int times) {
		int waitingCounter = 0;
		boolean waiting = true;
		while (waiting) {

			if (networkClient.isConnected()) {
				waiting = false;
				return;
			}

			logger.info("Waiting for answer from server...");
			if (times > 0 && waitingCounter > times) {
				throw new TechnicalException("Client are waiting too long..");
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
			waitingCounter++;
		}
	}
}
