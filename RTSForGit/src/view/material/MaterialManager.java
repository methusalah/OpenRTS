package view.material;


import java.util.ArrayList;
import java.util.HashMap;


import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

public class MaterialManager {
	AssetManager assetManager;

	HashMap<String, Material> texturesMap = new HashMap<String, Material>();
	HashMap<ColorRGBA, Material> colorsMap = new HashMap<ColorRGBA, Material>();

	// Materials
    public Material concreteMaterial;
    public Material streetLaneMaterial;
    public Material sidewalkMaterial;
    
    
    public Material contourMaterial;
    public Material lotMaterial1;
    public Material lotMaterial2;
    public Material lotMaterial3;
    public Material debugMaterial;
    public Material lotContourMaterial;
    public Material blockContourMaterial;
    public Material debugTextureMaterial;
    public Material blueMaterial;
    public Material blue2Material;
    public Material blue3Material;
    public Material redMaterial;
    public Material yellowMaterial;
    public Material cyanMaterial;
    public Material blackMaterial;
    public Material greenMaterial;
    public Material floorMaterial;
    public Material windowsMaterial;
    public Material itemMaterial;
    public Material terrainMaterial;
    public Material roadsMaterial;
    public Material whiteConcreteMaterial;
    public ArrayList<Material> gradientMaterial = new ArrayList<Material>();
    
    public MaterialManager (AssetManager assetManager) {
		this.assetManager = assetManager;
		initBaseMaterials();
	}

    public Material getColor(ColorRGBA color) {
    	Material res = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    	res.setColor("Color", color);
    	return res;
    }
    
    public Material getLightingColor(ColorRGBA color) {
        // We first check if the requested color exist in the map
        if(colorsMap.containsKey(color))
                return colorsMap.get(color);

        // At this point, we know that the color doesn't exist.
        // We must create a new material, add it to the map and return it.
    	Material res = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    	res.setColor("Diffuse", color);
        res.setFloat("Shininess", 10f);
    	res.setBoolean("UseMaterialColors", true);
    	
    	colorsMap.put(color, res);
  
    	return res;
    }
    
    public Material getTerrain(String alpha, String tex1, String tex2, String tex3, String tex4){
        Material m = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
        m.setTexture("AlphaMap", assetManager.loadTexture(alpha));
        
        // texture 1
        Texture t1 = assetManager.loadTexture(tex1);
        t1.setWrap(WrapMode.Repeat);
        t1.setAnisotropicFilter(8);
        m.setTexture("DiffuseMap", t1);
        m.setFloat("DiffuseMap_0_scale", 8f);

        // texture 2
        Texture t2 = assetManager.loadTexture(tex2);
        t2.setAnisotropicFilter(8);
        t2.setWrap(WrapMode.Repeat);
        m.setTexture("DiffuseMap_1", t2);
        m.setFloat("DiffuseMap_1_scale", 8f);

        m.setFloat("Shininess", 0f); // [0,128]

//        Texture normal = assetManager.loadTexture("textures/env01/groundGrass_n.png");
//        normal.setWrap(WrapMode.Repeat);
//        m.setTexture("NormalMap", normal);

//        Texture t3 = assetManager.loadTexture(tex3);
//        t3.setWrap(WrapMode.Repeat);
//        m.setTexture("DiffuseMap_2", t3);
//        m.setFloat("DiffuseMap_2_scale", 64f);

//        Texture t4 = assetManager.loadTexture(tex4);
//        t4.setWrap(WrapMode.Repeat);
//        m.setTexture("DiffuseMap_3", t4);
//        m.setFloat("DiffuseMap_3_scale", 32f);
        
        return m;
        
    }
    
	public Material getLightingTexture(String texturePath) {
		// We first check if the requested texture exist in the material map
		if(texturesMap.containsKey(texturePath))
			return texturesMap.get(texturePath);
			
		// At this point, we know that the texture doesn't exist.
		// We must create a new material, add it to the map and return it.
    	Material res = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    	Texture t = assetManager.loadTexture(texturePath);
    	t.setWrap(WrapMode.Repeat);
    	t.setAnisotropicFilter(8);
    	res.setTexture("DiffuseMap", t);
    	
       	res.setFloat("Shininess", 10f); // [0,128]
    	
    	// test for envmap
//    	TextureCubeMap envmap = new TextureCubeMap(assetManager.loadTexture("Textures/cubemap_reflec.dds").getImage());
//    	res.setTexture("EnvMap", envmap);
//    	res.setVector3("FresnelParams", new Vector3f(0.05f, 0.18f, 0.11f));
//    	res.setVector3("FresnelParams", new Vector3f(0.05f, 0.05f, 0.05f));
    	
    	// normal map loading
//    	String normalPath = texturePath.replaceAll(".jpg", "norm.jpg");;
//    	try {
//	       	Texture normalMap = assetManager.loadTexture(normalPath);
//	       	normalMap.setWrap(WrapMode.Repeat);
//	       	normalMap.setAnisotropicFilter(8);
//	   		res.setTexture("NormalMap", normalMap);
//    	}catch (Exception e) {
//    		LogUtil.logger.info("No normal map found for "+texturePath);
//    	}
	  
    	texturesMap.put(texturePath, res);

    	return res;
	}

    
	private void initBaseMaterials() {
    	ColorRGBA lotColorBase = new ColorRGBA(200f/255f, 200f/255f, 200f/255f, 255f/255f);
    	ColorRGBA concreteColor = new ColorRGBA(90f/255f, 100f/255f, 255f/255f, 255f/255f);
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
    	ColorRGBA terrainColor = new ColorRGBA(0f/255f, 50f/255f, 14f/255f, 255f/255f);

    	assetManager.registerLocator("assets/", FileLocator.class.getName());

    	contourMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    	contourMaterial.setColor("Color", blackConcreteColor);

    	blockContourMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    	blockContourMaterial.setColor("Color", redConcreteColor);

    	lotContourMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    	lotContourMaterial.setColor("Color", blueConcreteColor);
    	
    	lotMaterial1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    	lotMaterial1.setColor("Color", lotColorBase);

    	lotMaterial2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    	lotMaterial2.setColor("Color", lotColorBase);
    	
    	lotMaterial3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    	lotMaterial3.setColor("Color", lotColorBase);

    	
    	// debug material
    	debugMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    	debugMaterial.setColor("Color", redConcreteColor);
    	// debug texture material
//    	debugTextureMaterial = new Material(assetManager, "Common/MatDefs/Misc/SimpleTextured.j3md");
//    	debugTextureMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
//    	debugTextureMaterial.setTexture("DiffuseMap", assetManager.loadTexture("Textures/UVTest.jpg"));
//        debugTextureMaterial.setFloat("Shininess", 128f); // [0,128]
    	
    	// Red Material
    	redMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    	redMaterial.setColor("Color", redConcreteColor);
        redMaterial.setColor("GlowColor", redConcreteColor);
    	
    	// Concrete Material
    	yellowMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    	yellowMaterial.setColor("Color", yellowConcreteColor);
    	
    	// Concrete Material
    	cyanMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    	cyanMaterial.setColor("Color", cyanConcreteColor);
        cyanMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        
    	
    	// Concrete Material
    	blackMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    	blackMaterial.setColor("Color", blackConcreteColor);
    	
    	// Concrete Material
    	greenMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    	greenMaterial.setColor("Color", greenConcreteColor);
        greenMaterial.setColor("GlowColor", greenConcreteColor);
    	
    	// Item Material
    	itemMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    	itemMaterial.setColor("Diffuse", itemColor);
    	itemMaterial.setBoolean("UseMaterialColors", true);
    	
    	
    	// gradient blue
    	for (int i = 0; i < 4; i++) {
    		gradientMaterial.add(new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"));
    		gradientMaterial.get(i).setColor("Color", new ColorRGBA((float)(i*30)/255f, (float)(i*30)/255f, (float)(i*85)/255f, 1));
    	}
	}
}
