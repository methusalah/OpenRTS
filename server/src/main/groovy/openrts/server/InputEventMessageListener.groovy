package openrts.server;

import java.util.logging.Logger;

import model.ModelManager;

import com.jme3.network.HostedConnection;
import com.jme3.network.Message;

import controller.CommandManager;
import event.ClientLoggedOutEvent
import event.ClientTrysToLoginEvent
import event.EventManager;
import event.network.AckEvent;
import event.network.CreateGameEvent;
import event.network.NetworkEvent;
import event.network.SelectEntityEvent;
import geometry.geom2d.Point2D;
import groovy.transform.CompileStatic;

@CompileStatic
public class InputEventMessageListener implements com.jme3.network.MessageListener<HostedConnection> {

	private static final Logger logger = Logger.getLogger(InputEventMessageListener.class.getName());
	protected final static int DOUBLE_CLIC_DELAY = 200;// milliseconds
	protected final static int DOUBLE_CLIC_MAX_OFFSET = 5;// in pixels on screen

	public InputEventMessageListener() {
	}

	@Override
	public void messageReceived(HostedConnection source, Message message) {
		EventManager.post((NetworkEvent) message);
		if (message instanceof SelectEntityEvent) {
			// do something with the message
			SelectEntityEvent inputEvent = (SelectEntityEvent) message;
			logger.info("Client #" + source.getId() + " received: '" + inputEvent.getId() + "'");
			CommandManager.select(inputEvent.getId(), new Point2D());
			source.getServer().broadcast(new AckEvent(inputEvent.getDate()));
		} else if (message instanceof CreateGameEvent) {
			// do something with the message
			CreateGameEvent inputEvent = (CreateGameEvent) message;
			logger.info("Client #" + source.getId() + " received: '" + inputEvent.getPath() + "'");
			ModelManager.loadBattlefield(inputEvent.getPath());
			// Game game = new Game();
			source.getServer().broadcast(new AckEvent(inputEvent.getDate()));
		} else if (message instanceof ClientTrysToLoginEvent){
			source.getServer().broadcast(new ClientTrysToLoginEvent(message.getUser()));
		}else if (message instanceof ClientLoggedOutEvent){
				source.getServer().broadcast(new ClientLoggedOutEvent(message.getUser()));
		}else {
			logger.warning("Client send unsupported Message:" + message);
		}
	}

}