package network;

import com.jme3.network.serializing.Serializable;

@Serializable
public class Player {

	private String name;

	public Player() {
	}

	public Player(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



}
