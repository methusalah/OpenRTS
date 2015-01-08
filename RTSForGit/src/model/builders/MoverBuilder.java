/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders;

import geometry.Point2D;
import geometry3D.Point3D;
import ressources.definitions.DefElement;
import java.util.HashMap;
import model.battlefield.army.components.Movable;
import model.battlefield.army.components.Mover;
import ressources.definitions.Definition;
import model.battlefield.map.Map;
import ressources.definitions.BuilderLibrary;

/**
 *
 * @author Benoît
 */
public class MoverBuilder extends Builder{
    static final String PATHFINDING_MODE = "PathfindingMode";
    static final String HEIGHTMAP = "Heightmap";
    
    static final String FLY = "Fly";
    static final String WALK = "Walk";
    static final String SKY = "Sky";
    static final String AIR = "Air";
    static final String GROUND = "Ground";

    private Mover.PathfindingMode pathfindingMode;
    private Mover.Heightmap heightmap;
    
    public MoverBuilder(Definition def, BuilderLibrary lib){
        super(def, lib);
        for(DefElement de : def.elements)
            switch(de.name){
                case PATHFINDING_MODE :
                    switch (de.getVal()){
                        case FLY : pathfindingMode = Mover.PathfindingMode.FLY; break;
                        case WALK : pathfindingMode = Mover.PathfindingMode.WALK; break;
                    }
                    break;
                case HEIGHTMAP :
                    switch (de.getVal()){
                        case SKY : heightmap = Mover.Heightmap.SKY; break;
                        case AIR : heightmap = Mover.Heightmap.AIR; break;
                        case GROUND : heightmap = Mover.Heightmap.GROUND; break;
                    }
                    break;
            }
    }
    
    public Mover build(Movable movable, Point3D pos){
        Mover res = new Mover(heightmap, pathfindingMode, movable, lib.map, pos);
        return res;
    }
}
