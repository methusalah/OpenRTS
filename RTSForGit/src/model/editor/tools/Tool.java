/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.editor.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import model.editor.ToolManager;
import model.editor.Pencil;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public abstract class Tool {
    private List<String> operations;
    protected String actualOp;
    
    ToolManager manager;
    Pencil pencil;

    public Tool(ToolManager manager, Pencil selector, String... operationsArray) {
        this.manager = manager;
        this.pencil = selector;
        operations = Arrays.asList(operationsArray);
        actualOp = operations.get(0);
    }
    
    public abstract void primaryAction();
    public abstract void secondaryAction();
    public abstract void toggleSet();
    
    
    public void toggleOperation(){
        int index = operations.indexOf(actualOp)+1;
        if(index >= operations.size())
            index = 0;
        actualOp = operations.get(index);
        LogUtil.logger.info("operation set to "+actualOp);
    }
    public final String getOperationName(int index){
        if(index >= 0 && index < operations.size())
            return operations.get(index);
        return "";

    }
    public void setOperation(int index){
        if(index >= 0 && index < operations.size())
            actualOp = operations.get(index);
        LogUtil.logger.info("operation set to "+actualOp);
    }
    
    public boolean isAnalog(){
        return true;
    }
}
