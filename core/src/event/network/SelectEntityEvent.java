package event.network;

import com.jme3.network.serializing.Serializable;

@Serializable
public class SelectEntityEvent extends NetworkEvent {

	private int unitId;

	public SelectEntityEvent() {

	}

	public SelectEntityEvent(long id) {
		this.unitId = (int) id;
	}

	public SelectEntityEvent(int id) {
		this.unitId = id;
	}

	public int getUserId() {
		return unitId;
	}

	public void setUserId(int id) {
		this.unitId = id;
	}

}
