package geometry.structure.grid;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Node {
	
	@JsonProperty
	protected final int index;

	@JsonIgnore
	protected Grid<? extends Node> grid;
	
	public Node(Grid<? extends Node> grid, int index) {
		this.grid = grid;
		this.index = index;
	}

	@JsonIgnore
	public int getIndex() {
		return index;
	}
}
