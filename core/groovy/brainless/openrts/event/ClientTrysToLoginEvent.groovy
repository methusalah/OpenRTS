package brainless.openrts.event;

import brainless.openrts.event.network.NetworkEvent;

import com.jme3.network.serializing.Serializable;

@Serializable
public class ClientTrysToLoginEvent extends NetworkEvent{

	String user;
	Integer connectionId;

	ClientTrysToLoginEvent(){
	
	}
	
	ClientTrysToLoginEvent(String user, Integer id) {
		this.user = user;
		this.connectionId = id;
	}




}
