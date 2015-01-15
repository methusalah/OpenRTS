package model.battlefield.map.cliff;

import model.battlefield.map.Trinket;

import java.util.ArrayList;
import java.util.List;

import math.Angle;
import model.battlefield.map.Tile;
import model.battlefield.map.cliff.faces.natural.NaturalFace;
import model.battlefield.map.cliff.CliffOrganizer;
import static model.battlefield.map.Tile.STAGE_HEIGHT;
import model.battlefield.map.cliff.faces.natural.Dug1Corner;
import model.battlefield.map.cliff.faces.Face;
import model.battlefield.map.cliff.faces.manmade.ManmadeFace;
import model.battlefield.map.cliff.faces.natural.Dug1Ortho;
import model.battlefield.map.cliff.faces.natural.Dug1Salient;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import tools.LogUtil;

public class Cliff {
    public enum Type{Orthogonal, Salient, Corner, Border, Bugged}
    
    public Type type;
    
    public Face face;
    public List<Trinket> trinkets = new ArrayList<>();

    public Tile tile;
    public Tile parent;
    public Tile child;
    public double angle = 0;
    
    public Cliff(Tile t) {
        this.tile = t;
    }
    
    public void connect(){
        CliffOrganizer.organize(this);
    }
    
    public String getConnexionConfiguration(){
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
    
    public void link(Tile parent, Tile child){
    	this.parent = parent;
    	if(parent != null)
    		parent.cliff.child = tile;
    	
    	this.child = child;
    	if(child != null)
    		child.cliff.parent = tile;
    }
    
    public ArrayList<Tile> getUpperGrounds(){
        ArrayList<Tile> res = new ArrayList<>();
        for(Tile n : tile.get8Neighbors())
            if(n.level>tile.level)
                res.add(n);
        return res;
    }
    
    public void removeFromBattlefield(){
    	for(Trinket t : trinkets)
    		t.removeFromBattlefield();
    	if(parent != null)
    		parent.cliff.child = null;
    	if(child != null)
    		child.cliff.parent = null;
    }
    
}
