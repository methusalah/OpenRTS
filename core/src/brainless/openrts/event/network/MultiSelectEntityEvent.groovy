package brainless.openrts.event.network;

import groovy.transform.ToString;

import com.jme3.network.serializing.Serializable;

@Serializable
@ToString
public class MultiSelectEntityEvent extends NetworkEvent {

	private long entityId;

	public MultiSelectEntityEvent() {
	}

	public MultiSelectEntityEvent(long entityId) {
		this.entityId = entityId;
	}

}
