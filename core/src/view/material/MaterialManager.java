package view.material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

public class MaterialManager {
	public static AssetManager am;

	static Map<String, Material> texturesMap = new HashMap<String, Material>();
	static Map<String, Texture> textureFileMap = new HashMap<String, Texture>();
	static Map<ColorRGBA, Material> colorsMap = new HashMap<ColorRGBA, Material>();
	static Map<String, Material> materials = new HashMap<>();

	// Materials
	public static Material concreteMaterial;
	public static Material streetLaneMaterial;
	public static Material sidewalkMaterial;

	public static Material contourMaterial;
	public static Material lotMaterial1;
	public static Material lotMaterial2;
	public static Material lotMaterial3;
	public static Material debugMaterial;
	public static Material lotContourMaterial;
	public static Material blockContourMaterial;
	public static Material debugTextureMaterial;
	public static Material blueMaterial;
	public static Material blue2Material;
	public static Material blue3Material;
	public static Material redMaterial;
	public static Material yellowMaterial;
	public static Material cyanMaterial;
	public static Material blackMaterial;
	public static Material greenMaterial;
	public static Material floorMaterial;
	public static Material windowsMaterial;
	public static Material itemMaterial;
	public static Material terrainMaterial;
	public static Material roadsMaterial;
	public static Material whiteConcreteMaterial;
	public static ArrayList<Material> gradientMaterial = new ArrayList<Material>();

	private MaterialManager() {
	}
	
	public static void setAssetManager(AssetManager assetManager){
		am = assetManager;
		initBaseMaterials();
	}

	public static Material getColor(ColorRGBA color) {
		Material res = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
		res.setColor("Color", color);
		return res;
	}

	public static Material getLightingColor(ColorRGBA color) {
		// We first check if the requested color exist in the map
		if (colorsMap.containsKey(color)) {
			return colorsMap.get(color);
		}

		// At this point, we know that the color doesn't exist.
		// We must create a new material, add it to the map and return it.
		Material res = new Material(am, "Common/MatDefs/Light/Lighting.j3md");
		res.setColor("Diffuse", color);
		res.setFloat("Shininess", 10f);
		res.setBoolean("UseMaterialColors", true);

		colorsMap.put(color, res);

		return res;
	}

	private static Texture getTexture(String path) {
		Texture res = textureFileMap.get(path);
		if (res == null) {
			res = am.loadTexture(path);
			textureFileMap.put(path, res);
		}
		return res;

	}

	public static Material getLightingTexture(String texturePath) {
		// We first check if the requested texture exist in the material map
		if (texturesMap.containsKey(texturePath)) {
			return texturesMap.get(texturePath);
		}

		// At this point, we know that the texture doesn't exist.
		// We must create a new material, add it to the map and return it.
		Material res = new Material(am, "Common/MatDefs/Light/Lighting.j3md");
		Texture t = am.loadTexture(texturePath);
		t.setWrap(WrapMode.Repeat);
		t.setAnisotropicFilter(8);
		res.setTexture("DiffuseMap", t);

		res.setFloat("Shininess", 10f); // [0,128]

		// test for envmap
		// TextureCubeMap envmap = new TextureCubeMap(assetManager.loadTexture("Textures/cubemap_reflec.dds").getImage());
		// res.setTexture("EnvMap", envmap);
		// res.setVector3("FresnelParams", new Vector3f(0.05f, 0.18f, 0.11f));
		// res.setVector3("FresnelParams", new Vector3f(0.05f, 0.05f, 0.05f));

		// normal map loading
		// String normalPath = texturePath.replaceAll(".jpg", "norm.jpg");;
		// try {
		// Texture normalMap = assetManager.loadTexture(normalPath);
		// normalMap.setWrap(WrapMode.Repeat);
		// normalMap.setAnisotropicFilter(8);
		// res.setTexture("NormalMap", normalMap);
		// }catch (Exception e) {
		// LogUtil.logger.info("No normal map found for "+texturePath);
		// }

		texturesMap.put(texturePath, res);

		return res;
	}

	private static void initBaseMaterials() {
		ColorRGBA lotColorBase = new ColorRGBA(200f / 255f, 200f / 255f, 200f / 255f, 255f / 255f);
		ColorRGBA concreteColor = new ColorRGBA(90f / 255f, 100f / 255f, 255f / 255f, 255f / 255f);
		ColorRGBA redConcreteColor = ColorRGBA.Red;
		ColorRGBA blueConcreteColor = ColorRGBA.Blue;
		ColorRGBA yellowConcreteColor = ColorRGBA.Yellow;
		ColorRGBA cyanConcreteColor = new ColorRGBA(0, 1, 1, 0.4f);
		ColorRGBA blackConcreteColor = ColorRGBA.Black;
		ColorRGBA greenConcreteColor = ColorRGBA.Green;
		ColorRGBA floorColor = ColorRGBA.Gray;
		ColorRGBA windowsColor = ColorRGBA.White;
		ColorRGBA itemColor = ColorRGBA.LightGray;
		ColorRGBA roadsColor = ColorRGBA.LightGray;
		ColorRGBA terrainColor = new ColorRGBA(0f / 255f, 50f / 255f, 14f / 255f, 255f / 255f);

		am.registerLocator("assets/", FileLocator.class);

		contourMaterial = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
		contourMaterial.setColor("Color", blackConcreteColor);

		blockContourMaterial = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
		blockContourMaterial.setColor("Color", redConcreteColor);

		lotContourMaterial = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
		lotContourMaterial.setColor("Color", blueConcreteColor);

		lotMaterial1 = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
		lotMaterial1.setColor("Color", lotColorBase);

		lotMaterial2 = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
		lotMaterial2.setColor("Color", lotColorBase);

		lotMaterial3 = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
		lotMaterial3.setColor("Color", lotColorBase);

		// debug material
		debugMaterial = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
		debugMaterial.setColor("Color", redConcreteColor);
		// debug texture material
		// debugTextureMaterial = new Material(assetManager, "Common/MatDefs/Misc/SimpleTextured.j3md");
		// debugTextureMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		// debugTextureMaterial.setTexture("DiffuseMap", assetManager.loadTexture("Textures/UVTest.jpg"));
		// debugTextureMaterial.setFloat("Shininess", 128f); // [0,128]

		// Red Material
		redMaterial = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
		redMaterial.setColor("Color", redConcreteColor);
		redMaterial.setColor("GlowColor", redConcreteColor);

		// Concrete Material
		yellowMaterial = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
		yellowMaterial.setColor("Color", yellowConcreteColor);

		// Concrete Material
		cyanMaterial = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
		cyanMaterial.setColor("Color", cyanConcreteColor);
		cyanMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);

		// Concrete Material
		blackMaterial = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
		blackMaterial.setColor("Color", blackConcreteColor);

		// Concrete Material
		greenMaterial = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
		greenMaterial.setColor("Color", greenConcreteColor);
		greenMaterial.setColor("GlowColor", greenConcreteColor);

		// Item Material
		itemMaterial = new Material(am, "Common/MatDefs/Light/Lighting.j3md");
		itemMaterial.setColor("Diffuse", itemColor);
		itemMaterial.setBoolean("UseMaterialColors", true);

		// gradient blue
		for (int i = 0; i < 4; i++) {
			gradientMaterial.add(new Material(am, "Common/MatDefs/Misc/Unshaded.j3md"));
			gradientMaterial.get(i).setColor("Color", new ColorRGBA(i * 30 / 255f, i * 30 / 255f, i * 85 / 255f, 1));
		}
	}

	public static Material getMaterial(String materialPath) {
		if(materials.get(materialPath) == null)
			materials.put(materialPath, am.loadMaterial(materialPath));
		return materials.get(materialPath);
	}
}
