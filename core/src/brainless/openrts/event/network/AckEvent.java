package brainless.openrts.event.network;

import groovy.transform.ToString;

import java.util.Date;

import com.jme3.network.serializing.Serializable;

@Serializable
@ToString
public class AckEvent extends NetworkEvent{

	private Date ackDate;

	public AckEvent() {

	}

	public AckEvent(Date ackDate) {
		this.ackDate = ackDate;
	}

	public Date getAckDate() {
		return ackDate;
	}

}
