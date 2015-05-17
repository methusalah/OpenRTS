/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package event;

import com.google.common.eventbus.EventBus;

public class EventManager {

	private static final EventBus eventBus = new EventBus();

	// static {

	// Reflections reflections1 = new Reflections("simulacra.state");
	// Set<Class<?>> annotated = reflections1
	// .getTypesAnnotatedWith(Subscribe.class);

	//		Reflections reflections = new Reflections(new ConfigurationBuilder()
	//				.setUrls(ClasspathHelper.forPackage("simulacra.system"))
	//				.setScanners(new MethodAnnotationsScanner()));
	//		Set<Method> methods = reflections
	//				.getMethodsAnnotatedWith(Subscribe.class);
	//		try {
	//			for (Method method : methods) {
	//				register( method.getDeclaringClass().newInstance());
	//
	//			}
	//		} catch (InstantiationException | IllegalAccessException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}

	// register(new CommandManager());
	// register(new EventLoggerSystem());
	// register(new HealthSystem());
	// register(new InventorySystem());
	// register(new ManaSystem());
	// }

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
