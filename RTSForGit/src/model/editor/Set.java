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
    ArrayList<String> assets;
    boolean hasIcon;

    public Set(ArrayList<String> assets, boolean hasIcon) {
        this.assets = assets;
        this.count = assets.size();
        this.hasIcon = hasIcon;
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
    
    public void set(String asset){
        set(assets.indexOf(asset));
    }
    
    public String getAsset(int setIndex){
        return assets.get(setIndex);
    }
    
    public ArrayList<String> getAllAssets(){
        return assets;
    }

    public int getCount() {
        return count;
    }
    
    public boolean hasIcons(){
        return hasIcon;
    }

    
    
}
