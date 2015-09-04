package brainless.openrts.model;

import com.jme3.network.serializing.Serializable;

@Serializable
public class Player {

	String name;
	Integer id;

	public Player() {
	}

	public Player(String name, Integer id) {
		this.name = name;
		this.name = id;
	}

}
