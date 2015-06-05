package model.battlefield.map.atlas;

import java.util.ArrayList;
import java.util.List;

import geometry.collections.Map2D;
import geometry.tools.LogUtil;

import com.jme3.texture.image.ImageRaster;

public class AtlasLayer {

	Map2D<Byte> map;
	
	public ImageRaster mask;
	public double maskScale;
	
	public AtlasLayer(int xSize, int ySize) {
		map = new Map2D<Byte>(xSize, ySize, (byte)-128);
	}
	
	public double addAndReturnExcess(int x, int y, double toAdd){
		double excess = 0;
		double newVal = get(x, y) + toAdd;
		double maskVal = mask.getPixel(
				(x*mask.getWidth()*(int)maskScale/map.xSize())%(mask.getWidth()),
				(y*mask.getHeight()*(int)maskScale/map.ySize())%(mask.getHeight())).a;
		if (newVal > 1*maskVal) {
			excess = newVal - 1*maskVal;
			newVal = 1*maskVal;
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
	
	public void set(int x, int y, Double val) {
		map.set(x, y, (byte) (val*255-128));
	}
	
	public double get(int x, int y) {
		return ((double)map.get(x, y)+128)/255;
	}
	
	public List<Byte> getBytes(){
		return map.getAll();
	}
	
	public void setByte(int index, byte val){
		map.set(index, val);
	}
}
