package model.battlefield.map.atlas;

import geometry.collections.Map2D;

public class AtlasLayer extends Map2D<Double> {

	public AtlasLayer(int xSize, int ySize) {
		super(xSize, ySize, 0d);
	}
	
	public double addAndReturnExcess(int x, int y, double toAdd){
		double excess = 0;
		double newVal = get(x, y) + toAdd;
		if (newVal > 255) {
			excess = newVal - 255;
			newVal = 255;
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
}
