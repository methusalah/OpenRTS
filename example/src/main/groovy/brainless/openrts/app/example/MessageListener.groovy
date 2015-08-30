package brainless.openrts.app.example;

import groovy.transform.CompileStatic

import java.util.logging.Logger

import brainless.openrts.event.EventManager
import brainless.openrts.event.network.AckEvent

import com.jme3.network.Client
import com.jme3.network.Message

@CompileStatic
public class MessageListener implements com.jme3.network.MessageListener<Client> {

	private static final Logger logger = Logger.getLogger(ClientStateListener.class.getName());

	@Override
	public void messageReceived(Client source, Message message) {
		if (message instanceof AckEvent) {
			// do something with the message
			AckEvent evt = (AckEvent) message;
			logger.info("Client #" + source.getId() + " received: '" + evt.getAckDate() + "'");
			EventManager.post(evt);
		} // else...
	}
}