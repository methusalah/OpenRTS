package geometry.structure.grid;

import geometry.collections.Map2D;
import geometry.geom2d.Point2D;

import java.util.ArrayList;
import java.util.List;

public class Grid<T extends Node> extends Map2D<T> {

	public Grid(){
		super();
	}
	public Grid(int width, int height) {
		super(width, height);
	}
	
	public T getNorthNode(T n){
		Point2D p = getCoord(n.index).getAddition(0, 1);
		return isInBounds(p)? get(p) : null; 
	}

	public T getSouthNode(T n){
		Point2D p = getCoord(n.index).getAddition(0, -1);
		return isInBounds(p)? get(p) : null; 
	}

	public T getEastNode(T n){
		Point2D p = getCoord(n.index).getAddition(1, 0);
		return isInBounds(p)? get(p) : null; 
	}

	public T getWestNode(T n){
		Point2D p = getCoord(n.index).getAddition(-1, 0);
		return isInBounds(p)? get(p) : null; 
	}
	
	public List<T> getInSquareWithourCenter(T n, int distance) {
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
	public List<T> getInSquare(T n, int distance) {
		List<T> res = getInSquareWithourCenter(n, distance);
		res.add(n);
		return res;
	}

	public List<T> get8Around(T n) {
		List<T> res = getInSquareWithourCenter(n, 1);
		return res;
	}
	
	public List<T> get9Around(T n) {
		List<T> res = getInSquare(n, 1);
		return res;
	}

	public List<T> get25Around(T n) {
		List<T> res = getInSquare(n, 2);
		return res;
	}

	public List<T> get4Around(T node) {
		T n = getNorthNode(node);
		T s = getSouthNode(node);
		T e = getEastNode(node);
		T w = getWestNode(node);
		List<T> res = new ArrayList<>();
		if (n != null) {
			res.add(n);
		}
		if (s != null) {
			res.add(s);
		}
		if (e != null) {
			res.add(e);
		}
		if (w != null) {
			res.add(w);
		}
		return res;
	}

	public List<T> getInCircle(Point2D p, double distance) {
		int ceiled = (int)Math.ceil(distance);
		List<T> res = new ArrayList<>();
		for(T node : getInSquare(get(p), ceiled)){
			Point2D nodeCenter = getCoord(node.getIndex()).getAddition(0.5);
			if(nodeCenter.getDistance(p) < distance)
				res.add(node);
		}
		return res;
	}
}
