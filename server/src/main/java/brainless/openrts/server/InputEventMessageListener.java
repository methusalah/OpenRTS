package brainless.openrts.server;

import geometry.geom2d.Point2D;

import java.util.logging.Logger;

import model.ModelManager;
import brainless.openrts.event.ClientLoggedOutEvent;
import brainless.openrts.event.ClientTrysToLoginEvent;
import brainless.openrts.event.EventManager;
import brainless.openrts.event.network.AckEvent;
import brainless.openrts.event.network.CreateGameEvent;
import brainless.openrts.event.network.NetworkEvent;
import brainless.openrts.event.network.SelectEntityEvent;

import com.google.inject.Inject;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;

import controller.CommandManager;


public class InputEventMessageListener implements com.jme3.network.MessageListener<HostedConnection> {

	private static final Logger logger = Logger.getLogger(InputEventMessageListener.class.getName());
	protected final static int DOUBLE_CLIC_DELAY = 200;// milliseconds
	protected final static int DOUBLE_CLIC_MAX_OFFSET = 5;// in pixels on screen

	@Inject
	private CommandManager commandManager;
	
	@Inject
	private ModelManager modelManager;
	
	public InputEventMessageListener() {
	}

	@Override
	public void messageReceived(HostedConnection source, Message message) {
		EventManager.post((NetworkEvent) message);
		if (message instanceof SelectEntityEvent) {
			// do something with the message
			SelectEntityEvent inputEvent = (SelectEntityEvent) message;
			logger.info("Client #" + source.getId() + " received: '" + inputEvent.getId() + "'");
			commandManager.select(inputEvent.getId(), new Point2D());
			source.getServer().broadcast(new AckEvent(inputEvent.getDate()));
		} else if (message instanceof CreateGameEvent) {
			// do something with the message
			CreateGameEvent inputEvent = (CreateGameEvent) message;
			logger.info("Client #" + source.getId() + " received: '" + inputEvent.getPath() + "'");
			modelManager.loadBattlefield(inputEvent.getPath());
			// Game game = new Game();
			source.getServer().broadcast(new AckEvent(inputEvent.getDate()));
		} else if (message instanceof ClientTrysToLoginEvent){
			ClientTrysToLoginEvent msg = (ClientTrysToLoginEvent) message;
			source.getServer().broadcast(new ClientTrysToLoginEvent(msg.getUser(),msg.getConnectionId()));
		}else if (message instanceof ClientLoggedOutEvent){
				ClientLoggedOutEvent msg = (ClientLoggedOutEvent) message;
				source.getServer().broadcast(new ClientLoggedOutEvent(msg.getId(), msg.getUser()));
		}else {
			logger.warning("Client send unsupported Message:" + message);
		}
	}

}