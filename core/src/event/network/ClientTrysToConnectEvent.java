package event.network;

import java.util.Date;

import com.jme3.network.serializing.Serializable;

@Serializable
public class ClientTrysToConnectEvent extends NetworkEvent{

	private String user;

	public ClientTrysToConnectEvent(){
	
	}
	
	public ClientTrysToConnectEvent(String user) {
		this.user = user;

	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}



}
