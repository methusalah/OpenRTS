package model.battlefield.map.atlas;

import geometry.collections.Map2D;
import geometry.geom2d.Point2D;

import java.util.List;

public class AtlasLayer {
	public static double MAX_VALUE = 255;
	
	
	Map2D<Byte> values;

	public AtlasLayer(int xSize, int ySize) {
		this(xSize, ySize, 0);
	}

	public AtlasLayer(int xSize, int ySize, double val) {
//		values = new Map2D<Byte>(xSize, ySize, (byte)(val*255-128));
		values = new Map2D<Byte>(xSize, ySize, (byte)(val-128));
	}

	public double addAndReturnExcess(int x, int y, double toAdd){
		double excess = 0;
		double newVal = get(x, y) + toAdd;
		if (newVal > MAX_VALUE) {
			excess = newVal - MAX_VALUE;
			newVal = MAX_VALUE;
		}
		set(x, y, newVal);
		return excess;
	}

	public double withdrawAndReturnExcess(int x, int y, double toWithdraw) {
		double excess = 0;
		double newVal = get(x, y) - toWithdraw;
		if (newVal < 0) {
			excess = -newVal;
			newVal = 0;
		}
		set(x, y, newVal);
		return excess;
	}

	public double get(int x, int y){
		return ((double)values.get(x, y)+128);
	}

	public double getInAtlasSpace(Point2D mapCoord){
		mapCoord = mapCoord.getMult(Atlas.RESOLUTION_RATIO);
		return get((int)Math.round(mapCoord.x), (int)Math.round(mapCoord.y));
	}
	
	public void set(int x, int y, double val){
		values.set(x, y, (byte)(val-128));
	}
	
	public List<Byte> getBytes(){
		return values.getAll();
	}
	
	public void setByte(int i, byte val){
		values.set(i, val);
	}
	
}
