package brainless.openrts.model;

import com.jme3.network.serializing.Serializable;

@Serializable
public class Player {

	private String name;
	private Integer id;

	public Player() {
	}

	public Player(String name, Integer id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public Integer getId() {
		return id;
	}

}
