package event;

import com.jme3.network.serializing.Serializable;

@Serializable
public class SelectEntityEvent extends ToServerEvent {

	private int id;

	public SelectEntityEvent() {

	}

	public SelectEntityEvent(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
