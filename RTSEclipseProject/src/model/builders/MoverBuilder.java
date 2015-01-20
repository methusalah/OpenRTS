/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders;

import geometry.Point2D;
import geometry3D.Point3D;
import ressources.definitions.DefElement;

import java.util.HashMap;

import model.battlefield.abstractComps.Hiker;
import model.battlefield.army.components.Mover;
import model.battlefield.army.components.Mover.StandingMode;
import ressources.definitions.Definition;
import model.battlefield.map.Map;
import ressources.definitions.BuilderLibrary;

/**
 *
 * @author Benoît
 */
public class MoverBuilder extends Builder{
    private static final String PATHFINDING_MODE = "PathfindingMode";
    private static final String HEIGHTMAP = "Heightmap";
    private static final String STANDING_MODE = "StandingMode";
    
    private static final String FLY = "Fly";
    private static final String WALK = "Walk";
    
    private static final String SKY = "Sky";
    private static final String GROUND = "Ground";

    private static final String STAND = "Stand";
    private static final String PRONE = "Prone";

    private Mover.PathfindingMode pathfindingMode;
    private Mover.Heightmap heightmap;
    private Mover.StandingMode standingMode = StandingMode.STAND;
    
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
                        case GROUND : heightmap = Mover.Heightmap.GROUND; break;
                    }
                    break;
                case STANDING_MODE :
                    switch (de.getVal()){
                        case STAND : standingMode = Mover.StandingMode.STAND; break;
                        case PRONE : standingMode = Mover.StandingMode.PRONE; break;
                    }
                    break;
            }
    }
    
    public Mover build(Hiker movable){
        Mover res = new Mover(heightmap, pathfindingMode, standingMode, movable, lib.battlefield.map);
        return res;
    }

    @Override
    public void readFinalizedLibrary() {
    }
}
