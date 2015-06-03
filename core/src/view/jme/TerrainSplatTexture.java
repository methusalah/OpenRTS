/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package view.jme;

import java.util.ArrayList;
import java.util.List;

import model.battlefield.map.atlas.Atlas;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ImageRaster;
import com.jme3.texture.plugins.AWTLoader;

/**
 * @author Benoît
 */
public class TerrainSplatTexture {

	private Atlas atlas;
	private List<Texture> diffuseMaps = new ArrayList<>();
	private List<Texture> normaMaps = new ArrayList<>();
	private List<Double> scales = new ArrayList<>();

	AssetManager am;

	Material mat;
	AWTLoader awtLoader;

	public TerrainSplatTexture(Atlas atlas, AssetManager am) {
		this.atlas = atlas;
		this.am = am;
		awtLoader = new AWTLoader();
	}

	public void addTexture(Texture diffuse, Texture normal, double scale) {
		diffuse.setAnisotropicFilter(8);
		diffuse.setWrap(Texture.WrapMode.Repeat);
		diffuseMaps.add(diffuse);

		if (normal != null) {
			normal.setAnisotropicFilter(8);
			normal.setWrap(Texture.WrapMode.Repeat);
		}
		normaMaps.add(normal);

		scales.add(scale);
		//		ImageRaster raster = ImageRaster.create(diffuse.getImage());
		//		List<Short> alphaValues = new ArrayList<>(raster.getWidth()*raster.getHeight());
		//		LogUtil.logger.info("taille du buffer : "+raster.getWidth()*raster.getHeight());
		//		for(int x = 0; x < raster.getWidth(); x++)
		//			for(int y = 0; y < raster.getHeight(); y++)
		//				alphaValues.add((short)Math.round(raster.getPixel(x, y).a*255));
		//
		//		atlas.layers.get(diffuseMaps.size()-1).setMask(raster.getWidth(), raster.getHeight(), alphaValues, scale);

		atlas.getLayers().get(diffuseMaps.size() - 1).mask = ImageRaster.create(diffuse.getImage());
		atlas.getLayers().get(diffuseMaps.size() - 1).maskScale = scale;

	}

	public void buildMaterial() {
		mat = new Material(am, "Common/MatDefs/Terrain/TerrainLighting.j3md");
		mat.setTexture("AlphaMap", new Texture2D(new Image(Image.Format.RGBA8, atlas.getWidth(), atlas.getHeight(), atlas.getBuffer(0))));
		mat.setTexture("AlphaMap_1", new Texture2D(new Image(Image.Format.RGBA8, atlas.getWidth(), atlas.getHeight(), atlas.getBuffer(1))));
		// mat.setTexture("AlphaMap_2", new Texture2D(new Image(Image.Format.ABGR8, atlas.width, atlas.height, atlas.getBuffer(2))));
		// mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);

		for (int i = 0; i < 12; i++) {
			if (diffuseMaps.size() > i) {
				if (i == 0) {
					mat.setTexture("DiffuseMap", diffuseMaps.get(i));
				} else {
					mat.setTexture("DiffuseMap_" + i, diffuseMaps.get(i));
				}

				mat.setFloat("DiffuseMap_" + i + "_scale", scales.get(i).floatValue());
				if (normaMaps.get(i) != null) {
					if (i == 0) {
						mat.setTexture("NormalMap", normaMaps.get(i));
					} else {
						mat.setTexture("NormalMap_" + i, normaMaps.get(i));
					}
				}
			}


		}
	}

	public Material getMaterial() {
		if (atlas.isToUpdate()) {
			mat.setTexture("AlphaMap", new Texture2D(new Image(Image.Format.RGBA8, atlas.getWidth(), atlas.getHeight(), atlas.getBuffer(0))));
			mat.setTexture("AlphaMap_1", new Texture2D(new Image(Image.Format.RGBA8, atlas.getWidth(), atlas.getHeight(), atlas.getBuffer(1))));
			// mat.setTexture("AlphaMap_2", new Texture2D(new Image(Image.Format.ABGR8, atlas.width, atlas.height, atlas.getBuffer(2))));
			atlas.setToUpdate(false);
		}
		return mat;
	}
}
