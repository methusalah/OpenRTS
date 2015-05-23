/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.builders.actors;

import java.awt.Color;

import model.ModelManager;
import model.battlefield.abstractComps.FieldComp;
import model.battlefield.actors.Actor;
import model.battlefield.actors.ModelActor;
import model.builders.definitions.DefElement;
import model.builders.definitions.Definition;

/**
 * @author Benoît
 */
public class ModelActorBuilder extends ActorBuilder {
	private static final String MODEL_PATH = "ModelPath";
	private static final String SCALE = "Scale";
	private static final String SCALE_X = "ScaleX";
	private static final String SCALE_Y = "ScaleY";
	private static final String SCALE_Z = "ScaleZ";
	private static final String COLOR = "Color";
	private static final String RED = "R";
	private static final String GREEN = "G";
	private static final String BLUE = "B";

	private String modelPath;
	private double scaleX = 1;
	private double scaleY = 1;
	private double scaleZ = 1;
	private Color color;

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
				case COLOR:
					color = new Color(de.getIntVal(RED), de.getIntVal(GREEN), de.getIntVal(BLUE));
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

		ModelActor res = new ModelActor(null, "", childrenTriggers, childrenActorBuilders, ModelManager.getBattlefield().getActorPool(), localModelPath,
				localScaleX,
				localScaleY,
				localScaleZ, localColor, comp);
		res.debbug_id = getId();
		res.act();
		return res;
	}

	@Override
	public ModelActor build(String trigger, Actor parent) {
		throw new RuntimeException("Can't create Model actor without a Movable");
	}
}
