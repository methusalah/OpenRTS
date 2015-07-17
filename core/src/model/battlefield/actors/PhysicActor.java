package model.battlefield.actors;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.builders.entity.actors.ActorBuilder;

/**
 * A special Model Actor that must be managed by the physical engine. The life of this actor is limited and managed by the view. The life decrease only when the
 * model is completely static to prevent moving models to disappear. Only the view knows the physical state of this actor.
 *
 * @author Benoît
 */
public class PhysicActor extends ModelActor {
	public final double startLife;
	public final double mass;
	public final String massCenterBone;

	public double life;
	public long timer;
	public boolean launched = false;

	public PhysicActor(String modelPath,
			double scale,
			double startLife,
			double mass,
			String massCenterBone,
			Actor parent,
			String trigger,
			List<String> childrenTriggers,
			List<ActorBuilder> childrenBuilders,
			double yaw,
			double pitch,
			double roll,
			Color color,
			HashMap<String, Color> subColorsByName,
			HashMap<Integer, Color> subColorsByIndex,
			Map<String, String> materialsByName,
			Map<Integer, String> materialsByIndex
			) {
		super(parent, trigger, childrenTriggers, childrenBuilders, modelPath, scale, scale, scale, yaw, pitch, roll, color, subColorsByName, subColorsByIndex, materialsByName, materialsByIndex, null);
		this.startLife = startLife;
		this.mass = mass;
		this.massCenterBone = massCenterBone;
	}

	@Override
	public String getType() {
		return "physic";
	}

	public ModelActor getParentModelActor() {
		Actor parent = this;
		do {
			parent = parent.getParent();
			if (parent == null) {
				throw new RuntimeException(this.getClass().getSimpleName() + " seems to miss a modelActor parent");
			}

		} while (!parent.containsModel());
		return (ModelActor) parent;
	}

	public boolean alive() {
		return life > 0;
	}

}
