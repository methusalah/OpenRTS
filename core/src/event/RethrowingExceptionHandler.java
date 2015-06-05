package event;

import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

import exception.TechnicalException;

/**
 * Simple logging handler for subscriber exceptions.
 */
class RethrowingExceptionHandler implements SubscriberExceptionHandler {


	@Override
	public void handleException(Throwable exception, SubscriberExceptionContext context) {
		throw new TechnicalException(exception);
	}
}