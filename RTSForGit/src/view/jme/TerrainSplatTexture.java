/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.jme;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import model.map.editor.tools.AtlasTool;
import model.map.atlas.Atlas;

/**
 *
 * @author Benoît
 */
public class TerrainSplatTexture {
    
    Atlas atlas;
    ArrayList<Texture> diffuseMaps = new ArrayList<>();
    ArrayList<Texture> normaMaps = new ArrayList<>();
    ArrayList<Double> scales = new ArrayList<>();
    
    AssetManager am;
    
    Material mat;
    AWTLoader awtLoader;
            
    public TerrainSplatTexture(Atlas atlas, AssetManager am) {
        this.atlas = atlas;
        this.am = am;
        awtLoader = new AWTLoader();
    }
    
    public void addTexture(Texture diffuse, Texture normal, double scale){
        diffuse.setAnisotropicFilter(8);
        diffuse.setWrap(Texture.WrapMode.Repeat);
        diffuseMaps.add(diffuse);

        if(normal != null){
            normal.setAnisotropicFilter(8);
            normal.setWrap(Texture.WrapMode.Repeat);
        }
        normaMaps.add(normal);
        
        scales.add(scale);
    }
    
    public void buildMaterial(){
        mat = new Material(am, "Common/MatDefs/Terrain/TerrainLighting.j3md");
        mat.setTexture("AlphaMap", new Texture2D(new Image(Image.Format.RGBA8, atlas.width, atlas.height, atlas.getBuffer(0))));
        mat.setTexture("AlphaMap_1", new Texture2D(new Image(Image.Format.RGBA8, atlas.width, atlas.height, atlas.getBuffer(1))));
//        mat.setTexture("AlphaMap_2", new Texture2D(new Image(Image.Format.ABGR8, atlas.width, atlas.height, atlas.getBuffer(2))));

        for(int i=0; i<12; i++){
            if(diffuseMaps.size()>i){
                if(i==0)
                    mat.setTexture("DiffuseMap", diffuseMaps.get(i));
                else
                    mat.setTexture("DiffuseMap_"+i, diffuseMaps.get(i));

                mat.setFloat("DiffuseMap_"+i+"_scale", scales.get(i).floatValue());
                if(normaMaps.get(i) != null)
                    if(i==0)
                        mat.setTexture("NormalMap", normaMaps.get(i));
                    else
                        mat.setTexture("NormalMap_"+i, normaMaps.get(i));
            }
        }
    }
    
    public Material getMaterial(){
        if(atlas.toUpdate){
            mat.setTexture("AlphaMap", new Texture2D(new Image(Image.Format.RGBA8, atlas.width, atlas.height, atlas.getBuffer(0))));
            mat.setTexture("AlphaMap_1", new Texture2D(new Image(Image.Format.RGBA8, atlas.width, atlas.height, atlas.getBuffer(1))));
//            mat.setTexture("AlphaMap_2", new Texture2D(new Image(Image.Format.ABGR8, atlas.width, atlas.height, atlas.getBuffer(2))));
            atlas.toUpdate = false;
        }
        return mat;
    }
}
