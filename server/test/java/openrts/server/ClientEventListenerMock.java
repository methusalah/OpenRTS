package openrts.server;

import com.google.common.eventbus.Subscribe;

import event.ToClientEvent;

public class ClientEventListenerMock {

	private ToClientEvent event;

	@Subscribe
	public void handleServerEvents(ToClientEvent event) {
		this.event = event;
	}

	public ToClientEvent getEvent() {
		return event;
	}

}
