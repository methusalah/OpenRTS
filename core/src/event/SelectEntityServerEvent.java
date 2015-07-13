package event;

import com.jme3.network.serializing.Serializable;

@Serializable
public class SelectEntityServerEvent extends ToClientEvent {

	private int id;

	public SelectEntityServerEvent() {

	}

	public SelectEntityServerEvent(int id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
