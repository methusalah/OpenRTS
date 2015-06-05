package model.battlefield.map.atlas;

import geometry.collections.Map2D;

import java.util.List;

import com.jme3.texture.image.ImageRaster;

public class AtlasLayer {

	Map2D<Byte> values;
	Map2D<Short> alphaMask;
	public ImageRaster mask;
	public double maskScale;

	public AtlasLayer(int xSize, int ySize) {
		this(xSize, ySize, 0);
	}

	public AtlasLayer(int xSize, int ySize, double val) {
		values = new Map2D<Byte>(xSize, ySize, (byte)(val*255-128));
	}

	public double addAndReturnExcess(int x, int y, double toAdd){
		double excess = 0;
		double newVal = get(x, y) + toAdd;
		double maskVal = mask.getPixel(
				(x*mask.getWidth()*(int)maskScale/values.xSize())%(mask.getWidth()),
				(y*mask.getHeight()*(int)maskScale/values.ySize())%(mask.getHeight())).a;
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

	public void setMask(int width, int height, List<Short> data, int scale){
		alphaMask = new Map2D<>(width, height);
		for(int i = 0; i < width*height; i++) {
			alphaMask.set(i, data.get(i));
		}
		maskScale = scale;
	}
	
	public double get(int x, int y){
		return ((double)values.get(x, y)+128)/255;
		
	}
	
	public void set(int x, int y, double val){
		values.set(x, y, (byte)(val*255-128));
	}
	
	public List<Byte> getBytes(){
		return values.getAll();
	}
	
	public void setByte(int i, byte val){
		values.set(i, val);
	}
	
}
