package openrts.server;

import brainless.openrts.event.network.NetworkEvent;

import com.google.common.eventbus.Subscribe;

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
