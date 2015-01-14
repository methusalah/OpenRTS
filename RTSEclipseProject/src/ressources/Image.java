package ressources;

import java.awt.Color;

public class Image {

	private Color[][] data;
	public int width;
	public int height;
	
	public Image(int width, int height){
		data = new Color[width][height];
		this.width = width;
		this.height = height;
	}
	
	public void set(int x, int y, Color c) {
		data[x][y] = c;
	}
	
	public Color get(int x, int y){
		return data[x][y];
	}
}
