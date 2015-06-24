package event;

public class InputEvent extends NetworkEvent {

	private final String command;

	public InputEvent(String command) {
		this.command = command;
	}

	public String getActionCommand() {
		return command;
	}
}
