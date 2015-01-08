/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.map.atlas;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Benoît
 */
public class DoubleMap {
	private List<Double> values;
	private int xSize;
	private int ySize;
	
	
	public DoubleMap(int xSize, int ySize) {
		this.xSize = xSize;
		this.ySize = ySize;
		values = new ArrayList<>(xSize*ySize);
                for(int y=0; y<ySize; y++)
                    for(int x=0; x<xSize; x++)
                        values.add(0d);
	}
	
	public void set(int x, int y, double val) {
		values.set(y*xSize+x, val);
	}
	public void set(int index, double val) {
		values.set(index, val);
	}
	
	public double get(int x, int y) {
		return values.get(y*xSize+x);
	}
	
	public int xSize() {
		return xSize;
	}

	public int ySize() {
		return ySize;
	}
        
        public List<Double> getAll(){
            return values;
        }
}
