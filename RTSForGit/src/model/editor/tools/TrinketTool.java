/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.editor.tools;

import geometry.Point2D;
import geometry3D.Point3D;
import java.awt.Color;
import java.util.ArrayList;
import math.Angle;
import math.MyRandom;
import model.battlefield.army.components.Unit;
import model.builders.UnitBuilder;
import model.editor.ToolManager;
import model.editor.Pencil;
import model.editor.Set;
import model.battlefield.map.Trinket;
import model.builders.TrinketBuilder;
import model.battlefield.warfare.Faction;
import tools.LogUtil;

/**
 *
 * @author bedu
 */
public class TrinketTool extends Tool{
    private static final String ADD_REMOVE_OP = "add/remove";
    private static final String MOVE_ROTATE_OP = "move/rotate";
    
    ArrayList<TrinketBuilder> builders;
    Trinket actualTrinket;
    Point2D moveOffset;
    boolean analog = false;
    
    double angle = 0;
    
    public TrinketTool(ToolManager manager, ArrayList<TrinketBuilder> builders) {
        super(manager, ADD_REMOVE_OP, MOVE_ROTATE_OP);
        this.builders = new ArrayList<>();
        ArrayList<String> builderIDs = new ArrayList<>();
        for(TrinketBuilder b : builders)
            if(b.editable){
                this.builders.add(b);
                builderIDs.add(b.id);
            }
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
        for(Trinket t : manager.battlefield.map.trinkets)
            if(t.pos.equals(pos))
                pos = pos.getTranslation(MyRandom.between(Angle.FLAT, -Angle.FLAT), 0.1);
        
        Trinket t = builders.get(set.actual).build(pos.get3D(manager.battlefield.map.getGroundAltitude(pos)));
        manager.battlefield.map.trinkets.add(t);
        ArrayList<Trinket> toUpdate = new ArrayList<>();
        toUpdate.add(t);
        manager.updateTrinkets(toUpdate);
    }
    private void remove(){
        Trinket toRemove = null;
        if(isValid(manager.pointedSpatialLabel))
            for(Trinket t : manager.battlefield.map.trinkets)
                if(t.label.matches(manager.pointedSpatialLabel)){
                    toRemove = t;
                    break;
                }
        if(toRemove != null){
            manager.battlefield.map.trinkets.add(toRemove);
            ArrayList<Trinket> toUpdate = new ArrayList<>();
            toUpdate.add(toRemove);
            manager.deleteTrinkets(toUpdate);            
        }
    }
    private void move(){
        if(!pencil.maintained){
            pencil.maintain();
            actualTrinket = null;
            if(isValid(manager.pointedSpatialLabel))
                for(Trinket t : manager.battlefield.map.trinkets)
                    if(t.label.matches(manager.pointedSpatialLabel)){
                        actualTrinket = t;
                        moveOffset = pencil.getPos().getSubtraction(t.pos.get2D());
                        break;
                    }
        }
        if(actualTrinket != null){
            // TODO attention, l'elevation n'est pas forcement juste avec ce calcul
            double elevation = actualTrinket.pos.z-manager.battlefield.map.getGroundAltitude(actualTrinket.pos.get2D());
            Point2D newPos = pencil.getPos().getSubtraction(moveOffset);
            double z = manager.battlefield.map.getGroundAltitude(newPos)+elevation;
            actualTrinket.pos = newPos.get3D(z);
            ArrayList<Trinket> toUpdate = new ArrayList<>();
            toUpdate.add(actualTrinket);
            manager.updateTrinkets(toUpdate);            
        }
    }
    private void rotate(){
        if(!pencil.maintained){
            pencil.maintain();
            actualTrinket = null;
            if(isValid(manager.pointedSpatialLabel))
                for(Trinket t : manager.battlefield.map.trinkets)
                    if(t.label.matches(manager.pointedSpatialLabel)){
                        actualTrinket = t;
                        break;
                    }
        }
        if(actualTrinket != null){
            actualTrinket.rotZ = pencil.getPos().getSubtraction(actualTrinket.pos.get2D()).getAngle();
            ArrayList<Trinket> toUpdate = new ArrayList<>();
            toUpdate.add(actualTrinket);
            manager.updateTrinkets(toUpdate);            
        }
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
