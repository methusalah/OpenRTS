package geometry.collections;

import geometry.geom2d.Point2D;

import java.util.ArrayList;
import java.util.List;

public class Map2D<E> {
	
	private List<E> values;
	
	protected int xSize;
	protected int ySize;
	
	public Map2D(){
		
	}
	
	public Map2D(int xSize, int ySize) {
		this(xSize, ySize, null);
	}
	
	public Map2D(int xSize, int ySize, E defaultVal) {
		this.xSize = xSize;
		this.ySize = ySize;
		values = new ArrayList<>(xSize*ySize);
		setAllAs(defaultVal);
	}
	
	public void set(int index, E val) {
		values.set(index, val);
	}

	public void set(int x, int y, E val) {
		checkInBounds(x, y);
		values.set(getIndex(x, y), val);
	}
	
	public void set(Point2D coord, E val){
		set((int)coord.x, (int)coord.y, val);
	}

	
	public E get(int index) {
		return values.get(index);
	}

	public E get(int x, int y) {
		checkInBounds(x, y);
		return values.get(getIndex(x, y));
	}

	public E get(Point2D coord){
		return get((int)coord.x, (int)coord.y);
	}

	
	public int xSize() {
		return xSize;
	}

	public int ySize() {
		return ySize;
	}
	
    public boolean isInBounds(int x, int y){
    	return x >= 0 && x < xSize && y >= 0 && y < ySize;
    }
    
    public boolean isInBounds(Point2D p){
    	return isInBounds((int)p.x, (int)p.y);
    }
    
    private void setAllAs(E value){
    	values.clear();
		for (int i = 0; i < xSize*ySize; i++)
				values.add(value);
    }
    
    public List<E> getAll(){
    	return values;
    }
    
    protected void setAll(List<E> values){
    	this.values = values; 
    }
    
    public int getIndex(int x, int y){
    	return y*xSize+x;
    }

    public Point2D getCoord(int index){
    	return new Point2D(index % xSize, index/xSize);
    }
    
    public int size(){
    	return values.size();
    }
    
    private void checkInBounds(int x, int y){
    	if(!isInBounds(x, y))
    		throw new IllegalArgumentException("("+x+";"+y+") is out of bounds (x-size = "+xSize+"; y-size = "+ySize);
    }
}
