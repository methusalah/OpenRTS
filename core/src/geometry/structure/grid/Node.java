package geometry.structure.grid;


public class Node {
	
	protected final int index;

	protected final Grid<? extends Node> grid;
	
	public Node(Grid<? extends Node> grid, int index) {
		this.grid = grid;
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
}
