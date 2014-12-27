/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.editor.tools;

import java.util.ArrayList;
import math.MyRandom;
import model.map.Tile;
import model.map.editor.MapToolManager;
import model.map.editor.Pencil;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class HeightTool extends MapTool {
    enum Operation {RaiseLow, NoiseSmooth, Uniform}
    
    Operation actualOp = Operation.RaiseLow;

    double amplitude = 0.2;
    double maintainedElevation;
    
    public HeightTool(MapToolManager manager, Pencil selector) {
        super(manager, selector);
    }

    @Override
    public void primaryAction() {
        ArrayList<Tile> group = pencil.getTiles();
        switch (actualOp){
            case RaiseLow : raise(group); break;
            case NoiseSmooth : noise(group); break;
            case Uniform : uniform(group); break;
        }
        manager.updateParcels(group);
    }

    @Override
    public void secondaryAction() {
        ArrayList<Tile> group = pencil.getTiles();
        switch (actualOp){
            case RaiseLow : low(group); break;
            case NoiseSmooth : smooth(group); break;
            case Uniform : break;
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
            maintainedElevation = manager.map.getGroundAltitude(pencil.getPos());
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
        double average = 0;
        for(Tile t : tiles)
            average += t.elevation;
        average /= tiles.size();
        
        for(Tile t : tiles){
            double diff = average-t.elevation;
            double attenuatedAmplitude = amplitude*pencil.getApplicationRatio(t.getPos2D());
            if(diff > 0)
                t.elevation += Math.min(diff, attenuatedAmplitude);
            else if(diff < 0)
                t.elevation += Math.max(diff, -attenuatedAmplitude);
        }
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
                actualOp = Operation.Uniform;
                LogUtil.logger.info("Atlas tool operation toggled to uniform.");
                break;
            case Uniform :
                actualOp = Operation.RaiseLow;
                LogUtil.logger.info("Atlas tool operation toggled to raise/low.");
                break;
        }
    }
    
    
    
    
    
    
}
