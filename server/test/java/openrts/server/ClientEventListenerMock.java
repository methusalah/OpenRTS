package openrts.server;

import com.google.common.eventbus.Subscribe;

import event.network.NetworkEvent;

public class ClientEventListenerMock {

	private NetworkEvent event;

	@Subscribe
	public void handleServerEvents(NetworkEvent event) {
		this.event = event;
	}

	public NetworkEvent getEvent() {
		return event;
	}

}
