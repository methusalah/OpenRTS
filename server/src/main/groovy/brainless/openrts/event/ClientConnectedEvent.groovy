package brainless.openrts.event;

import groovy.transform.ToString


@ToString
public class ClientConnectedEvent extends ServerEvent {

	int id
	String address
	String name
}
