package brainless.openrts.event;


public class ClientDisconnectedEvent extends ServerEvent {

	private int id;
	private String address;
	
	public ClientDisconnectedEvent(int id, String address) {
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
	
	
}
