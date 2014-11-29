package model.map.cliff;

import java.util.ArrayList;
import math.Angle;
import model.map.Tile;
import model.map.cliff.faces.NaturalFace;
import model.map.cliff.CliffOrganizer;
import static model.map.Tile.STAGE_HEIGHT;
import model.map.TileDef;
import model.map.cliff.faces.CornerNaturalFace;
import model.map.cliff.faces.ManmadeFace;
import model.map.cliff.faces.OrthogonalNaturalFace;
import model.map.cliff.faces.SalientNaturalFace;
import tools.LogUtil;

public class Cliff {
    public enum Type{Orthogonal, Salient, Corner, Border}
    
    public Tile tile;
    public Tile parent;
    public Tile child;
    public double angle = 0;
    public Type type;

    public NaturalFace naturalFace;
    public ManmadeFace manmadeFace;
    public ArrayList<Trinket> trinkets;
    
    public Cliff(Tile t) {
        this.tile = t;
    }
    
    public void correctGroundZ(){
        if(tile.w!=null && tile.w.level > tile.level ||
                tile.s!=null && tile.s.level > tile.level ||
                tile.w!=null && tile.w.s!=null && tile.w.s.level > tile.level)
            tile.z = (tile.level+1)*STAGE_HEIGHT;
    }

    public void connect(){
        correctGroundZ();
        CliffOrganizer.organize(this);
    }
    
    public void buildFace(){
        switch (type){
            case Orthogonal : naturalFace = new OrthogonalNaturalFace(this); break;
            case Salient : naturalFace = new SalientNaturalFace(this); break;
            case Corner : naturalFace = new CornerNaturalFace(this); break;
        }
    }
    
    public String getConnectedCliffs(){
        String res = new String();
        if(isNeighborCliff(tile.n))
            res = res.concat("n");
        if(isNeighborCliff(tile.s))
            res = res.concat("s");
        if(isNeighborCliff(tile.e))
            res = res.concat("e");
        if(isNeighborCliff(tile.w))
            res = res.concat("w");
        return res;
    }
    
    private boolean isNeighborCliff(Tile t){
        return t != null && t.level == tile.level && t.isCliff();
    }
    
    public void setParent(Cliff o){
        parent = o.tile;
        o.child = this.tile;
    }
    
    
}
