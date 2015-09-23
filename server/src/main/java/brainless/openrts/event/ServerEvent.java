package brainless.openrts.event;

import java.sql.Date;


public abstract class ServerEvent {

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
