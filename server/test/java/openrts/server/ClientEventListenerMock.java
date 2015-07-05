package openrts.server;

import com.google.common.eventbus.Subscribe;

import event.ServerEvent;

public class ClientEventListenerMock {

	private ServerEvent event;

	@Subscribe
	public void handleServerEvents(ServerEvent event) {
		this.event = event;
	}

	public ServerEvent getEvent() {
		return event;
	}

}
