package brainless.openrts.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import brainless.openrts.event.client.Event;
import brainless.openrts.event.network.NetworkEvent;
import exception.TechnicalException;

public class EventManager {

	private static final Logger logger = Logger.getLogger(EventManager.class.getName());

	private static final EventBus eventBus = new EventBus(new RethrowingExceptionHandler());
	private static EventHistory eventhistory = new EventHistory();
	private static Set<Object> registeredIntances = new HashSet<Object>();

	public static void post(NetworkEvent event) {
		logger.info("Event posted:" + event);
		event.setDate(new Date());
		eventBus.post(event);
	}


	public static void post(Object event) {
		logger.info("Event posted:" + event);
		eventBus.post(event);
	}

	public static void register(Object obj) {
		if (registeredIntances.contains(obj)) {
			throw new TechnicalException(" The object is already registered" + obj);
		}
		logger.info("register for ClientEvents:" + obj);

		eventBus.register(obj);
	}


	public static void unregister(Object obj) {
		logger.info("unregister for Events:" + obj);
		eventBus.unregister(obj);
	}


	public static EventHistory getEventhistory() {
		return eventhistory;
	}

	public static class EventHistory {

		private List<Event> events = new ArrayList<Event>();

		//		public void write(JmeExporter ex) throws IOException {
		//			OutputCapsule capsule = ex.getCapsule(this);
		//
		//			capsule.writeSavableArrayList(events, "events",
		//					new ArrayList<AbilityComponent>());
		//		}
		//
		//		public void read(JmeImporter im) throws IOException {
		//			InputCapsule capsule = im.getCapsule(this);
		//			events = capsule.readSavableArrayList("events",
		//					new ArrayList<AbilityComponent>());
		//		}

		public List<Event> getEvents() {
			return events;
		}

		public void add(Event e) {
			events.add(e);
		}
	}
}
