/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.editor.tools;

import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import geometry.tools.LogUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import model.ModelManager;
import model.battlefield.map.atlas.Atlas;
import model.battlefield.map.atlas.AtlasExplorer;
import model.battlefield.map.atlas.AtlasLayer;
import model.editor.AssetSet;
import model.editor.Pencil;
import model.editor.ToolManager;

/**
 * @author Benoît
 */
public class AtlasTool extends Tool {
	private static final String ADD_DELETE_OP = "add/delete";
	private static final String PROPAGATE_SMOOTH_OP = "propagate/smooth";

	AtlasExplorer explorer;

	int autoLayer;
	double increment = 40;

	public AtlasTool() {
		super(ADD_DELETE_OP, PROPAGATE_SMOOTH_OP);
		explorer = new AtlasExplorer(ModelManager.getBattlefield().getMap());
		List<String> allTextures = ModelManager.getBattlefield().getMap().style.diffuses;
		while(allTextures.size() < 8)
			allTextures.add(null);
		allTextures.addAll(ModelManager.getBattlefield().getMap().style.coverDiffuses);
		set = new AssetSet(allTextures, true);
	}

	@Override
	protected void createPencil() {
		pencil = new Pencil();
		pencil.size = 2;
		pencil.sizeIncrement = 0.25;
		pencil.strength = 0.5;
		pencil.strengthIncrement = 0.01;
	}

	@Override
	public void primaryAction() {
		switch (actualOp) {
			case ADD_DELETE_OP:
				increment(getInvolvedPixels());
				break;
			case PROPAGATE_SMOOTH_OP:
				propagate(getInvolvedPixels());
				break;
		}

		ToolManager.updateGroundAtlas();
	}

	@Override
	public void secondaryAction() {
		switch (actualOp) {
			case ADD_DELETE_OP:
				decrement(getInvolvedPixels());
				break;
			case PROPAGATE_SMOOTH_OP:
				smooth(getInvolvedPixels());
				break;
		}
		ToolManager.updateGroundAtlas();
	}

	public ArrayList<Point2D> getInvolvedPixels() {
		switch (pencil.shape) {
			case Circle:
				return explorer.getPixelsInMapSpaceCircle(pencil.getCoord(), pencil.size / 2);
			case Diamond:
				return explorer.getPixelsInMapSpaceDiamond(pencil.getCoord(), pencil.size / 2);
			case Square:
				return explorer.getPixelsInMapSpaceSquare(pencil.getCoord(), pencil.size / 2);
			default:
				throw new RuntimeException();
		}
	}

	private void increment(ArrayList<Point2D> pixels) {
		for (Point2D p : pixels) {
			increment(p, set.actual);
		}
	}

	private void increment(Point2D p, int layer) {
		Atlas toPaint = ModelManager.getBattlefield().getMap().atlas;
		if(layer >= 8){
			toPaint = ModelManager.getBattlefield().getMap().cover;
			layer -= 8; 
		}
		
		int x = (int) Math.round(p.x);
		int y = (int) Math.round(p.y);
		double attenuatedInc = Math.round(increment * pencil.strength * pencil.getApplicationRatio(explorer.getInMapSpace(p)));

		double valueToDitribute = attenuatedInc;
		ArrayList<AtlasLayer> availableLayers = new ArrayList<>();
		for (AtlasLayer l : toPaint.getLayers()) {
			if (toPaint.getLayers().indexOf(l) == layer) {
				valueToDitribute -= l.addAndReturnExcess(x, y, attenuatedInc);
			} else {
				if(l.get(x, y) > 0)
					availableLayers.add(l);
			}
		}
		int secur = -1;
		while (valueToDitribute > 0 && !availableLayers.isEmpty() && secur++ < 50) {
			ArrayList<AtlasLayer> unavailableLayers = new ArrayList<>();
			double shared = Math.round(valueToDitribute / availableLayers.size());
			valueToDitribute = 0;
			for (AtlasLayer l : availableLayers) {
				valueToDitribute += l.withdrawAndReturnExcess(x, y, shared);
				if (l.get(x, y) == 0) {
					unavailableLayers.add(l);
				}
			}
			availableLayers.removeAll(unavailableLayers);
		}
		if (secur > 40) {
			LogUtil.logger.warning("Impossible to distribute value");
		}
		toPaint.updatePixel(x, y);
	}

	private void decrement(ArrayList<Point2D> pixels) {
		for (Point2D p : pixels) {
			decrement(p, set.actual);
		}
	}

	private void decrement(Point2D p, int layer) {
		Atlas toPaint = ModelManager.getBattlefield().getMap().atlas;
		if(layer >= 8){
			toPaint = ModelManager.getBattlefield().getMap().cover;
			layer -= 8; 
		}
		
		int x = (int) Math.round(p.x);
		int y = (int) Math.round(p.y);
		double attenuatedInc = Math.round(increment * pencil.strength * pencil.getApplicationRatio(explorer.getInMapSpace(p)));

		double valueToDitribute = attenuatedInc;
		ArrayList<AtlasLayer> availableLayers = new ArrayList<>();
		for (AtlasLayer l : toPaint.getLayers()) {
			if (toPaint.getLayers().indexOf(l) == layer) {
				valueToDitribute -= l.withdrawAndReturnExcess(x, y, attenuatedInc);
			} else if (l.get(x, y) > 0) {
				availableLayers.add(l);
			}
		}
		if (availableLayers.isEmpty()) {
			availableLayers.add(toPaint.getLayers().get(0));
		}

		int secur = -1;
		while (valueToDitribute > 0 && !availableLayers.isEmpty() && secur++ < 50) {
			ArrayList<AtlasLayer> unavailableLayers = new ArrayList<>();
			double shared = valueToDitribute / availableLayers.size();
			valueToDitribute = 0;
			for (AtlasLayer l : availableLayers) {
				valueToDitribute += l.addAndReturnExcess(x, y, shared);
				if (l.get(x, y) == 255) {
					unavailableLayers.add(l);
				}
			}
			availableLayers.removeAll(unavailableLayers);
		}
		if (secur > 40) {
			LogUtil.logger.warning("Impossible to distribute value");
		}
		toPaint.updatePixel(x, y);
	}

	private void propagate(ArrayList<Point2D> pixels) {
		if (!pencil.maintained) {
			pencil.maintain();
			autoLayer = 0;
			Point2D center = pencil.getCoord().getMult(ModelManager.getBattlefield().getMap().atlas.getWidth(), ModelManager.getBattlefield().getMap().atlas.getHeight())
					.getDivision(ModelManager.getBattlefield().getMap().width, ModelManager.getBattlefield().getMap().height);
			int centerX = (int) Math.round(center.x);
			int centerY = (int) Math.round(center.y);
			for (AtlasLayer l : ModelManager.getBattlefield().getMap().atlas.getLayers()) {
				if (l.get(centerX, centerY) > ModelManager.getBattlefield().getMap().atlas.getLayers().get(autoLayer).get(centerX, centerY)) {
					autoLayer = ModelManager.getBattlefield().getMap().atlas.getLayers().indexOf(l);
				}
			}
		}
		for (Point2D p : pixels) {
			increment(p, autoLayer);
		}
	}

	private void smooth(ArrayList<Point2D> pixels) {
		for (Point2D p : pixels) {
			int x = (int) Math.round(p.x);
			int y = (int) Math.round(p.y);
			double attenuatedInc = Math.round(increment
					* pencil.strength
					* pencil.getApplicationRatio(new Point2D(x, y).getMult(ModelManager.getBattlefield().getMap().width,
							ModelManager.getBattlefield().getMap().height).getDivision(ModelManager.getBattlefield().getMap().atlas.getWidth(), ModelManager.getBattlefield().getMap().atlas.getHeight())));

			int activeLayerCount = 0;
			for (AtlasLayer l : ModelManager.getBattlefield().getMap().atlas.getLayers()) {
				if (l.get(x, y) != 0) {
					activeLayerCount++;
				}
			}
			double targetVal = 255 / activeLayerCount;
			for (AtlasLayer l : ModelManager.getBattlefield().getMap().atlas.getLayers()) {
				if (l.get(x, y) != 0) {
					double diff = targetVal - l.get(x, y);
					if (diff < 0) {
						l.set(x, y, l.get(x, y) + Math.max(diff, -attenuatedInc));
					} else if (diff > 0) {
						l.set(x, y, l.get(x, y) + Math.min(diff, attenuatedInc));
					}
				}
			}
			ModelManager.getBattlefield().getMap().atlas.updatePixel(x, y);
		}

	}
}
