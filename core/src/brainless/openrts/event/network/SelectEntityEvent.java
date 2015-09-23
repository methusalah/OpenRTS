package brainless.openrts.event.network;

import com.jme3.network.serializing.Serializable;

@Serializable
public class SelectEntityEvent extends NetworkEvent {

	int unitId;
	
	public SelectEntityEvent(){
		
	}
	
	public SelectEntityEvent(int entityId) {
		this.unitId = entityId;
	}

	public int getUnitId() {
		return unitId;
	}

}
