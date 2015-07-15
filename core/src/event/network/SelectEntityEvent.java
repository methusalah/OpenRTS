package event.network;

import com.jme3.network.serializing.Serializable;

@Serializable
public class SelectEntityEvent extends NetworkEvent {

	private int id;

	public SelectEntityEvent() {

	}

	public SelectEntityEvent(long id) {
		this.id = (int) id;
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
