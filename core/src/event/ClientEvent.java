/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package event;


public abstract class ClientEvent {

	public ClientEvent() {
	}

	public String getName() {
		return this.getClass().getSimpleName();
	}

}
