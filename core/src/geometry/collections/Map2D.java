package geometry.collections;

import java.util.ArrayList;
import java.util.List;

public class Map2D<E> {
	
	private List<E> values;
	
	protected int xSize;
	protected int ySize;
	
	
	public Map2D(int xSize, int ySize) {
		this(xSize, ySize, null);
	}
	
	public Map2D(int xSize, int ySize, E defaultVal) {
		this.xSize = xSize;
		this.ySize = ySize;
		values = new ArrayList<>(xSize*ySize);
		setAll(defaultVal);
	}
	
	public void set(int x, int y, E val) {
		values.set(y*xSize+x, val);
	}

	public void set(int index, E val) {
		values.set(index, val);
	}

	public E get(int x, int y) {
		return values.get(y*xSize+x);
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
    
    public void setAll(E value){
    	values.clear();
		for (int i = 0; i < xSize*ySize; i++)
				values.add(value);
    }
    
    public List<E> getAll(){
    	return values;
    }
}
