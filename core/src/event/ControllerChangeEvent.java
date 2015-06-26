package event;

public class ControllerChangeEvent extends ClientEvent {

	private final int index;

	public ControllerChangeEvent(int ctrlIndex) {
		this.index = ctrlIndex;
	}

	public int getControllerIndex() {
		return index;
	}
}
