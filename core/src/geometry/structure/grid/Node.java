package geometry.structure.grid;


public class Node {
	
	public int index;
	protected Grid grid;
	
	public Node(Grid grid, int index) {
		this.grid = grid;
		this.index = index;
	}
	
	public Node n(){
		return grid.getNorthNode(this);
	}
	public Node s(){
		return grid.getSouthNode(this);
	}
	public Node e(){
		return grid.getEastNode(this);
	}
	public Node w(){
		return grid.getWestNode(this);
	}
	
	

}
