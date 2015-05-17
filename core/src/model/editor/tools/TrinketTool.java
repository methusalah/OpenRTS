/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.editor.tools;

import geometry.geom2d.Point2D;
import geometry.math.Angle;
import geometry.math.MyRandom;

import java.util.ArrayList;

import model.EntityManager;
import model.ModelManager;
import model.battlefield.map.Trinket;
import model.builders.TrinketBuilder;
import model.editor.Pencil;
import model.editor.Set;
import model.editor.ToolManager;

/**
 * @author bedu
 */
public class TrinketTool extends Tool {
	private static final String ADD_REMOVE_OP = "add/remove";
	private static final String MOVE_ROTATE_OP = "move/rotate";

	Trinket actualTrinket;
	Point2D moveOffset;
	boolean analog = false;

	double angle = 0;

	public TrinketTool(ToolManager manager) {
		super(manager, ADD_REMOVE_OP, MOVE_ROTATE_OP);
		ArrayList<String> builderIDs = new ArrayList<>();
		for (TrinketBuilder b : ModelManager.lib.getAllEditableTrinketBuilders()) {
			builderIDs.add(b.getId());
		}
		set = new Set(builderIDs, false);
	}

	@Override
	protected void createPencil() {
		pencil = new Pencil(ModelManager.battlefield.map);
		pencil.sizeIncrement = 0;
		pencil.strengthIncrement = 0;
		pencil.setUniqueMode();
	}

	@Override
	public void primaryAction() {
		switch (actualOp) {
			case ADD_REMOVE_OP:
				add();
				break;
			case MOVE_ROTATE_OP:
				move();
				break;
		}
	}

	@Override
	public void secondaryAction() {
		switch (actualOp) {
			case ADD_REMOVE_OP:
				remove();
				break;
			case MOVE_ROTATE_OP:
				rotate();
				break;
		}
	}

	private void add() {
		Point2D pos = pencil.getCoord();
		for (Trinket t : ModelManager.battlefield.map.trinkets) {
			if (t.pos.equals(pos)) {
				pos = pos.getTranslation(MyRandom.between(Angle.FLAT, -Angle.FLAT), 0.1);
			}
		}
		Trinket t = ModelManager.lib.getAllEditableTrinketBuilders().get(set.actual).build(pos.get3D(ModelManager.battlefield.map.getAltitudeAt(pos)));
		ModelManager.battlefield.map.trinkets.add(t);
	}

	private void remove() {
		Trinket toRemove = null;
		if (EntityManager.isValidId(manager.getPointedSpatialEntityId())) {
			for (Trinket t : ModelManager.battlefield.map.trinkets) {
				if (t.getId() == manager.getPointedSpatialEntityId()) {
					toRemove = t;
					break;
				}
			}
		}
		if (toRemove != null) {
			ModelManager.battlefield.map.trinkets.remove(toRemove);
			toRemove.removeFromBattlefield();
		}
	}

	private void move() {
		if (!pencil.maintained) {
			pencil.maintain();
			actualTrinket = null;
			if (EntityManager.isValidId(manager.getPointedSpatialEntityId())) {
				for (Trinket t : ModelManager.battlefield.map.trinkets) {
					if (t.getId() == manager.getPointedSpatialEntityId()) {
						actualTrinket = t;
						moveOffset = pencil.getCoord().getSubtraction(t.pos.get2D());
						break;
					}
				}
			}
		}
		if (actualTrinket != null) {
			// TODO attention, l'elevation n'est pas forcement juste avec ce calcul
			double elevation = actualTrinket.pos.z - ModelManager.battlefield.map.getAltitudeAt(actualTrinket.pos.get2D());
			Point2D newPos = pencil.getCoord().getSubtraction(moveOffset);
			double z = ModelManager.battlefield.map.getAltitudeAt(newPos) + elevation;
			actualTrinket.pos = newPos.get3D(z);
		}
	}

	private void rotate() {
		if (!pencil.maintained) {
			pencil.maintain();
			actualTrinket = null;
			if (EntityManager.isValidId((manager.getPointedSpatialEntityId()))) {
				for (Trinket t : ModelManager.battlefield.map.trinkets) {
					if (t.getId() == manager.getPointedSpatialEntityId()) {
						actualTrinket = t;
						break;
					}
				}
			}
		}
		if (actualTrinket != null) {
			actualTrinket.yaw = pencil.getCoord().getSubtraction(actualTrinket.pos.get2D()).getAngle();
		}
	}

	@Override
	public boolean isAnalog() {
		return analog;
	}

	@Override
	public void setOperation(int index) {
		super.setOperation(index);
		analog = actualOp.equals(MOVE_ROTATE_OP);
	}

	@Override
	public void toggleOperation() {
		super.toggleOperation();
		analog = actualOp.equals(MOVE_ROTATE_OP);
	}
}
