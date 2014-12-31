/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.editor.tools;

import java.util.ArrayList;
import math.MyRandom;
import model.map.Tile;
import model.editor.ToolManager;
import model.editor.Pencil;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class HeightTool extends EditorTool {
    enum Operation {RaiseLow, NoiseSmooth, UniformReset}
    
    Operation actualOp = Operation.RaiseLow;

    double amplitude = 0.2;
    double maintainedElevation;
    
    public HeightTool(ToolManager manager, Pencil selector) {
        super(manager, selector);
    }

    @Override
    public void primaryAction() {
        ArrayList<Tile> group = pencil.getTiles();
        switch (actualOp){
            case RaiseLow : raise(group); break;
            case NoiseSmooth : noise(group); break;
            case UniformReset : uniform(group); break;
        }
        manager.updateParcels(group);
    }

    @Override
    public void secondaryAction() {
        ArrayList<Tile> group = pencil.getTiles();
        switch (actualOp){
            case RaiseLow : low(group); break;
            case NoiseSmooth : smooth(group); break;
            case UniformReset : reset(group); break;
        }
        manager.updateParcels(group);
    }
    
    private void raise(ArrayList<Tile> tiles){
        for(Tile t : tiles)
            t.elevation += amplitude*pencil.getApplicationRatio(t.getPos2D());
    }
    
    private void low(ArrayList<Tile> tiles){
        for(Tile t : tiles)
            t.elevation -= amplitude*pencil.getApplicationRatio(t.getPos2D());
    }
    
    private void uniform(ArrayList<Tile> tiles){
        if(!pencil.maintained){
            pencil.maintain();
            maintainedElevation = manager.encounter.map.getGroundAltitude(pencil.getPos());
        }
        for(Tile t : tiles){
            double diff = maintainedElevation-t.elevation;
            double attenuatedAmplitude = amplitude*pencil.getApplicationRatio(t.getPos2D());
            if(diff > 0)
                t.elevation += Math.min(diff, attenuatedAmplitude);
            else if(diff < 0)
                t.elevation += Math.max(diff, -attenuatedAmplitude);
        }
    }
    private void noise(ArrayList<Tile> tiles){
        for(Tile t : tiles){
            t.elevation += amplitude*MyRandom.between(-1.0, 1.0)*pencil.getApplicationRatio(t.getPos2D());
        }
    }

    private void smooth(ArrayList<Tile> tiles){
        for(Tile t : tiles){
            double average = 0;
            for(Tile n : t.get4Neighbors())
                average += n.elevation;
            average /= t.get4Neighbors().size();
            
            double diff = average-t.elevation;
            double attenuatedAmplitude = amplitude*pencil.getApplicationRatio(t.getPos2D());
            if(diff > 0)
                t.elevation += Math.min(diff, attenuatedAmplitude);
            else if(diff < 0)
                t.elevation += Math.max(diff, -attenuatedAmplitude);
        }
    }
    
    private void reset(ArrayList<Tile> tiles){
        for(Tile t : tiles)
            t.elevation = 0;
    }

    @Override
    public void toggleSet() {
        LogUtil.logger.info("Height tool has no set.");
    }

    @Override
    public void toggleOperation() {
        switch (actualOp){
            case RaiseLow :
                actualOp = Operation.NoiseSmooth;
                LogUtil.logger.info("Atlas tool operation toggled to noise/smooth.");
                break;
            case NoiseSmooth :
                actualOp = Operation.UniformReset;
                LogUtil.logger.info("Atlas tool operation toggled to uniform/reset.");
                break;
            case UniformReset :
                actualOp = Operation.RaiseLow;
                LogUtil.logger.info("Atlas tool operation toggled to raise/low.");
                break;
        }
    }
    
    
    
    
    
    
}
