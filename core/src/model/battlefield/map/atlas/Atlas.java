package model.battlefield.map.atlas;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Stores and manage layers of texture to paint on the ground.
 * 
 * Atlas itself doesn't know the textures, and provides only alpha channels used
 * by the view to draw and blend textures on a multiple material.
 * 
 * This class contains also methods for serialization/deserialization by Byte, has the data may
 * be huge in a more common XML format.
 * 
 */
@Root
public class Atlas {
    private static int LAYER_COUNT = 8;
    private static int RESOLUTION_RATIO = 8;
    
    @Element
    private int mapWidth, mapHeight;

    public int width, height;
    public List<DoubleMap> layers = new ArrayList<>();
    List<ByteBuffer> buffers = new ArrayList<>();
    
    public boolean toUpdate = false;

    public Atlas(@Element(name="mapWidth")int mapWidth, @Element(name="mapHeight")int mapHeight) {
    	this.mapWidth = mapWidth;
    	this.mapHeight = mapHeight;
    	
    	
        width = mapWidth*RESOLUTION_RATIO;
        height = mapHeight*RESOLUTION_RATIO;
    }
    
    @Override
	public void finalize(){
        for(int i=0; i<LAYER_COUNT; i++){
            DoubleMap layer = new DoubleMap(width, height);
            for(int x=0; x<width; x++)
                for(int y=0; y<height; y++)
                    if(i == 0)
                        layer.set(x, y, 255d);
                    else
                        layer.set(x, y, 0d);
            layers.add(layer);
        }
        buffers.add(buildBuffer(0));
        buffers.add(buildBuffer(1));
    }

    private ByteBuffer buildBuffer(int index){
        ByteBuffer res = ByteBuffer.allocateDirect(width*height*4);
        int firstLayerIndex = index*4;
        for(int x=0; x<width; x++)
            for(int y=0; y<height; y++){
                res.asIntBuffer().put(y*width+x, getBufferVal(x, y, firstLayerIndex));
            }
        return res;
    }
    
    public ByteBuffer getBuffer(int index){
        return buffers.get(index);
    }
    
    
    public void saveToFile(String fileName){
        byte[] bytes = new byte[width*height*LAYER_COUNT];
        int index = 0;
        for(DoubleMap l : layers)
            for(Double d : l.getAll()){
                int i = (int)Math.floor(d-128);
                bytes[index++] = (byte)i;
            }
        try {
            FileOutputStream fos = new FileOutputStream(fileName+"atlas");
            fos.write(bytes);
            fos.close();
        } catch (IOException e){
            System.out.println("IOException : " + e);
        }
    }
    
    public void loadFromFile(String fileName){
        byte[] bytes = new byte[width*height*LAYER_COUNT];
        try {
            FileInputStream fis = new FileInputStream(fileName+"atlas");
            fis.read(bytes, 0, width*height*LAYER_COUNT);
            fis.close();
        } catch (IOException e){
            System.out.println("IOException : " + e);
        }
        int index = 0;
        layers.clear();
        for(int i=0; i<LAYER_COUNT; i++){
            DoubleMap l = new DoubleMap(width, height);
            for(int xy=0; xy<width*height; xy++)
                l.set(xy, (double)bytes[index++]+128);
            layers.add(l);
        }
        buffers.clear();
        buffers.add(buildBuffer(0));
        buffers.add(buildBuffer(1));
        toUpdate = true;               
    }
    
	public void updatePixel(int x, int y) {
		for (int i = 0; i < buffers.size(); i++) {
	        int firstLayerIndex = i*4;
			buffers.get(i).asIntBuffer().put(y*width+x, getBufferVal(x, y, firstLayerIndex));
		}
		toUpdate = true;
	}
	
	private int getBufferVal(int x, int y, int firstLayerIndex){
		int r = (int) Math.round(layers.get(firstLayerIndex).get(x, y)) << 24;
		int g = (int) Math.round(layers.get(firstLayerIndex + 1).get(x, y)) << 16;
		int b = (int) Math.round(layers.get(firstLayerIndex + 2).get(x, y)) << 8;
		int a = (int) Math.round(layers.get(firstLayerIndex + 3).get(x, y));
		return(r + g + b + a);
	}


}
