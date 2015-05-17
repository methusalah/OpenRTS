/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package event;

import com.google.common.eventbus.EventBus;

public class EventManager {

	private static final EventBus eventBus = new EventBus();

	public static void post(Event event) {
		eventBus.post(event);
	}

	public static void register(Object obj) {
		eventBus.register(obj);
	}

	public static void unregister(Object obj) {
		eventBus.unregister(obj);
	}
}
