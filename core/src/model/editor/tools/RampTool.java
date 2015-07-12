/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.editor.tools;

import java.util.ArrayList;
import java.util.List;

import model.ModelManager;
import model.battlefield.map.Tile;
import model.battlefield.map.cliff.Ramp;
import model.builders.MapArtisanUtil;
import model.editor.Pencil;

/**
 * @author Benoît
 */
public class RampTool extends Tool {
	private static final String ADD_DELETE_OP = "add/delete";

	public RampTool() {
		super(ADD_DELETE_OP);
	}

	@Override
	protected void createPencil() {
		pencil = new Pencil();
		pencil.sizeIncrement = 0;
		pencil.setUniqueMode();
		pencil.strengthIncrement = 0;
	}

	@Override
	public void primaryAction() {
		Tile t = pencil.getCenterTile();
		if (t.ramp != null) {
			t.ramp.grow(t);
		} else {
			if (!t.hasCliff()) {
				return;
			}
			new Ramp(t);
		}

		List<Tile> changed = new ArrayList<>();
		changed.addAll(t.ramp.getTiles());
		for (Tile t1 : t.ramp.getTiles()) {
			for (Tile n : ModelManager.getBattlefield().getMap().get8Around(t1)) {
				if (!changed.contains(n)) {
					changed.add(n);
				}
				if (n.hasCliff()) {
					n.unsetCliff();
				}
			}
		}
		MapArtisanUtil.updateParcelsFor(changed);
	}

	@Override
	public void secondaryAction() {
	}

	@Override
	public boolean isAnalog() {
		return false;
	}
}
