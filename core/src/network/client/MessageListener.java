package network.client;

import java.util.logging.Logger;

import com.jme3.network.Client;
import com.jme3.network.Message;

import event.EventManager;
import event.SelectEntityServerEvent;

public class MessageListener implements com.jme3.network.MessageListener<Client> {

	private static final Logger logger = Logger.getLogger(ClientStateListener.class.getName());

	@Override
	public void messageReceived(Client source, Message message) {
		if (message instanceof SelectEntityServerEvent) {
			// do something with the message
			SelectEntityServerEvent evt = (SelectEntityServerEvent) message;
			logger.info("Client #" + source.getId() + " received: '" + evt.getId() + "'");
			EventManager.post(evt);
		} // else...
	}
}