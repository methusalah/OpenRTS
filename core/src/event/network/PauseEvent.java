package event.network;

import com.jme3.network.serializing.Serializable;

@Serializable
public class PauseEvent extends NetworkEvent {
	private long entityId;

	public PauseEvent(long entityId) {
		this.entityId = entityId;
	}
}
