/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package event;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public abstract class ClientEvent extends AbstractMessage {

	public ClientEvent() {
	}

	public String getName() {
		return this.getClass().getSimpleName();
	}

}
