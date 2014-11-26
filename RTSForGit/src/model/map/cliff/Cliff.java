package model.map.cliff;

import math.Angle;
import model.map.Tile;
import model.map.cliff.faces.NaturalFace;
import model.map.cliff.CliffOrganizer;
import static model.map.Tile.STAGE_HEIGHT;
import model.map.TileDef;
import model.map.cliff.faces.ManmadeFace;
import tools.LogUtil;

public class Cliff extends Tile {
    public enum Type{Orthogonal, Salient, Corner}
    
    public Cliff parent;

    public NaturalFace naturalFace;
    public ManmadeFace manmadeFace;
    public boolean urban;
    
    public Type type;

    public Cliff(TileDef def) {
        super(def);
        if(!def.cliff)
            throw new IllegalArgumentException("Trying to create a cliff with a non cliff definition.");
        urban = def.urban;
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
        naturalFace = CliffOrganizer.createShape(this);
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
