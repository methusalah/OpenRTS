package brainless.openrts.model;

import com.jme3.network.serializing.Serializable;

@Serializable
public class Player {

	String name;

	public Player() {
	}

	public Player(String name) {
		this.name = name;
	}

}
