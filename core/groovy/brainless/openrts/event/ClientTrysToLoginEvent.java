package brainless.openrts.event;

import brainless.openrts.event.network.NetworkEvent;

import com.jme3.network.serializing.Serializable;

@Serializable
public class ClientTrysToLoginEvent extends NetworkEvent{

	private String user;
	private Integer id;

	public ClientTrysToLoginEvent(){
	
	}
	
	public ClientTrysToLoginEvent(String user, Integer id) {
		this.user = user;
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}



}
