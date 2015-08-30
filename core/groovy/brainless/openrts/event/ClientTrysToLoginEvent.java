package brainless.openrts.event;

import brainless.openrts.event.network.NetworkEvent;

import com.jme3.network.serializing.Serializable;

@Serializable
public class ClientTrysToLoginEvent extends NetworkEvent{

	private String user;

	public ClientTrysToLoginEvent(){
	
	}
	
	public ClientTrysToLoginEvent(String user) {
		this.user = user;

	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}



}
