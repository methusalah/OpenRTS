package brainless.openrts.event.network;

import groovy.transform.ToString
import com.jme3.network.serializing.Serializable;


@Serializable
@ToString
public class MoveAttackEvent extends NetworkEvent {

	private long entityId;

	public MoveAttackEvent(long entityId) {
		this.entityId = entityId;
	}

}
