/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.editor.tools;

import geometry.Point2D;
import geometry3D.Point3D;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import math.Angle;
import math.MyRandom;
import model.battlefield.Battlefield;
import model.battlefield.army.components.Unit;
import model.battlefield.army.components.UnitPlacement;
import model.builders.UnitBuilder;
import model.editor.ToolManager;
import model.editor.Pencil;
import model.editor.Set;
import model.battlefield.warfare.Faction;
import tools.LogUtil;

/**
 *
 * @author bedu
 */
public class UnitTool extends Tool{
    private static final String ADD_REMOVE_OP = "add/remove";
    private static final String MOVE_ROTATE_OP = "move/rotate";
    
    Unit actualUnit;
    Point2D moveOffset;
    boolean analog = false;
    
    double angle = 0;
    
    public UnitTool(ToolManager manager) {
        super(manager, ADD_REMOVE_OP, MOVE_ROTATE_OP);
        ArrayList<String> builderIDs = new ArrayList<>();
        for(UnitBuilder b : manager.lib.getAllUnitBuilders())
            builderIDs.add(b.getUIName());
        set = new Set(builderIDs, false);
    }
    
    @Override
    protected void createPencil() {
        pencil = new Pencil(manager.battlefield.map);
        pencil.sizeIncrement = 0;
        pencil.strengthIncrement = 0;
        pencil.setUniqueMode();
    }
    

    @Override
    public void primaryAction() {
        switch (actualOp) {
            case ADD_REMOVE_OP : add(); break;
            case MOVE_ROTATE_OP : move(); break;
        }
    }

    @Override
    public void secondaryAction() {
        switch (actualOp) {
            case ADD_REMOVE_OP : remove(); break;
            case MOVE_ROTATE_OP : rotate(); break;
        }
    }
    
    private void add(){
        Point2D coord = pencil.getCoord();
        for(Unit u : manager.battlefield.armyManager.units)
            if(u.getPos2D().equals(coord))
                coord = coord.getTranslation(MyRandom.between(Angle.FLAT, -Angle.FLAT), 0.1);
        Faction f = manager.lib.getAllUnitBuilders().get(set.actual).hasRace("human")?
                manager.battlefield.engagement.factions.get(0) :
                manager.battlefield.engagement.factions.get(1);
        
        UnitPlacement placement = new UnitPlacement(manager.lib.getAllUnitBuilders().get(set.actual).getId(), f.name, coord.get3D(0));
        manager.battlefield.engagement.addUnit(placement);
    }
    private void remove(){
        if(isValid(manager.pointedSpatialLabel))
            for(Unit u : manager.battlefield.armyManager.units)
                if(u.label.matches(manager.pointedSpatialLabel)){
                    manager.battlefield.engagement.removeUnit(u);
                    break;
                }
    }
    private void move(){
        if(!pencil.maintained){
            pencil.maintain();
            actualUnit = null;
            if(isValid(manager.pointedSpatialLabel))
                for(Unit u : manager.battlefield.armyManager.units)
                    if(u.label.matches(manager.pointedSpatialLabel)){
                        actualUnit = u;
                        moveOffset = pencil.getCoord().getSubtraction(u.getPos2D());
                        break;
                    }
        }
        if(actualUnit != null)
            manager.battlefield.engagement.setCoord(actualUnit, pencil.getCoord().getSubtraction(moveOffset));
    }
    private void rotate(){
        if(!pencil.maintained){
            pencil.maintain();
            actualUnit = null;
            if(isValid(manager.pointedSpatialLabel))
                for(Unit u : manager.battlefield.armyManager.units)
                    if(u.label.matches(manager.pointedSpatialLabel)){
                        actualUnit = u;
                        break;
                    }
        }
        if(actualUnit != null)
            manager.battlefield.engagement.setYaw(actualUnit, pencil.getCoord().getSubtraction(actualUnit.getPos2D()).getAngle());
    }
    private boolean isValid(String label){
        return label != null && !label.isEmpty();
    }

    @Override
    public boolean isAnalog() {
        return analog;
    }

    @Override
    public void setOperation(int index) {
        super.setOperation(index);
        if(actualOp.equals(MOVE_ROTATE_OP))
            analog = true;
        else
            analog = false;
    }

    @Override
    public void toggleOperation() {
        super.toggleOperation();
        if(actualOp.equals(MOVE_ROTATE_OP))
            analog = true;
        else
            analog = false;
    }
}
