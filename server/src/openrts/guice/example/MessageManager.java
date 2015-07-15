/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package openrts.guice.example;

import openrts.guice.AppSettingsRef;
import openrts.guice.AssetManagerRef;
import openrts.guice.GuiNodeRef;

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
	private @AssetManagerRef AssetManager assetManager;
	@Inject
	private @GuiNodeRef Node guiNode;
	@Inject
	private @AppSettingsRef AppSettings settings;

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
