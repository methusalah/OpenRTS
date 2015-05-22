/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.editor.tools;

import java.util.ArrayList;
import java.util.List;

import model.ModelManager;
import model.battlefield.map.Tile;
import model.battlefield.map.cliff.Cliff;
import model.builders.CliffShapeBuilder;
import model.editor.AssetSet;
import model.editor.Pencil;
import model.editor.ToolManager;

/**
 * @author Benoît
 */
public class CliffTool extends Tool {
	private static final String RAISE_LOW_OP = "raise/low";
	private static final String FLATTEN_OP = "flatten";

	int maintainedlevel;

	public CliffTool(ToolManager manager) {
		super(manager, RAISE_LOW_OP, FLATTEN_OP);
		ArrayList<String> iconPaths = new ArrayList<>();
		for (CliffShapeBuilder b : ModelManager.getBattlefield().getMap().style.cliffShapeBuilders) {
			iconPaths.add(b.getIconPath());
		}
		set = new AssetSet(iconPaths, true);
	}

	@Override
	protected void createPencil() {
		pencil = new Pencil();
		pencil.snapPair = true;
		pencil.size = 4;
		pencil.sizeIncrement = 2;
		pencil.setUniqueMode();
		pencil.strengthIncrement = 0;
	}

	@Override
	public void primaryAction() {
		switch (actualOp) {
			case RAISE_LOW_OP:
				raise();
				break;
			case FLATTEN_OP:
				flatten();
				break;
		}
	}

	@Override
	public void secondaryAction() {
		switch (actualOp) {
			case RAISE_LOW_OP:
				low();
				break;
			case FLATTEN_OP:
				break;
		}
	}

	private void raise() {
		if (!pencil.maintained) {
			pencil.maintain();
			maintainedlevel = pencil.getCenterTile().level + 1;
			if (maintainedlevel > 2) {
				maintainedlevel = 2;
			}
		}
		changeLevel();
	}

	private void low() {
		if (!pencil.maintained) {
			pencil.maintain();
			maintainedlevel = pencil.getCenterTile().level - 1;
			if (maintainedlevel < 0) {
				maintainedlevel = 0;
			}
		}
		changeLevel();
	}

	private void flatten() {
		if (!pencil.maintained) {
			pencil.maintain();
			maintainedlevel = pencil.getCenterTile().level;
		}
		changeLevel();
	}

	private void changeLevel() {
		List<Tile> group = pencil.getTiles();

		List<Tile> toUpdate = new ArrayList<>();
		for (Tile t : group) {
			t.level = maintainedlevel;
			if (t.ramp != null) {
				toUpdate.addAll(t.ramp.destroy());
			}
		}
		group.addAll(toUpdate);
		manager.updateTiles(group);
	}

	public void buildShape(Cliff cliff) {
		ModelManager.getBattlefield().getMap().style.cliffShapeBuilders.get(set.actual).build(cliff);
	}

}
