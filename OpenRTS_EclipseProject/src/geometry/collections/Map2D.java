package geometry.collections;

import java.util.ArrayList;
import java.util.List;

public class Map2D<E> {
	
	private List<List<E>> values;
	
	protected int xSize;
	protected int ySize;
	
	
	public Map2D(int xSize, int ySize) {
		this.xSize = xSize;
		this.ySize = ySize;
		values = new ArrayList<>(xSize);
		for (int i = 0; i < xSize; i++) {
			List<E> row = new ArrayList<>();
			for (int j = 0; j < ySize; j++) {
				row.add(null);
			}
			values.add(row);
		}
	}
	
	public void set(int x, int y, E value) {
		values.get(x).set(y, value);
	}
	
	public E get(int x, int y) {
		return values.get(x).get(y);
	}
	
	public List<E> get(int x) {
		return values.get(x);
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
    
    public void clear(){
    	setAll(null);
    }
    
    public void setAll(E value){
		for (int i = 0; i < xSize; i++)
			for (int j = 0; j < ySize; j++)
				set(i, j, value);
    	
    }

}
