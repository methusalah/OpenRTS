package geometry.structure.grid;


public class Node {
	
	public int index;
	protected Grid<? extends Node> grid;
	
	public Node(Grid<? extends Node> grid, int index) {
		this.grid = grid;
		this.index = index;
	}
}
