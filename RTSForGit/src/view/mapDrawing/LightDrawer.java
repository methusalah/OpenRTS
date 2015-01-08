/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.mapDrawing;

import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.shadow.CompareMode;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import model.battlefield.lighting.SunLight;
import model.battlefield.map.Tile;
import tools.LogUtil;
import view.math.Translator;

/**
 *
 * @author Benoît
 */
public class LightDrawer implements ActionListener {
    
    SunLight sunLight;
    Node rootNode;
    
    AmbientLight al;
    DirectionalLight sun;
    DirectionalLight shadowCaster;
    DirectionalLightShadowRenderer sr;

    public LightDrawer(SunLight sunLight, AssetManager am, Node rootNode, ViewPort vp) {
        this.sunLight = sunLight;
        this.rootNode = rootNode;
        al = Translator.toJMELight(sunLight.ambient);
        sun = Translator.toJMELight(sunLight.sun);
        shadowCaster = Translator.toJMELight(sunLight.shadowCaster);
                
        rootNode.addLight(al);
        rootNode.addLight(sun);
        rootNode.addLight(shadowCaster);

        FilterPostProcessor fpp = new FilterPostProcessor(am);

        int SHADOWMAP_SIZE = 4096;
        sr = new DirectionalLightShadowRenderer(am, SHADOWMAP_SIZE, 1);
        sr.setLight(shadowCaster);
        sr.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
        sr.setShadowIntensity((float)sunLight.shadowCaster.intensity);
        vp.addProcessor(sr);

        DirectionalLightShadowFilter sf = new DirectionalLightShadowFilter(am, SHADOWMAP_SIZE, 1);
        sf.setLight(shadowCaster);
        sf.setEnabled(true);
        sf.setShadowZExtend(SHADOWMAP_SIZE);
//        fpp.addFilter(sf);


        // Ambiant occlusion filter
        SSAOFilter ssaoFilter = new SSAOFilter(0.5f, 4f, 0.2f, 0.3f);
//        fpp.addFilter(ssaoFilter);
        // Glow filter
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
        vp.addProcessor(fpp);
        
        updateLights();
        
    }
    
    

    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()){
            case "light" : updateLights(); break;
                default: throw new IllegalArgumentException("Unknown command : "+e.getActionCommand());
        }
    }

    private void updateLights(){
        Translator.toJMELight(al, sunLight.ambient);
        Translator.toJMELight(sun, sunLight.sun);
        Translator.toJMELight(shadowCaster, sunLight.shadowCaster);
        shadowCaster.setColor(ColorRGBA.Blue.mult(0));
        sr.setShadowIntensity((float)sunLight.shadowCaster.intensity);
        
    }

    
}
