package event;

import com.jme3.network.serializing.Serializable;

import event.network.NetworkEvent;

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
