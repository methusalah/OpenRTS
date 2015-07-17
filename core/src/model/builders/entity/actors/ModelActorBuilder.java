/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.builders.entity.actors;

import geometry.math.AngleUtil;

import java.awt.Color;
import java.util.HashMap;
import java.util.logging.Logger;

import model.battlefield.abstractComps.FieldComp;
import model.battlefield.actors.Actor;
import model.battlefield.actors.ModelActor;
import model.builders.entity.definitions.DefElement;
import model.builders.entity.definitions.Definition;

/**
 * @author Benoît
 */
public class ModelActorBuilder extends ActorBuilder {

	private static final Logger logger = Logger.getLogger(ModelActorBuilder.class.getName());

	private static final String MODEL_PATH = "ModelPath";
	private static final String SCALE = "Scale";
	private static final String SCALE_X = "ScaleX";
	private static final String SCALE_Y = "ScaleY";
	private static final String SCALE_Z = "ScaleZ";
	private static final String YAW = "Yaw";
	private static final String PITCH = "Pitch";
	private static final String ROLL = "Roll";
	private static final String COLOR = "Color";
	private static final String MATERIAL = "Material";
	private static final String SUB_COLOR = "SubColor";
	private static final String SUB_MESH_NAME = "SubMeshName";
	private static final String SUB_MESH_INDEX = "SubMeshIndex";
	private static final String RED = "R";
	private static final String GREEN = "G";
	private static final String BLUE = "B";
	private static final String PATH = "Path";

	private String modelPath;
	private double scaleX = 1;
	private double scaleY = 1;
	private double scaleZ = 1;
	private double yaw = 0;
	private double pitch = 0;
	private double roll = 0;
	private Color color;
	private HashMap<String, Color> subColorsByName = new HashMap<>();
	private HashMap<Integer, Color> subColorsByIndex = new HashMap<>();
	private HashMap<String, String> materialsByName = new HashMap<>();
	private HashMap<Integer, String> materialsByIndex = new HashMap<>();

	public ModelActorBuilder(Definition def) {
		super(def);
		for (DefElement de : def.getElements()) {
			switch (de.name) {
				case TYPE:
				case TRIGGER:
				case ACTOR_LIST:
					break;
				case MODEL_PATH:
					modelPath = de.getVal();
					break;
				case SCALE:
					scaleX = scaleY = scaleZ = de.getDoubleVal();
					break;
				case SCALE_X:
					scaleX = de.getDoubleVal();
					break;
				case SCALE_Y:
					scaleY = de.getDoubleVal();
					break;
				case SCALE_Z:
					scaleZ = de.getDoubleVal();
					break;
				case YAW:
					yaw = AngleUtil.toRadians(de.getDoubleVal());
					break;
				case PITCH:
					pitch = AngleUtil.toRadians(de.getDoubleVal());
					break;
				case ROLL:
					roll = AngleUtil.toRadians(de.getDoubleVal());
					break;
				case COLOR:
					color = new Color(de.getIntVal(RED), de.getIntVal(GREEN), de.getIntVal(BLUE));
					break;
				case SUB_COLOR:
					Color subcolor = new Color(de.getIntVal(RED), de.getIntVal(GREEN), de.getIntVal(BLUE));
					String name = de.getVal(SUB_MESH_NAME);
					String indexString = de.getVal(SUB_MESH_INDEX);
					int index = indexString == null?-1 : Integer.parseInt(indexString);
					if(name != null) {
						subColorsByName.put(name, subcolor);
					} else if(index != -1){
						if(index == 0) {
							logger.warning("the submesh index must be >= 1 in "+getId());
						}
						subColorsByIndex.put(index, subcolor);
					} else {
						logger.warning("SubColor incorrect in "+getId());
					}
					break;
				case MATERIAL:
					String material = de.getVal(PATH);
					name = de.getVal(SUB_MESH_NAME);
					indexString = de.getVal(SUB_MESH_INDEX);
					index = indexString == null?-1 : Integer.parseInt(indexString);
					if(name != null) {
						materialsByName.put(name, material);
					} else if(index != -1) {
						materialsByIndex.put(index, material);
					} else {
						logger.warning("Material incorrect in " + getId());
					}
					break;
					
				default:
					printUnknownElement(de.name);
			}
		}
	}

	public ModelActor build(FieldComp comp) {
		String localModelPath = modelPath;
		if (!comp.modelPath.isEmpty()) {
			localModelPath = comp.modelPath;
		}
		Color localColor = color;
		if (comp.color != null) {
			localColor = comp.color;
		}

		double localScaleX = scaleX * comp.scaleX;
		double localScaleY = scaleY * comp.scaleY;
		double localScaleZ = scaleZ * comp.scaleZ;

		ModelActor res = new ModelActor(null, "", childrenTriggers, childrenActorBuilders,
				localModelPath,
				localScaleX,
				localScaleY,
				localScaleZ,
				yaw,
				pitch,
				roll,
				localColor,
				subColorsByName,
				subColorsByIndex,
				materialsByName,
				materialsByIndex,
				comp);
		res.debbug_id = getId();
		//		res.act();
		return res;
	}

	@Override
	public ModelActor build(String trigger, Actor parent) {
		throw new RuntimeException("Can't create Model actor without a Movable");
	}
}
