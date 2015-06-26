/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package event;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public abstract class ToClientEvent extends AbstractMessage {

	public ToClientEvent() {
	}

	public String getName() {
		return this.getClass().getSimpleName();
	}

}
