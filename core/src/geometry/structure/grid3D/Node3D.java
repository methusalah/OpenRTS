package geometry.structure.grid3D;

import geometry.structure.grid.Node;

public class Node3D extends Node {

	public double elevation;
	
	public Node3D(Grid3D<? extends Node3D> grid, int index) {
		super(grid, index);
	}

}
