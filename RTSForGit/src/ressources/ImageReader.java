package ressources;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import tools.LogUtil;

public class ImageReader {
	
	static public Image read(String path) {
		return convertToColorArray(getImageFromFile(path));
	}
	
	private static BufferedImage getImageFromFile(String path) {
		try {
			return ImageIO.read(new File(path));
		} catch (IOException e) {
			LogUtil.logger.info("Image not found : "+path);
			return null;
		}
	}
	
	private static Image convertToColorArray(BufferedImage bi) {
		byte[] pixels = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		int width = bi.getWidth();
	    int height = bi.getHeight();
	    boolean hasAlphaChannel = bi.getAlphaRaster() != null;

	    Image res = new Image(width, height);
	    if (hasAlphaChannel) {
	    	final int pixelLength = 4;
	    	for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                    int alpha = ((int) pixels[pixel] & 0xff); // alpha
	            int blue =  ((int) pixels[pixel + 1] & 0xff); // blue
	            int green = ((int) pixels[pixel + 2] & 0xff); // green
	            int red =   ((int) pixels[pixel + 3] & 0xff); // red
	            res.set(col, row, new Color(red, green, blue, alpha));
	            col++;
	            if (col == width) {
	               col = 0;
	               row++;
	            }
	    	}
	    } else {
	         final int pixelLength = 3;
	         for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
	            int alpha = 255; // 255 alpha
	            int blue =  ((int) pixels[pixel] & 0xff); // blue
	            int green = ((int) pixels[pixel + 1] & 0xff); // green
	            int red =   ((int) pixels[pixel + 2] & 0xff); // red
	            res.set(col, row, new Color(red, green, blue, alpha));
	            col++;
	            if (col == width) {
	               col = 0;
	               row++;
	            }
	         }
	      }

	      return res;
	}



}
