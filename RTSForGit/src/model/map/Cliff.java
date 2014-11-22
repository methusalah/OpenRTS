package model.map;

import model.map.CliffShape.CliffShape;
import model.map.CliffShape.CliffShapeFactory;
import geometry.Point2D;
import geometry3D.Point3D;
import java.util.ArrayList;
import math.Angle;
import math.MyRandom;
import static model.map.Tile.STAGE_HEIGHT;
import tools.LogUtil;

public class Cliff extends Tile {
    
    public CliffShape shape;
    public Cliff parent;

    public Cliff(Tile t) {
        super(t);
        if(n!=null)
            n.s = this;
        if(s!=null)
            s.n = this;
        if(e!=null)
            e.w = this;
        if(w!=null)
            w.e = this;

        
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
        shape = CliffShapeFactory.getSpecialised(this);
    }
}
