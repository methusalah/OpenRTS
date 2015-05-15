package network.server;

import network.msg.HelloMessage;

import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;

public class ServerListener implements MessageListener<HostedConnection> {
	public void messageReceived(HostedConnection source, Message message) {
		if (message instanceof HelloMessage) {
			// do something with the message
			HelloMessage helloMessage = (HelloMessage) message;
			System.out.println("Server received '"
					+ helloMessage.getHello() + "' from client #"
					+ source.getId());
		} // else....
	}
}