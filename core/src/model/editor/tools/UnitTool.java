/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.editor.tools;

import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import geometry.math.AngleUtil;
import geometry.math.RandomUtil;

import java.util.ArrayList;
import java.util.List;

import model.EntityManager;
import model.ModelManager;
import model.battlefield.army.ArmyManager;
import model.battlefield.army.components.Unit;
import model.battlefield.map.Trinket;
import model.battlefield.warfare.Faction;
import model.builders.entity.UnitBuilder;
import model.builders.entity.definitions.BuilderManager;
import model.editor.AssetSet;
import model.editor.Pencil;
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

	public UnitTool() {
		super(ADD_REMOVE_OP, MOVE_ROTATE_OP);
		List<String> builderIDs = new ArrayList<>();
		for (UnitBuilder b : BuilderManager.getAllUnitBuilders()) {
			builderIDs.add(b.getUIName());
		}
		set = new AssetSet(builderIDs, false);
	}

	@Override
	protected void createPencil() {
		pencil = new Pencil();
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
		for (Unit u : ArmyManager.getUnits()) {
			if (u.getCoord().equals(coord)) {
				coord = coord.getTranslation(RandomUtil.between(AngleUtil.FLAT, -AngleUtil.FLAT), 0.1);
			}
		}
		// TODO: what happend, if there is no Race named "human"?
		Faction f = BuilderManager.getAllUnitBuilders().get(set.actual).hasRace("human") ?
				ModelManager.getBattlefield().getEngagement().getFactions().get(0)
				: ModelManager.getBattlefield().getEngagement().getFactions().get(1);

		Unit u = BuilderManager.getAllUnitBuilders().get(set.actual).build(f, coord.get3D(ModelManager.getBattlefield().getMap().getAltitudeAt(coord)), RandomUtil.between(-AngleUtil.FLAT, AngleUtil.FLAT));
		u.drawOnBattlefield();
		ArmyManager.registerUnit(u);
	}

	private void remove() {
		Unit u = getPointedUnit(); 
		if (u != null) {
			ArmyManager.unregisterUnit(u);
		}
	}

	private void move() {
		if (!pencil.maintained) {
			pencil.maintain();
			actualUnit = null;
			Unit u = getPointedUnit();
			if (u != null) {
				actualUnit = u;
				moveOffset = pencil.getCoord().getSubtraction(u.getCoord());
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
			Unit u = getPointedUnit();
			if (u != null) {
				actualUnit = u;
			}
		}
		if (actualUnit != null) {
			actualUnit.setOrientation(pencil.getCoord().getSubtraction(actualUnit.getCoord()).getAngle());
			actualUnit.setDirection(Point3D.UNIT_X.getRotationAroundZ(actualUnit.getOrientation()));
		}
	}
	
	private Unit getPointedUnit(){
		if (EntityManager.isValidId(ToolManager.getPointedSpatialEntityId())) {
			return ArmyManager.getUnit(ToolManager.getPointedSpatialEntityId());
		}
		return null;
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
