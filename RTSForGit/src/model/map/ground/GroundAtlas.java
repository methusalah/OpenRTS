/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.ground;

import collections.Map2D;
import de.lessvoid.nifty.loaderv2.types.ImageType;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
@Root
public class GroundAtlas {
    private static int LAYER_COUNT = 12;
    
    @ElementList
    public List<Map2D<Double>> layers = new ArrayList<>();
    
    @Element
    public int width, height;
    
    @ElementList
    List<ByteBuffer> buffers = new ArrayList<>();
    
    public boolean toUpdate = false;

    public GroundAtlas(int width, int height) {
        this.width = width;
        this.height = height;
                
        for(int i=0; i<LAYER_COUNT; i++){
            Map2D<Double> layer = new Map2D(width, height);
            for(int x=0; x<width; x++)
                for(int y=0; y<width; y++)
                    if(i == 0)
                        layer.set(x, y, 255d);
                    else
                        layer.set(x, y, 0d);
            layers.add(layer);
        }
        buffers.add(buildBuffer(0));
        buffers.add(buildBuffer(1));
        buffers.add(buildBuffer(2));
    }
    
    private ByteBuffer buildBuffer(int index){
        ByteBuffer res = ByteBuffer.allocateDirect(width*height*4);
        int firstMapIndex = index*4;
        for(int x=0; x<width; x++)
            for(int y=0; y<width; y++){
                int r = (int)Math.round(layers.get(firstMapIndex).get(x, y)) << 24;
                int g = (int)Math.round(layers.get(firstMapIndex+1).get(x, y)) << 16;
                int b = (int)Math.round(layers.get(firstMapIndex+2).get(x, y)) << 8;
                int a = (int)Math.round(layers.get(firstMapIndex+3).get(x, y));
//                res.asIntBuffer().put(y*width+x, new Color(r, g, b, a).getRGB());
                res.asIntBuffer().put(y*width+x, r+g+b+a);
            }
        return res;
    }
    
    public ByteBuffer getBuffer(int index){
        return buffers.get(index);
    }
    
    
}
