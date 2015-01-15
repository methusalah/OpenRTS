/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.editor;

import java.util.ArrayList;
import java.util.List;

import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class Set {
    public int count;
    public int actual = 0;
    List<String> assets;
    boolean hasIcon;

    public Set(List<String> assets, boolean hasIcon) {
        this.assets = assets;
        this.count = assets.size();
        this.hasIcon = hasIcon;
    }
    
    public void toggle() {
        actual++;
        if(actual > count)
            actual = 0;
    }

    public void set(int index) {
        if(index < 0 || index >= count)
            throw new IllegalArgumentException("Set "+index+" doesn't exists.");
        actual = index;
    }
    
    public void set(String asset){
        set(assets.indexOf(asset));
    }
    
    public String getAsset(int setIndex){
        return assets.get(setIndex);
    }
    
    public List<String> getAllAssets(){
        return assets;
    }

    public int getCount() {
        return count;
    }
    
    public boolean hasIcons(){
        return hasIcon;
    }

    
    
}
