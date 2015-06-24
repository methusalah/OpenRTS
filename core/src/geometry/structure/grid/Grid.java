package geometry.structure.grid;

import geometry.collections.Map2D;
import geometry.geom2d.Point2D;

import java.util.ArrayList;
import java.util.List;

public class Grid  extends Map2D<Node> {

	public Grid(int width, int height) {
		super(width, height);
	}
	
	protected <T extends Node> T getNorthNode(T n){
		Point2D p = getCoord(n.index).getAddition(0, 1);
		return isInBounds(p)? (T)get(p) : null; 
	}

	protected <T extends Node> T getSouthNode(T n){
		Point2D p = getCoord(n.index).getAddition(0, -1);
		return isInBounds(p)? (T)get(p) : null; 
	}

	protected <T extends Node> T getEastNode(T n){
		Point2D p = getCoord(n.index).getAddition(1, 0);
		return isInBounds(p)? (T)get(p) : null; 
	}

	protected <T extends Node> T getWestNode(T n){
		Point2D p = getCoord(n.index).getAddition(-1, 0);
		return isInBounds(p)? (T)get(p) : null; 
	}
	
	private <T extends Node> List<T> getAround(T n, int distance) {
		List<T> res = new ArrayList<>();
		for (int x = -distance; x <= distance; x++) {
			for (int y = -distance; y <= distance; y++) {
				if (x == 0 && y == 0) {
					continue;
				}
				Point2D p = getCoord(n.index).getAddition(x, y);
				if(!isInBounds(p))
					continue;
				res.add((T)get(p));
			}
		}
		return res;
	}

	public <T extends Node> List<T> get8Around(T n) {
		List<T> res = getAround(n, 1);
		return res;
	}
	
	public <T extends Node> List<T> get9Around(T n) {
		List<T> res = getAround(n, 1);
		res.add(n);
		return res;
	}

	public <T extends Node> List<T> get16Around(T n) {
		List<T> res = getAround(n, 2);
		res.removeAll(getAround(n, 1));
		return res;
	}

	public <T extends Node> List<T> get4Around(T n) {
		List<T> res = new ArrayList<>();
		if (n.n() != null) {
			res.add((T)n.n());
		}
		if (n.s() != null) {
			res.add((T)n.s());
		}
		if (n.e() != null) {
			res.add((T)n.e());
		}
		if (n.w() != null) {
			res.add((T)n.w());
		}
		return res;
	}

	public <T extends Node> List<T> getAround(Point2D p, double distance) {
		List<T> res = new ArrayList<>();
		
		int ceiled = (int)Math.ceil(distance);
		for (int x = (int)Math.round(p.x)-ceiled; x < (int)Math.round(p.x)+ceiled; x++) {
			for (int y = (int)Math.round(p.y)-ceiled; y < (int)Math.round(p.y)+ceiled; y++) {
				Point2D tileCenter = new Point2D(x+0.5, y+0.5);
				if(tileCenter.getDistance(p)<distance && isInBounds(new Point2D(x, y)))
					res.add((T)get(x, y));
			}
		}
		return res;
	}

}
