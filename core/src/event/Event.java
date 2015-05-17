/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package event;

public abstract class Event {

	public Event() {
	}

	public String getName() {
		return this.getClass().getSimpleName();
	}

}
