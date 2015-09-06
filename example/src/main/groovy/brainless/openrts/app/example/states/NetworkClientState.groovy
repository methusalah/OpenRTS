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
import brainless.openrts.model.Player

import exception.TechnicalException
import groovy.transform.CompileStatic

@CompileStatic
public class NetworkClientState extends AbstractAppState {

	private static final Logger logger = Logger.getLogger(NetworkClientState.class.getName());
	private static final String gameName = "OpenRTS";
	private static String host = "localhost";
	private static final int version = 1;
	private Client networkClient;
	private int currentTick = 0;

	private float tickTimer = 0;

	@Inject
	MultiplayerGame main
	
	public NetworkClientState() {
		
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
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
		Serializer.registerClasses(SelectEntityEvent.class,AckEvent.class,CreateGameEvent.class, MultiSelectEntityEvent.class, ClientTrysToLoginEvent.class, ClientLoggedOutEvent.class);
		super.stateAttached(stateManager);
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
		ClientTrysToLoginEvent evt1 = new ClientTrysToLoginEvent(main.game.mySelf.name, networkClient.getId());
		EventManager.post(evt1);
		main.game.players.add(new Player(main.game.mySelf.name,networkClient.getId()))
		main.game.mySelf = new Player(main.game.mySelf.name, networkClient.getId());
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
