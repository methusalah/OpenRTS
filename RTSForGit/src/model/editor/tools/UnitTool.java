/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.editor.tools;

import geometry.Point2D;
import java.awt.Color;
import java.util.ArrayList;
import math.Angle;
import math.MyRandom;
import model.army.data.Unit;
import model.army.data.UnitBuilder;
import model.editor.ToolManager;
import model.editor.Pencil;
import model.editor.Set;
import model.warfare.Faction;
import tools.LogUtil;

/**
 *
 * @author bedu
 */
public class UnitTool extends Tool{
    private static final String ADD_REMOVE_OP = "add/remove";
    private static final String MOVE_ROTATE_OP = "move/rotate";
    
    ArrayList<UnitBuilder> builders;
    Unit actualUnit;
    boolean analog = false;
    
    double angle = 0;
    
    Faction f1 = new Faction(Color.RED);
    Faction f2 = new Faction(Color.GREEN);

    public UnitTool(ToolManager manager, ArrayList<UnitBuilder> builders) {
        super(manager, ADD_REMOVE_OP, MOVE_ROTATE_OP);
        this.builders = builders;
        f1.setEnnemy(f2);
        ArrayList<String> builderIDs = new ArrayList<>();
        for(UnitBuilder b : builders)
            builderIDs.add(b.id);
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
        Point2D pos = pencil.getPos();
        for(Unit u : manager.battlefield.armyManager.units)
            if(u.getPos2D().equals(pos))
                pos = pos.getTranslation(MyRandom.between(Angle.FLAT, -Angle.FLAT), 0.1);
        Faction f = builders.get(set.actual).race.equals("human")? f1 : f2;
        builders.get(set.actual).build(f, pos);
    }
    private void remove(){
        Unit toRemove = null;
        if(isValid(manager.pointedSpatialLabel))
            for(Unit u : manager.battlefield.armyManager.units)
                if(u.label.matches(manager.pointedSpatialLabel)){
                    toRemove = u;
                    break;
                }
        if(toRemove != null)
            toRemove.removeFromBattlefield();
    }
    private void move(){
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
            actualUnit.mover.setPosition(pencil.getPos());
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
            actualUnit.mover.yaw = pencil.getPos().getSubtraction(actualUnit.getPos2D()).getAngle();
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
