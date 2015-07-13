/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package event;

import java.util.logging.Logger;

import com.google.common.eventbus.EventBus;

public class EventManager {

	private static final Logger logger = Logger.getLogger(EventManager.class.getName());

	private static final EventBus eventBus = new EventBus(new RethrowingExceptionHandler());

	public static void post(ToClientEvent event) {
		logger.info("Event posted:" + event);
		eventBus.post(event);
	}

	public static void post(ToServerEvent event) {
		logger.info("Event posted:" + event);
		eventBus.post(event);
	}

	public static void post(ClientEvent event) {
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

	public static void post(ServerEvent event) {
		logger.info("Event posted:" + event);
		eventBus.post(event);
	}
}
