package openrts.event

import groovy.transform.ToString

@ToString
class ClientDisconnectedEvent extends ServerEvent {

	int id
	String address
}
