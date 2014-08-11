package model.map;

import math.Angle;

public class Cliff {
	public Tile tile;
	
	public double angle = 0;
	public boolean ortho = false;
	public boolean acuteDiag = false;
	public boolean obtuseDiag = false;

	public Cliff(Tile t) {
		tile = t;
	}
	
	public void ComputeAngle() {
		if(tile.n == null || tile.s == null || tile.e == null || tile.w == null)
			return;
		
		// orthogonal
		if(tile.n.isCliff() && tile.s.isCliff()){
			ortho = true;
			if(tile.e.z>tile.w.z)
				angle = Angle.FLAT;
			else
				angle = 0;
		} else if(tile.e.isCliff() && tile.w.isCliff()) {
			ortho = true;
			if(tile.n.z>tile.s.z)
				angle = -Angle.RIGHT;
			else
				angle = Angle.RIGHT;
		// digonal	
		} else if(tile.n.isCliff() && tile.w.isCliff()) {
			angle = 0;
			if(tile.n.getNeighborsMaxLevel()>tile.getNeighborsMaxLevel())
				acuteDiag = true;
			else
				obtuseDiag = true;
		} else if(tile.w.isCliff() && tile.s.isCliff()) {
			angle = Angle.RIGHT;
			if(tile.w.getNeighborsMaxLevel()>tile.getNeighborsMaxLevel())
				acuteDiag = true;
			else
				obtuseDiag = true;
		} else if(tile.s.isCliff() && tile.e.isCliff()) {
			angle = Angle.FLAT;
			if(tile.s.getNeighborsMaxLevel()>tile.getNeighborsMaxLevel())
				acuteDiag = true;
			else
				obtuseDiag = true;
		} else if(tile.e.isCliff() && tile.n.isCliff()) {
			angle = -Angle.RIGHT;
			if(tile.e.getNeighborsMaxLevel()>tile.getNeighborsMaxLevel())
				acuteDiag = true;
			else
				obtuseDiag = true;
		}
                
                if(tile.w!=null && tile.w.level > tile.level ||
                        tile.s!=null && tile.s.level>tile.level ||
                        tile.w.s!=null && tile.w.s.level>tile.level)
                    tile.z = (tile.level+1)*2;
	}
}
