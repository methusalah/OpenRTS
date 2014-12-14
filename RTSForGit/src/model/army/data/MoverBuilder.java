/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.army.data;

import geometry.Point2D;
import geometry3D.Point3D;
import ressources.definitions.DefElement;
import java.util.HashMap;
import ressources.definitions.Definition;
import model.map.Map;
import ressources.definitions.BuilderLibrary;

/**
 *
 * @author Benoît
 */
public class MoverBuilder {
    static final String PATHFINDING_MODE = "PathfindingMode";
    static final String HEIGHTMAP = "Heightmap";
    
    static final String FLY = "Fly";
    static final String WALK = "Walk";
    static final String SKY = "Sky";
    static final String AIR = "Air";
    static final String GROUND = "Ground";

    Definition def;
    BuilderLibrary lib;

    public MoverBuilder(Definition def, BuilderLibrary lib){
        this.def = def;
        this.lib = lib;
    }
    
    public Mover build(Movable movable, Point3D position){
        Mover res = new Mover(lib.map, movable, position);
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
                        case SKY : res.heightmap = Mover.Heightmap.SKY; break;
                        case AIR : res.heightmap = Mover.Heightmap.AIR; break;
                        case GROUND : res.heightmap = Mover.Heightmap.GROUND; break;
                    }
                    break;
            }
        res.updateElevation();
        return res;
    }
}
