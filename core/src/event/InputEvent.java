package event;

public class InputEvent extends Event {

	private final String actionCommand;

	public InputEvent(String command) {
		this.actionCommand = command;
	}

	public String getActionCommand() {
		return actionCommand;
	}
}
