package event;

public class GenericEvent extends Event {

	private final Object o;

	public GenericEvent(Object o) {
		this.o = o;
	}

	public Object getObject() {
		return o;
	}
}
