package brainless.openrts.event.network;

import com.jme3.network.serializing.Serializable;


@Serializable
class JoinGameEvent extends NetworkEvent {
	
	private int playerId;
	private int game;

}
