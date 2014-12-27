/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.editor.tools;

import model.map.editor.MapToolManager;
import model.map.editor.Pencil;

/**
 *
 * @author Benoît
 */
public abstract class MapTool {
    
    MapToolManager manager;
    Pencil pencil;

    public MapTool(MapToolManager manager, Pencil selector) {
        this.manager = manager;
        this.pencil = selector;
    }
    
    public abstract void primaryAction();
    public abstract void secondaryAction();
    public abstract void toggleSet();
    public abstract void toggleOperation();
}
