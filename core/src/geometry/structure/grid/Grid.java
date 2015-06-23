package geometry.structure.grid;

import geometry.collections.Map2D;
import geometry.geom2d.Point2D;

import java.util.ArrayList;
import java.util.List;

public class Grid  extends Map2D<Node> {

	public Grid(int width, int height) {
		super(width, height);
	}
	
	protected Node getNorthNode(Node n){
		Point2D p = getCoord(n.index).getAddition(0, 1);
		return isInBounds(p)? get(p) : null; 
	}

	protected Node getSouthNode(Node n){
		Point2D p = getCoord(n.index).getAddition(0, -1);
		return isInBounds(p)? get(p) : null; 
	}

	protected Node getEastNode(Node n){
		Point2D p = getCoord(n.index).getAddition(1, 0);
		return isInBounds(p)? get(p) : null; 
	}

	protected Node getWestNode(Node n){
		Point2D p = getCoord(n.index).getAddition(-1, 0);
		return isInBounds(p)? get(p) : null; 
	}
	
	private List<Node> getAround(Node n, int distance) {
		List<Node> res = new ArrayList<>();
		for (int x = -distance; x <= distance; x++) {
			for (int y = -distance; y <= distance; y++) {
				if (x == 0 && y == 0) {
					continue;
				}
				Point2D p = getCoord(n.index).getAddition(x, y);
				if(!isInBounds(p))
					continue;
				res.add(get(p));
			}
		}
		return res;
	}

	public List<Node> get8Around(Node n) {
		List<Node> res = getAround(n, 1);
		return res;
	}
	
	public List<Node> get9Around(Node n) {
		List<Node> res = getAround(n, 1);
		res.add(n);
		return res;
	}

	public List<Node> get16Around(Node n) {
		List<Node> res = getAround(n, 2);
		res.removeAll(getAround(n, 1));
		return res;
	}

	public List<Node> get4Around(Node n) {
		List<Node> res = new ArrayList<>();
		if (n.n() != null) {
			res.add(n.n());
		}
		if (n.s() != null) {
			res.add(n.s());
		}
		if (n.e() != null) {
			res.add(n.e());
		}
		if (n.w() != null) {
			res.add(n.w());
		}
		return res;
	}

	public List<Node> getAround(Point2D p, double distance) {
		List<Node> res = new ArrayList<>();
		
		int ceiled = (int)Math.ceil(distance);
		for (int x = (int)Math.round(p.x)-ceiled; x < (int)Math.round(p.x)+ceiled; x++) {
			for (int y = (int)Math.round(p.y)-ceiled; y < (int)Math.round(p.y)+ceiled; y++) {
				Point2D tileCenter = new Point2D(x+0.5, y+0.5);
				if(tileCenter.getDistance(p)<distance && isInBounds(new Point2D(x, y)))
					res.add(get(x, y));
			}
		}
		return res;
	}

}
