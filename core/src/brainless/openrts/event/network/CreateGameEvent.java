package brainless.openrts.event.network;

import com.jme3.network.serializing.Serializable;

@Serializable
public class CreateGameEvent extends NetworkEvent {

	private String path;

	public String getPath() {
		return path;
	}

}
