package event;

import java.util.Date;

import com.jme3.network.serializing.Serializable;
import com.sun.xml.internal.ws.developer.StreamingAttachment;

import event.network.NetworkEvent;

@Serializable
public class ClientLoggedOutEvent extends NetworkEvent{

	private String user;

	public ClientLoggedOutEvent(){
	
	}
	
	public ClientLoggedOutEvent(String user) {
		this.user = user;

	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}



}
