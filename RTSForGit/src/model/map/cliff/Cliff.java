package model.map.cliff;

import math.Angle;
import model.map.Tile;
import model.map.cliff.CliffShape;
import model.map.cliff.CliffShapeFactory;
import static model.map.Tile.STAGE_HEIGHT;
import model.map.TileDef;

public class Cliff extends Tile {
    
    public CliffShape shape;
    public Cliff parent;

    public Cliff(TileDef def) {
        super(def);
        if(!def.cliff)
            throw new IllegalArgumentException("Trying to create a cliff with a non cliff definition.");
    }
    
    public void correctGroundZ(){
        if(w!=null && w.level > level ||
                s!=null && s.level > level ||
                w!=null && w.s!=null && w.s.level > level)
            z = (level+1)*STAGE_HEIGHT;
    }

    @Override
    public boolean isBlocked() {
        return true;
    }

    @Override
    public boolean isCliff() {
        return true;
    }
    
    
    public void drawShape(){
        shape = CliffShapeFactory.createShape(this);
    }
    
    public String getConnectedCliffs(){
        String res = new String();
        if(n != null && n.isCliff())
            res = res.concat("n");
        if(s != null && s.isCliff())
            res = res.concat("s");
        if(e != null && e.isCliff())
            res = res.concat("e");
        if(w != null && w.isCliff())
            res = res.concat("w");
        return res;
    }
}
