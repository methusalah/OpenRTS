/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package event;

import com.google.common.eventbus.EventBus;

public class EventManager {

	private static final EventBus eventBus = new EventBus(new RethrowingExceptionHandler());

	public static void post(Event event) {
		// LogUtil.logger.info("Event posted:" + event);
		eventBus.post(event);
	}

	public static void register(Object obj) {
		// LogUtil.logger.info("register for Events:" + obj);
		eventBus.register(obj);
	}

	public static void unregister(Object obj) {
		// LogUtil.logger.info("unregister for Events:" + obj);
		eventBus.unregister(obj);
	}
}
