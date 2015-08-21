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
import model.battlefield.map.Trinket;
import model.builders.MapArtisanUtil;
import model.builders.entity.TrinketBuilder;
import model.builders.entity.definitions.BuilderManager;
import model.editor.AssetSet;
import model.editor.Pencil;
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

	public TrinketTool() {
		super(ADD_REMOVE_OP, MOVE_ROTATE_OP);
		List<String> builderIDs = new ArrayList<>();
		for (TrinketBuilder b : BuilderManager.getAllEditableTrinketBuilders()) {
			builderIDs.add(b.getId());
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
		for (Trinket t : ModelManager.getBattlefield().getMap().get(coord).getData(Trinket.class)) {
			if (t.pos.equals(coord)) {
				coord = coord.getTranslation(RandomUtil.between(AngleUtil.FLAT, -AngleUtil.FLAT), 0.1);
			}
		}
		Trinket t = BuilderManager.getAllEditableTrinketBuilders().get(set.actual)
				.build(coord.get3D(ModelManager.getBattlefield().getMap().getAltitudeAt(coord)));
		MapArtisanUtil.attachTrinket(t, ModelManager.getBattlefield().getMap());
		t.drawOnBattlefield();
	}

	private void remove() {
		Trinket toRemove = getPointedTrinket();
		if (toRemove != null) {
			MapArtisanUtil.dettachTrinket(toRemove, ModelManager.getBattlefield().getMap());
			toRemove.removeFromBattlefield();
		}
	}

	private void move() {
		if (!pencil.maintained) {
			pencil.maintain();
			actualTrinket = getPointedTrinket();
			actualTrinket.sowed = false;
			if(actualTrinket != null) {
				moveOffset = pencil.getCoord().getSubtraction(actualTrinket.getCoord());
			}
		}
		if (actualTrinket != null) {
			// TODO attention, l'elevation n'est pas forcement juste avec ce calcul
			double elevation = actualTrinket.pos.z - ModelManager.getBattlefield().getMap().getAltitudeAt(actualTrinket.pos.get2D());
			Point2D newPos = pencil.getCoord().getSubtraction(moveOffset);
			double z = ModelManager.getBattlefield().getMap().getAltitudeAt(newPos) + elevation;
			actualTrinket.pos = newPos.get3D(z);
			MapArtisanUtil.cleanSowing(ModelManager.getBattlefield().getMap(), pencil.getCoord(), actualTrinket.getRadius());
		}
	}

	private void rotate() {
		if (!pencil.maintained) {
			pencil.maintain();
			actualTrinket = getPointedTrinket();
			actualTrinket.sowed = false;
		}
		if (actualTrinket != null) {
			actualTrinket.setOrientation(pencil.getCoord().getSubtraction(actualTrinket.getCoord()).getAngle());
			actualTrinket.setDirection(Point3D.UNIT_X.getRotationAroundZ(actualTrinket.getOrientation()));
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

	private Trinket getPointedTrinket(){
		if (EntityManager.isValidId(ToolManager.getPointedSpatialEntityId())) {
			return ModelManager.getBattlefield().getMap().getTrinket(ToolManager.getPointedSpatialEntityId());
		}
		return null;
	}
}
