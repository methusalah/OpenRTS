package brainless.openrts.event.network

import groovy.transform.ToString;

import com.jme3.network.serializing.Serializable;


@Serializable
@ToString
class ChatMessageEvent extends NetworkEvent {
	
	int playerId
	String content

}
