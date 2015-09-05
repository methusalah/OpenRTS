package brainless.openrts.event;

import brainless.openrts.event.network.NetworkEvent;

import com.jme3.network.serializing.Serializable;

@Serializable
public class ClientLoggedOutEvent extends NetworkEvent{

	private String user;
	private Integer id;
	
	public ClientLoggedOutEvent(){
	
	}
	
	public ClientLoggedOutEvent(Integer id, String user) {
		this.user = user;

	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	

}
