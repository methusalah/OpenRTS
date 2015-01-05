/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.editor;

import java.util.ArrayList;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class Set {
    public int count;
    public int actual = 0;
    ArrayList<String> iconPaths;

    public Set(int count, ArrayList<String> iconPaths) {
        this.count = count;
        this.iconPaths = iconPaths;
        if(iconPaths.size()!=count)
            throw new RuntimeException("set count and icon count must be equals.");
    }
    
    public void toggle() {
        actual++;
        if(actual > count)
            actual = 0;
        LogUtil.logger.info("toggled to set "+actual+".");
    }

    public void set(int index) {
        if(index < 0 || index >= count)
            throw new IllegalArgumentException("Set "+index+" doesn't exists.");
        actual = index;
        LogUtil.logger.info("toggled to set "+actual+".");
    }
    
    public String getIcon(int setIndex){
        return iconPaths.get(setIndex);
    }

    public int getCount() {
        return count;
    }

    
    
}
