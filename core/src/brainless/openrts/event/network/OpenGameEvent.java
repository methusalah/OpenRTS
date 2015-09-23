package brainless.openrts.event.network;

import com.jme3.network.serializing.Serializable;


@Serializable
class OpenGameEvent extends NetworkEvent {
	
	private int playerId;
	private String map;
	private int gameId;

}
