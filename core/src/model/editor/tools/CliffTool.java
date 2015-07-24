/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.editor.tools;

import java.util.ArrayList;
import java.util.List;

import model.ModelManager;
import model.battlefield.map.Tile;
import model.builders.MapArtisanUtil;
import model.builders.TileArtisanUtil;
import model.builders.entity.CliffShapeBuilder;
import model.editor.AssetSet;
import model.editor.Pencil;

/**
 * @author Benoît
 */
public class CliffTool extends Tool {
	private static final String RAISE_LOW_OP = "raise/low";
	private static final String FLATTEN_OP = "flatten";

	int maintainedLevel;

	public CliffTool() {
		super(RAISE_LOW_OP, FLATTEN_OP);
		ArrayList<String> iconPaths = new ArrayList<>();
		for (CliffShapeBuilder b : ModelManager.getBattlefield().getMap().getStyle().cliffShapeBuilders) {
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
			maintainedLevel = pencil.getCenterTile().level + 1;
			if (maintainedLevel > 2) {
				maintainedLevel = 2;
			}
		}
		changeLevel();
	}

	private void low() {
		if (!pencil.maintained) {
			pencil.maintain();
			maintainedLevel = pencil.getCenterTile().level - 1;
			if (maintainedLevel < 0) {
				maintainedLevel = 0;
			}
		}
		changeLevel();
	}

	private void flatten() {
		if (!pencil.maintained) {
			pencil.maintain();
			maintainedLevel = pencil.getCenterTile().level;
		}
		changeLevel();
	}

	private void changeLevel(){
		List<Tile> tiles = pencil.getTiles();
		TileArtisanUtil.changeLevel(tiles, maintainedLevel, tiles.get(0).getMap().getStyle().cliffShapeBuilders.get(set.actual).getId());
		MapArtisanUtil.cleanSowing(ModelManager.getBattlefield().getMap(), pencil.getCoord(), pencil.size);
	}
}
