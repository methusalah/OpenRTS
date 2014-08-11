/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data;

import geometry.Point2D;
import model.army.data.definitions.DefElement;
import java.util.HashMap;
import model.army.data.definitions.Definition;
import model.map.Map;

/**
 *
 * @author Benoît
 */
public class MoverBuilder {
    static final String PATHFINDING_MODE = "PathfindingMode";
    static final String HEIGHTMAP = "Heightmap";
    
    static final String FLY = "Fly";
    static final String WALK = "Walk";
    static final String AIR = "Air";
    static final String GROUND = "Ground";

    Map map;
    Definition def;

    public MoverBuilder(Definition def, Map map){
        this.def = def;
        this.map = map;
    }
    
    public Mover build(Movable movable, Point2D position){
        Mover res = new Mover(map, movable, position);
        for(DefElement de : def.elements)
            switch(de.name){
                case PATHFINDING_MODE :
                    switch (de.getVal()){
                        case FLY : res.pathfindingMode = Mover.PathfindingMode.FLY; break;
                        case WALK : res.pathfindingMode = Mover.PathfindingMode.WALK; break;
                    }
                    break;
                case HEIGHTMAP :
                    switch (de.getVal()){
                        case AIR : res.heightmap = Mover.Heightmap.AIR; break;
                        case GROUND : res.heightmap = Mover.Heightmap.GROUND; break;
                    }
                    break;
            }
        res.updateElevation();
        return res;
    }
}
