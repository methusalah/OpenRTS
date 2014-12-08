/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.editor.tools;

import model.map.editor.MapToolManager;
import model.map.editor.TileSelector;

/**
 *
 * @author Benoît
 */
public class MapTool {
    
    MapToolManager manager;
    TileSelector selector;

    public MapTool(MapToolManager manager, TileSelector selector) {
        this.manager = manager;
        this.selector = selector;
    }
    
    public void primaryAction(){
        throw new RuntimeException("Can't be called from mother class.");
    }
    public void secondaryAction(){
        throw new RuntimeException("Can't be called from mother class.");
    }
    
}
