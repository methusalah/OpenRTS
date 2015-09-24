package brainless.openrts.event.network;

public class HoldEvent extends NetworkEvent {

	private long entityId;

	public HoldEvent(long entityId) {
		this.entityId = entityId;
	}

}
