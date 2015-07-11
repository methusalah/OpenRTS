package model.battlefield.actors;

import geometry.geom3d.Point3D;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import model.battlefield.abstractComps.FieldComp;
import model.builders.entity.actors.ActorBuilder;

/**
 * Contains the path to a model to draw. This actor also hold the model bone coordinates given by the view. These coordinates may be useful in the model. IE :
 * Only the view knows where is the canon muzzle bone where the projectile needs to appear.
 */
public class ModelActor extends Actor {

	private static final Logger logger = Logger.getLogger(ModelActor.class.getName());

	private final String modelPath;
	private final double scaleX;
	private final double scaleY;
	private final double scaleZ;
	private final double yawFix;
	private final double pitchFix;
	private final double rollFix;
	private final Color color;
	private final HashMap<String, Color> subColorsByName;
	private final HashMap<Integer, Color> subColorsByIndex;
	final FieldComp comp;

	private Map<String, Point3D> boneCoords = new HashMap<>();

	public ModelActor(Actor parent,
			String trigger,
			List<String> childrenTriggers,
			List<ActorBuilder> childrenBuilders,
			String modelPath,
			double scaleX,
			double scaleY,
			double scaleZ,
			double yaw,
			double pitch,
			double roll,
			Color color,
			HashMap<String, Color> subColorsByName,
			HashMap<Integer, Color> subColorsByIndex,
			FieldComp comp) {
		super(parent, trigger, childrenTriggers, childrenBuilders);
		this.modelPath = modelPath;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;
		this.yawFix = yaw;
		this.pitchFix = pitch;
		this.rollFix = roll;
		this.color = color;
		this.subColorsByName = subColorsByName;
		this.subColorsByIndex = subColorsByIndex;
		this.comp = comp;
	}


	public String getLabel(){
		return "";
	}

	@Override
	public boolean containsModel() {
		return true;
	}

	public Point3D getBoneCoord(String boneName){
		Point3D res = boneCoords.get(boneName);
		if(res == null) {
			throw new IllegalArgumentException("Can't find bone "+boneName);
		}
		return res;
	}

	public void setBone(String name, Point3D coord){
		boneCoords.put(name, coord);
	}

	public boolean hasBone(){
		return !boneCoords.isEmpty();
	}
	public boolean hasBone(String boneName){
		return boneCoords.get(boneName) != null;
	}
	public void debbugWriteBoneNames(){
		logger.info("" + boneCoords.keySet());
	}


	@Override
	public String getType() {
		return "model";
	}

	public FieldComp getComp(){
		return comp;
	}

	public Point3D getPos(){
		return comp.getPos();
	}

	public double getYaw(){
		return comp.getYaw();
	}

	public Color getColor() {
		return color;
	}

	public String getModelPath() {
		return modelPath;
	}

	public double getScaleZ() {
		return scaleZ;
	}

	public double getScaleX() {
		return scaleX;
	}

	public double getScaleY() {
		return scaleY;
	}


	public double getYawFix() {
		return yawFix;
	}


	public double getPitchFix() {
		return pitchFix;
	}


	public double getRollFix() {
		return rollFix;
	}


	public HashMap<String, Color> getSubColorsByName() {
		return subColorsByName;
	}


	public HashMap<Integer, Color> getSubColorsByIndex() {
		return subColorsByIndex;
	}



}
