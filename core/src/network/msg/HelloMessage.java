package network.msg;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class HelloMessage extends AbstractMessage {
	private String hello; // custom message data

	public HelloMessage() {
	} // empty constructor

	public HelloMessage(String s) {
		hello = s;
	} // custom constructor

	public String getHello() {
		return hello;
	}
	
	
}