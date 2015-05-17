/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.editor.tools;

import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import geometry.math.Angle;
import geometry.math.MyRandom;

import java.util.ArrayList;

import model.EntityManager;
import model.ModelManager;
import model.battlefield.army.components.Unit;
import model.battlefield.warfare.Faction;
import model.builders.UnitBuilder;
import model.editor.Pencil;
import model.editor.Set;
import model.editor.ToolManager;

/**
 * @author bedu
 */
public class UnitTool extends Tool {
	private static final String ADD_REMOVE_OP = "add/remove";
	private static final String MOVE_ROTATE_OP = "move/rotate";

	Unit actualUnit;
	Point2D moveOffset;
	boolean analog = false;

	double angle = 0;

	public UnitTool(ToolManager manager) {
		super(manager, ADD_REMOVE_OP, MOVE_ROTATE_OP);
		ArrayList<String> builderIDs = new ArrayList<>();
		for (UnitBuilder b : ModelManager.lib.getAllUnitBuilders()) {
			builderIDs.add(b.getUIName());
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
		Point2D coord = pencil.getCoord();
		for (Unit u : ModelManager.battlefield.armyManager.getUnits()) {
			if (u.getPos2D().equals(coord)) {
				coord = coord.getTranslation(MyRandom.between(Angle.FLAT, -Angle.FLAT), 0.1);
			}
		}
		// TODO: what happend, if there is no Race named "human"?
		Faction f = ModelManager.lib.getAllUnitBuilders().get(set.actual).hasRace("human") ? ModelManager.battlefield.engagement.factions.get(0)
				: ModelManager.battlefield.engagement.factions.get(1);

		ModelManager.battlefield.engagement.addUnit(ModelManager.lib.getAllUnitBuilders().get(set.actual)
				.build(f, coord.get3D(0), MyRandom.between(-Angle.FLAT, Angle.FLAT)));
	}

	private void remove() {
		if (EntityManager.isValidId(manager.getPointedSpatialEntityId())) {
			Unit u = ModelManager.battlefield.armyManager.getUnit(manager.getPointedSpatialEntityId());
			if (u != null) {
				ModelManager.battlefield.engagement.removeUnit(u);
			}
		}
	}

	private void move() {
		if (!pencil.maintained) {
			pencil.maintain();
			actualUnit = null;
			if (EntityManager.isValidId(manager.getPointedSpatialEntityId())) {
				Unit u = ModelManager.battlefield.armyManager.getUnit(manager.getPointedSpatialEntityId());
				if (u != null) {
					actualUnit = u;
					moveOffset = pencil.getCoord().getSubtraction(u.getPos2D());
				}
			}
		}
		if (actualUnit != null) {
			actualUnit.mover.changeCoord(pencil.getCoord().getSubtraction(moveOffset));
		}
	}

	private void rotate() {
		if (!pencil.maintained) {
			pencil.maintain();
			actualUnit = null;
			if (EntityManager.isValidId(manager.getPointedSpatialEntityId())) {
				Unit u = ModelManager.battlefield.armyManager.getUnit(manager.getPointedSpatialEntityId());
				if (u != null) {
					actualUnit = u;
				}
			}
		}
		if (actualUnit != null) {
			actualUnit.yaw = pencil.getCoord().getSubtraction(actualUnit.getPos2D()).getAngle();
			actualUnit.direction = Point3D.UNIT_X.getRotationAroundZ(actualUnit.yaw);
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
