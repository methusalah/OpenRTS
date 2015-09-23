package brainless.openrts.event;

public class ClientConnectedEvent extends ServerEvent {

	private int id;
	private String address;
	private String name;
	
	public ClientConnectedEvent(int id, String address, String name) {
		super();
		this.id = id;
		this.address = address;
		this.name = name;
	}

	public ClientConnectedEvent(int id, String address) {
		super();
		this.id = id;
		this.address = address;
	}

	public int getId() {
		return id;
	}

	public String getAddress() {
		return address;
	}

	public String getName() {
		return name;
	}
}
