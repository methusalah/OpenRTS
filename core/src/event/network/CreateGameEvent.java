package event.network;

import com.jme3.network.serializing.Serializable;

@Serializable
public class CreateGameEvent extends NetworkEvent {

	private String path;

	public CreateGameEvent() {

	}

	public CreateGameEvent(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
