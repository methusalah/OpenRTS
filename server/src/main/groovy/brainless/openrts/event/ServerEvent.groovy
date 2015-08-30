package brainless.openrts.event;

import groovy.transform.ToString

@ToString
abstract class ServerEvent {

	private Date date;

	public ServerEvent() {
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
