/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.builders.entity.actors;

import geometry.math.AngleUtil;

import java.awt.Color;

import model.battlefield.actors.Actor;
import model.battlefield.actors.ParticleActor;
import model.builders.entity.definitions.DefElement;
import model.builders.entity.definitions.Definition;

/**
 * @author Benoît
 */
public class ParticleActorBuilder extends ActorBuilder {
	private static final String SPRITE_PATH = "SpritePath";
	private static final String NB_COL = "NbCol";
	private static final String NB_ROW = "NbRow";
	private static final String EMISSION_NODE = "EmissionBone";
	private static final String DIRECTION_NODE = "DirectionBone";
	private static final String VELOCITY = "Velocity";
	private static final String FANNING = "Fanning";
	private static final String MAX_COUNT = "MaxCount";
	private static final String PER_SECOND = "PerSecond";
	private static final String DURATION = "Duration";
	private static final String START_SIZE = "StartSize";
	private static final String END_SIZE = "EndSize";
	private static final String START_COLOR = "StartColor";
	private static final String END_COLOR = "EndColor";
	private static final String MIN_LIFE = "MinLife";
	private static final String MAX_LIFE = "MaxLife";
	private static final String GRAVITY = "Gravity";
	private static final String FACING = "Facing";
	private static final String FACING_VELOCITY = "Velocity";
	private static final String FACING_HORIZONTAL = "Horizontal";
	private static final String ADD = "Add";
	private static final String START_VARIATION = "StartVariation";
	private static final String ROTATION_SPEED = "RotationSpeed";

	private static final String RED = "R";
	private static final String GREEN = "G";
	private static final String BLUE = "B";
	private static final String ALPHA = "A";

	private String spritePath;
	private int nbCol = 1;
	private int nbRow = 1;
	private String emissionBone;
	private String directionBone;
	private double velocity = 0;
	private double fanning = 0;
	private boolean randomSprite = false;
	private int maxCount;
	private int perSecond;
	private double duration = Double.MAX_VALUE;
	private double startSize;
	private double endSize;
	private Color startColor;
	private Color endColor;
	private double minLife;
	private double maxLife;
	private boolean gravity = false;
	private ParticleActor.Facing facing = ParticleActor.Facing.Camera;
	private boolean add = true;
	private double startVariation = 0;
	private double rotationSpeed;

	public ParticleActorBuilder(Definition def) {
		super(def);
		for (DefElement de : def.getElements()) {
			switch (de.name) {
				case TYPE:
				case TRIGGER:
				case ACTOR_LIST:
					break;
				case SPRITE_PATH:
					spritePath = de.getVal();
					break;
				case NB_COL:
					nbCol = de.getIntVal();
					break;
				case NB_ROW:
					nbRow = de.getIntVal();
					break;
				case EMISSION_NODE:
					emissionBone = de.getVal();
					break;
				case DIRECTION_NODE:
					directionBone = de.getVal();
					break;
				case VELOCITY:
					velocity = de.getDoubleVal();
					break;
				case FANNING:
					fanning = de.getDoubleVal();
					break;
				case MAX_COUNT:
					maxCount = de.getIntVal();
					break;
				case PER_SECOND:
					perSecond = de.getIntVal();
					break;
				case DURATION:
					duration = de.getDoubleVal();
					break;
				case START_SIZE:
					startSize = de.getDoubleVal();
					break;
				case END_SIZE:
					endSize = de.getDoubleVal();
					break;
				case START_COLOR:
					startColor = new Color(de.getIntVal(RED), de.getIntVal(GREEN), de.getIntVal(BLUE), de.getIntVal(ALPHA));
					break;
				case END_COLOR:
					endColor = new Color(de.getIntVal(RED), de.getIntVal(GREEN), de.getIntVal(BLUE), de.getIntVal(ALPHA));
					break;
				case MIN_LIFE:
					minLife = de.getDoubleVal();
					break;
				case MAX_LIFE:
					maxLife = de.getDoubleVal();
					break;
				case GRAVITY:
					gravity = de.getBoolVal();
					break;
				case FACING:
					switch (de.getVal()) {
						case FACING_HORIZONTAL:
							facing = ParticleActor.Facing.Horizontal;
							break;
						case FACING_VELOCITY:
							facing = ParticleActor.Facing.Velocity;
							break;
					}
					break;
				case ADD:
					add = de.getBoolVal();
					break;
				case START_VARIATION:
					startVariation = de.getDoubleVal();
					break;
				case ROTATION_SPEED:
					rotationSpeed = AngleUtil.toRadians(de.getDoubleVal());
					break;

				default:
					printUnknownElement(de.name);
			}
		}
	}

	@Override
	public Actor build(String trigger, Actor parent) {
		Actor res = new ParticleActor(spritePath, nbCol, nbRow, emissionBone, directionBone, velocity, fanning, randomSprite, maxCount, perSecond, duration,
				startSize, endSize, startColor, endColor, minLife, maxLife, rotationSpeed, gravity, facing, add, startVariation, parent, trigger,
				childrenTriggers, childrenActorBuilders);
		res.debbug_id = getId();
		return res;
	}
}
