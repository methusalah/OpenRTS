package network.client;

import java.util.logging.Logger;

import com.jme3.network.Client;
import com.jme3.network.Message;

import event.EventManager;
import event.ScreenInputEvent;

public class MessageListener implements com.jme3.network.MessageListener<Client> {

	private static final Logger logger = Logger.getLogger(ClientStateListener.class.getName());

	@Override
	public void messageReceived(Client source, Message message) {
		if (message instanceof ScreenInputEvent) {
			// do something with the message
			ScreenInputEvent helloMessage = (ScreenInputEvent) message;
			logger.info("Client #" + source.getId() + " received: '" + helloMessage.getCommand() + "'");
			EventManager.post(helloMessage);
		} // else...
	}
}