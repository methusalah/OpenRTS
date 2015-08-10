package event.network;

import com.jme3.network.serializing.Serializable;

@Serializable
public class HoldEvent extends NetworkEvent {

	private long entityId;

	public HoldEvent(long entityId) {
		this.entityId = entityId;
	}

}
