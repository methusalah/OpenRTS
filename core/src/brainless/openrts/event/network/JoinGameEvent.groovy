package brainless.openrts.event.network

import groovy.transform.ToString;

import com.jme3.network.serializing.Serializable;


@Serializable
@ToString
class JoinGameEvent extends NetworkEvent {
	
	int playerId
	int game

}
