package geometry.structure.grid3D;

import geometry.structure.grid.Node;

public class Node3D extends Node {

	protected double elevation;
	
	public Node3D(Grid3D<? extends Node3D> grid, int index) {
		super(grid, index);
	}

	public double getElevation() {
		return elevation;
	}

	public void setElevation(double elevation) {
		this.elevation = elevation;
	}
	
	public void elevate(double val){
		elevation += val;
	}
}
