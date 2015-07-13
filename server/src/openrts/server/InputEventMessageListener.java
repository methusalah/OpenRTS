package openrts.server;

import java.util.logging.Logger;

import com.jme3.network.HostedConnection;
import com.jme3.network.Message;

import event.SelectEntityEvent;
import event.SelectEntityServerEvent;

public class InputEventMessageListener implements com.jme3.network.MessageListener<HostedConnection> {

	private static final Logger logger = Logger.getLogger(InputEventMessageListener.class.getName());
	protected final static int DOUBLE_CLIC_DELAY = 200;// milliseconds
	protected final static int DOUBLE_CLIC_MAX_OFFSET = 5;// in pixels on screen

	private GameController ctrl;

	public InputEventMessageListener(GameController ctl) {
		this.ctrl = ctl;
	}

	@Override
	public void messageReceived(HostedConnection source, Message message) {
		if (message instanceof SelectEntityEvent) {
			// do something with the message
			SelectEntityEvent inputEvent = (SelectEntityEvent) message;
			logger.info("Client #" + source.getId() + " received: '" + inputEvent.getId() + "'");
			source.getServer().broadcast(new SelectEntityServerEvent(inputEvent.getId()));
		}


	}
}