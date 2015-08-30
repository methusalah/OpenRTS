/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brainless.openrts.event.network;

import java.util.Date;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public abstract class NetworkEvent extends AbstractMessage {

	protected Date date;
	protected int id;

}
