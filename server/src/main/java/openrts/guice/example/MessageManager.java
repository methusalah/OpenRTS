/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package openrts.guice.example;

import openrts.guice.annotation.AppSettingsRef;
import openrts.guice.annotation.GuiNodeRef;

import com.google.inject.Inject;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

/**
 *
 * @author lbrasseur
 */
public class MessageManager {
	@Inject
	private AssetManager assetManager;
	@Inject
	@GuiNodeRef
	private  Node guiNode;
	
	@Inject
	@AppSettingsRef
	private  AppSettings settings;

	public void setMessage(String message) {
		BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
		BitmapText ch = new BitmapText(guiFont, false);
		ch.setSize(guiFont.getCharSet().getRenderedSize() * 6);
		ch.setText(message);
		ch.setLocalTranslation(
				settings.getWidth() / 2 - ch.getLineWidth() / 2,
				settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
		guiNode.attachChild(ch);
	}

}
