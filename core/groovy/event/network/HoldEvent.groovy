package event.network;

import groovy.transform.ToString;

import com.jme3.network.serializing.Serializable;

@Serializable
@ToString
public class HoldEvent extends NetworkEvent {

	private long entityId;

	public HoldEvent(long entityId) {
		this.entityId = entityId;
	}

}
