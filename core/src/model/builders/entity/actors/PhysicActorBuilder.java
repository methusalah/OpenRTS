/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.builders.entity.actors;

import geometry.math.Angle;

import java.awt.Color;
import java.util.HashMap;
import java.util.logging.Logger;

import model.battlefield.abstractComps.Hiker;
import model.battlefield.actors.Actor;
import model.battlefield.actors.PhysicActor;
import model.builders.entity.definitions.DefElement;
import model.builders.entity.definitions.Definition;

/**
 * @author Benoît
 */
public class PhysicActorBuilder extends ActorBuilder {

	private static final Logger logger = Logger.getLogger(PhysicActorBuilder.class.getName());

	private static final String MODEL_PATH = "ModelPath";
	private static final String SCALE = "Scale";
	private static final String MASS = "Mass";
	private static final String LIFE = "Life";
	private static final String MASS_CENTER_BONE = "MassCenterBone";
	private static final String YAW = "Yaw";
	private static final String PITCH = "Pitch";
	private static final String ROLL = "Roll";
	private static final String COLOR = "Color";
	private static final String SUB_COLOR = "SubColor";
	private static final String SUB_MESH_NAME = "SubMeshColor";
	private static final String SUB_MESH_INDEX = "SubMeshIndex";
	private static final String RED = "R";
	private static final String GREEN = "G";
	private static final String BLUE = "B";


	private String modelPath;
	private double scale = 1;
	private double mass = 1;
	private double life = 1;
	private String massCenterBone;
	private double yaw = 0;
	private double pitch = 0;
	private double roll = 0;
	private Color color;
	private HashMap<String, Color> subColorsByName = new HashMap<>();
	private HashMap<Integer, Color> subColorsByIndex = new HashMap<>();


	public PhysicActorBuilder(Definition def) {
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
					scale = de.getDoubleVal();
					break;
				case LIFE:
					life = de.getDoubleVal() * 1000;
					break;
				case MASS:
					mass = de.getDoubleVal();
					break;
				case MASS_CENTER_BONE:
					massCenterBone = de.getVal();
					break;
				case YAW:
					yaw = Angle.toRadians(de.getDoubleVal());
					break;
				case PITCH:
					pitch = Angle.toRadians(de.getDoubleVal());
					break;
				case ROLL:
					roll = Angle.toRadians(de.getDoubleVal());
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
					} else if(index != -1) {
						subColorsByIndex.put(index, subcolor);
					} else {
						logger.warning("SubColor incorrect in " + getId());
					}
					break;

				default:
					printUnknownElement(de.name);
			}
		}
	}

	@Override
	public Actor build(String trigger, Actor parent) {
		return build(trigger, null, parent);
	}

	public Actor build(String trigger, Hiker movable, Actor parent) {
		Actor res = new PhysicActor(modelPath, scale, life, mass, massCenterBone, parent, trigger, childrenTriggers, childrenActorBuilders, yaw, pitch, roll, color, subColorsByName, subColorsByIndex);
		res.debbug_id = getId();
		return res;
	}
}
