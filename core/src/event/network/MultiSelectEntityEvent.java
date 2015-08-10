package event.network;

import com.jme3.network.serializing.Serializable;

@Serializable
public class MultiSelectEntityEvent extends NetworkEvent {

	private long entityId;

	public MultiSelectEntityEvent() {
	}

	public MultiSelectEntityEvent(long entityId) {
		this.entityId = entityId;
	}

}
