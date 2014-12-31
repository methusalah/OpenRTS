/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.editor.tools;

import model.editor.ToolManager;
import model.editor.Pencil;

/**
 *
 * @author Benoît
 */
public abstract class EditorTool {
    
    ToolManager manager;
    Pencil pencil;

    public EditorTool(ToolManager manager, Pencil selector) {
        this.manager = manager;
        this.pencil = selector;
    }
    
    public abstract void primaryAction();
    public abstract void secondaryAction();
    public abstract void toggleSet();
    public abstract void toggleOperation();
    
    public boolean isAnalog(){
        return true;
    }
}
