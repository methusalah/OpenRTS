package event.network;

import com.jme3.network.serializing.Serializable;

@Serializable
public class AckEvent extends NetworkEvent{

	private Integer eventId;

	public AckEvent() {

	}

	public AckEvent(Integer eventId) {
		this.eventId = eventId;
	}

	public Integer getEventId() {
		return eventId;
	}

}
