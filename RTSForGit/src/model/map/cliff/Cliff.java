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
    public enum Type{Orthogonal, Salient, Corner, Border, Bugged}
    
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
    
    public void connect(){
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
        if(t == null ||
                !t.isCliff() ||
                t.level != tile.level ||
                t.cliff.type == Type.Bugged)
            return false;
        
        for(Tile n1 : getUpperGrounds())
            for(Tile n2 : t.cliff.getUpperGrounds())
                if(n1 == n2)
                    return true;
        return false;
    }
    
    public void setParent(Cliff o){
        parent = o.tile;
        o.child = this.tile;
    }
    
    public ArrayList<Tile> getUpperGrounds(){
        ArrayList<Tile> res = new ArrayList<>();
        for(Tile n : tile.get8Neighbors())
            if(n.level>tile.level)
                res.add(n);
        return res;
        
    }
    
}
