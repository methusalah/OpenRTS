/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.map.atlas;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import tools.LogUtil;

/**
 *
 * @author Benoît
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
        int firstMapIndex = index*4;
        for(int x=0; x<width; x++)
            for(int y=0; y<height; y++){
                int r = (int)Math.round(layers.get(firstMapIndex).get(x, y)) << 24;
                int g = (int)Math.round(layers.get(firstMapIndex+1).get(x, y)) << 16;
                int b = (int)Math.round(layers.get(firstMapIndex+2).get(x, y)) << 8;
                int a = (int)Math.round(layers.get(firstMapIndex+3).get(x, y));
                res.asIntBuffer().put(y*width+x, r+g+b+a);
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

}
