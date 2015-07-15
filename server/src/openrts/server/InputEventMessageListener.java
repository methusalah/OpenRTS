package openrts.server;

import java.util.logging.Logger;

import com.jme3.network.HostedConnection;
import com.jme3.network.Message;

import controller.CommandManager;
import event.network.AckEvent;
import event.network.SelectEntityEvent;
import geometry.geom2d.Point2D;

public class InputEventMessageListener implements com.jme3.network.MessageListener<HostedConnection> {

	private static final Logger logger = Logger.getLogger(InputEventMessageListener.class.getName());
	protected final static int DOUBLE_CLIC_DELAY = 200;// milliseconds
	protected final static int DOUBLE_CLIC_MAX_OFFSET = 5;// in pixels on screen

	public InputEventMessageListener() {
	}

	@Override
	public void messageReceived(HostedConnection source, Message message) {
		if (message instanceof SelectEntityEvent) {
			// do something with the message
			SelectEntityEvent inputEvent = (SelectEntityEvent) message;
			logger.info("Client #" + source.getId() + " received: '" + inputEvent.getId() + "'");
			source.getServer().broadcast(new AckEvent(inputEvent.getId()));
			CommandManager.select(inputEvent.getId(), new Point2D());
		} else {
			logger.warning("Client send unsupported Message:" + message);
		}
	}
}