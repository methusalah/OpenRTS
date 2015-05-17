/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.editor;

import java.util.List;

/**
 *
 * @author Benoît
 */
public class AssetSet {
	public int actual = 0;
	List<String> assets;
	boolean hasIcon;

	public AssetSet(List<String> assets, boolean hasIcon) {
		this.assets = assets;
		this.hasIcon = hasIcon;
	}

	public void toggle() {
		actual++;
		if (actual > assets.size()) {
			actual = 0;
		}
	}

	public void set(int index) {
		if (index < 0 || index >= assets.size()) {
			throw new IllegalArgumentException("Set "+index+" doesn't exists.");
		}
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
		return assets.size();
	}

	public boolean hasIcons(){
		return hasIcon;
	}

}
