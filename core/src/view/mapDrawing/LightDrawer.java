/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package view.mapDrawing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.ModelManager;
import view.MapView;
import view.math.Translator;

import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;

/**
 * @author Benoît
 */
public class LightDrawer implements ActionListener {

	MapView view;
	Node rootNode;

	AmbientLight al;
	DirectionalLight sun;
	DirectionalLight shadowCaster;
	DirectionalLightShadowRenderer sr;
	DirectionalLightShadowFilter sf;

	public LightDrawer(MapView view, AssetManager am, Node rootNode, ViewPort vp) {
		this.view = view;
		this.rootNode = rootNode;

		FilterPostProcessor fpp = new FilterPostProcessor(am);

		int SHADOWMAP_SIZE = 4096;
		sr = new DirectionalLightShadowRenderer(am, SHADOWMAP_SIZE, 1);
		sr.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
		sr.setShadowIntensity((float) ModelManager.getBattlefield().getSunLight().shadowCaster.intensity);
		// vp.addProcessor(sr);

		sf = new DirectionalLightShadowFilter(am, SHADOWMAP_SIZE, 1);
		sf.setEnabled(true);
		sf.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
		sf.setShadowZExtend(SHADOWMAP_SIZE);
		fpp.addFilter(sf);

		// Ambiant occlusion filter
		SSAOFilter ssaoFilter = new SSAOFilter(0.5f, 4f, 0.2f, 0.3f);
		// fpp.addFilter(ssaoFilter);
		// Glow filter
		BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
		fpp.addFilter(bloom);
		vp.addProcessor(fpp);

		reset();
		updateLights();

	}

	public void reset() {
		rootNode.removeLight(al);
		rootNode.removeLight(sun);
		rootNode.removeLight(shadowCaster);

		al = Translator.toJMELight(ModelManager.getBattlefield().getSunLight().ambient);
		sun = Translator.toJMELight(ModelManager.getBattlefield().getSunLight().sun);
		shadowCaster = Translator.toJMELight(ModelManager.getBattlefield().getSunLight().shadowCaster);
		sr.setLight(shadowCaster);
		sf.setLight(shadowCaster);

		rootNode.addLight(al);
		rootNode.addLight(sun);
		rootNode.addLight(shadowCaster);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case "light":
				updateLights();
				break;
			default:
				throw new IllegalArgumentException("Unknown command : " + e.getActionCommand());
		}
	}

	public void updateLights() {
		Translator.toJMELight(al, ModelManager.getBattlefield().getSunLight().ambient);
		Translator.toJMELight(sun, ModelManager.getBattlefield().getSunLight().sun);
		Translator.toJMELight(shadowCaster, ModelManager.getBattlefield().getSunLight().shadowCaster);
		shadowCaster.setColor(ColorRGBA.Blue.mult(0));
		sr.setShadowIntensity((float) ModelManager.getBattlefield().getSunLight().shadowCaster.intensity);
		sf.setShadowIntensity((float) ModelManager.getBattlefield().getSunLight().shadowCaster.intensity);

	}

}
