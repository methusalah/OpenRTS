package event;

public class InputEvent extends Event {

	private final String command;

	public InputEvent(String command) {
		this.command = command;
	}

	public String getActionCommand() {
		return command;
	}
}
