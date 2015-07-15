/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package event;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.common.eventbus.EventBus;

import event.client.Event;
import event.network.NetworkEvent;

public class EventManager {

	private static final Logger logger = Logger.getLogger(EventManager.class.getName());

	private static final EventBus eventBus = new EventBus(new RethrowingExceptionHandler());
	private static EventHistory eventhistory = new EventHistory();

	public static void post(NetworkEvent event) {
		logger.info("Event posted:" + event);
		eventBus.post(event);
	}


	public static void post(Event event) {
		logger.info("Event posted:" + event);
		eventBus.post(event);
	}

	public static void register(Object obj) {
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
