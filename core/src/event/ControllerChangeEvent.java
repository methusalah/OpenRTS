package event;

public class ControllerChangeEvent extends Event {

	private final int index;

	public ControllerChangeEvent(int ctrlIndex) {
		this.index = ctrlIndex;
	}

	public int getControllerIndex() {
		return index;
	}
}
