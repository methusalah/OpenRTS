/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.editor.tools;

import geometry.geom2d.Point2D;
import geometry.geom2d.algorithm.PoissonDiscSampler;
import geometry.math.Angle;
import geometry.math.MyRandom;
import geometry.tools.LogUtil;

import java.util.ArrayList;
import java.util.List;

import model.EntityManager;
import model.ModelManager;
import model.battlefield.map.Map;
import model.battlefield.map.Tile;
import model.battlefield.map.Trinket;
import model.builders.MapArtisan;
import model.builders.entity.TrinketBuilder;
import model.builders.entity.definitions.BuilderManager;
import model.editor.AssetSet;
import model.editor.Pencil;
import model.editor.ToolManager;
import model.editor.engines.Sower;

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
		Point2D pos = pencil.getCoord();
		for (Trinket t : ModelManager.getBattlefield().getMap().get(pos).getData(Trinket.class)) {
			if (t.pos.equals(pos)) {
				pos = pos.getTranslation(MyRandom.between(Angle.FLAT, -Angle.FLAT), 0.1);
			}
		}
		Trinket t = BuilderManager.getAllEditableTrinketBuilders().get(set.actual)
				.build(pos.get3D(ModelManager.getBattlefield().getMap().getAltitudeAt(pos)));
		MapArtisan.attachTrinket(t, ModelManager.getBattlefield().getMap());
		t.drawOnBattlefield();
	}

	private void remove() {
		Trinket toRemove = getPointedTrinket();
		if (toRemove != null) {
			MapArtisan.dettachTrinket(toRemove, ModelManager.getBattlefield().getMap());
			toRemove.removeFromBattlefield();
		}
	}

	private void move() {
		if (!pencil.maintained) {
			pencil.maintain();
			actualTrinket = getPointedTrinket();
			if(actualTrinket != null)
				moveOffset = pencil.getCoord().getSubtraction(actualTrinket.getCoord());
		}
		if (actualTrinket != null) {
			// TODO attention, l'elevation n'est pas forcement juste avec ce calcul
			double elevation = actualTrinket.pos.z - ModelManager.getBattlefield().getMap().getAltitudeAt(actualTrinket.pos.get2D());
			Point2D newPos = pencil.getCoord().getSubtraction(moveOffset);
			double z = ModelManager.getBattlefield().getMap().getAltitudeAt(newPos) + elevation;
			actualTrinket.pos = newPos.get3D(z);
		}
	}

	private void rotate() {
		if (!pencil.maintained) {
			pencil.maintain();
			actualTrinket = getPointedTrinket();
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
	
	private Trinket getPointedTrinket(){
		if (EntityManager.isValidId(ToolManager.getPointedSpatialEntityId())) {
			return ModelManager.getBattlefield().getMap().getTrinket(ToolManager.getPointedSpatialEntityId());
		}
		return null;
	}
}
