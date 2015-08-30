package event.network;

import groovy.transform.ToString;

import com.jme3.network.serializing.Serializable;

@Serializable
@ToString
class CreateGameEvent extends NetworkEvent {

	String path

}
