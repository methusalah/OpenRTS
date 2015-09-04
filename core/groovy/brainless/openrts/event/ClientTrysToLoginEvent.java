package brainless.openrts.event;

import brainless.openrts.event.network.NetworkEvent;

import com.jme3.network.serializing.Serializable;

@Serializable
public class ClientTrysToLoginEvent extends NetworkEvent{

	String user;
	Integer connectionId;

	public ClientTrysToLoginEvent(){
	
	}
	
	public ClientTrysToLoginEvent(String user, Integer connectionId) {
		this.user = user;
		this.connectionId = connectionId;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Integer getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(Integer connectionId) {
		this.connectionId = connectionId;
	}
	
	



}
