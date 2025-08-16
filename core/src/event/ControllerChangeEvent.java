package event;

import com.jme3.network.serializing.Serializable;

@Serializable
public class ControllerChangeEvent extends Event {

	private final int index;

	public ControllerChangeEvent(int ctrlIndex) {
		this.index = ctrlIndex;
	}

	public int getControllerIndex() {
		return index;
	}
}
