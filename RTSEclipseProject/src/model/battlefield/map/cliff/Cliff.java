package model.battlefield.map.cliff;

import model.battlefield.map.Trinket;

import java.util.ArrayList;
import java.util.List;

import model.battlefield.map.Tile;
import model.battlefield.map.cliff.CliffOrganizer;
import model.battlefield.map.cliff.faces.Face;
import tools.LogUtil;

public class Cliff {
    public enum Type{Orthogonal, Salient, Corner, Border, Bugged}
    
    public Type type;
    
    public Face face;
    public List<Trinket> trinkets = new ArrayList<>();

    private final Tile tile;
    public final int level;
    private Tile parentTile;
    private Tile childTile;
    public double angle = 0;
    
    public Cliff(Tile t, int level) {
        this.tile = t;
        this.level = level;
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
                !t.hasCliffOnLevel(level) ||
//                t.level != tile.level ||
                t.getCliff(level).type == Type.Bugged)
            return false;
        
        for(Tile n1 : getUpperGrounds())
            for(Tile n2 : t.getCliff(level).getUpperGrounds())
                if(n1 == n2)
                    return true;
        return false;
    }
    
    public void link(Tile parent, Tile child){
    	this.parentTile = parent;
    	if(parent != null)
    		getParent().childTile = tile;
    	
    	this.childTile = child;
    	if(child != null)
    		getChild().parentTile = tile;
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
    	if(parentTile != null && parentTile.getCliff(level) != null)
    		getParent().childTile = null;
    	if(childTile != null && childTile.getCliff(level) != null)
    		getChild().parentTile = null;
    }
    
    public Cliff getParent(){
    	return parentTile.getCliff(level);
    }
    
    public Cliff getChild(){
    	return childTile.getCliff(level);
    }
    
    public boolean hasParent(){
    	return parentTile != null;
    }
    
    public boolean hasChild(){
    	return childTile != null;
    }
    
    public Tile getTile(){
    	return tile;
    }
    
}
