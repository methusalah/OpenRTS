/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package event.network;

import java.util.Date;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public abstract class NetworkEvent extends AbstractMessage {

	private Date date;
	private int id;



	public NetworkEvent() {
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
