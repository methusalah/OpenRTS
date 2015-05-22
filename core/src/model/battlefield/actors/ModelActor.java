package model.battlefield.actors;

import geometry.geom3d.Point3D;
import geometry.tools.LogUtil;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.battlefield.abstractComps.FieldComp;
import model.builders.actors.ActorBuilder;

/**
 * Contains the path to a model to draw. This actor also hold the model bone coordinates given by the view. These coordinates may be useful in the model. IE :
 * Only the view knows where is the canon muzzle bone where the projectile needs to appear.
 */
public class ModelActor extends Actor {
	private final String modelPath;
	private final double scaleX;
	private final double scaleY;
	private final double scaleZ;
	private final Color color;
	final FieldComp comp;

	private Map<String, Point3D> boneCoords = new HashMap<>();

	public ModelActor(Actor parent,
			String trigger,
			List<String> childrenTriggers,
			List<ActorBuilder> childrenBuilders,
			ActorPool pool,
			String modelPath,
			double scaleX,
			double scaleY,
			double scaleZ,
			Color color,
			FieldComp comp) {
		super(parent, trigger, childrenTriggers, childrenBuilders, pool);
		this.modelPath = modelPath;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;
		this.color = color;
		this.comp = comp;
	}

	@Override
	public void act() {
		// TODO Auto-generated method stub
		super.act();
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
		LogUtil.logger.info(""+boneCoords.keySet());
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

	public Point3D getDirection(){
		return comp.direction;
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
}
