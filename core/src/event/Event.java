/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package event;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public abstract class Event extends AbstractMessage {

	public Event() {
	}

	public String getName() {
		return this.getClass().getSimpleName();
	}

}
