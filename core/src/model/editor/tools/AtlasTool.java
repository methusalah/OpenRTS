/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.editor.tools;

import geometry.geom2d.Point2D;

import java.util.ArrayList;
import java.util.List;

import model.ModelManager;
import model.battlefield.map.atlas.Atlas;
import model.battlefield.map.atlas.AtlasExplorer;
import model.battlefield.map.atlas.AtlasLayer;
import model.builders.AtlasArtisanUtil;
import model.builders.MapArtisanUtil;
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

	AtlasLayer autoLayer;
	double increment = 40;

	public AtlasTool() {
		super(ADD_DELETE_OP, PROPAGATE_SMOOTH_OP);
		explorer = new AtlasExplorer(ModelManager.getBattlefield().getMap());
		List<String> allTextures = new ArrayList<>();
		allTextures.addAll(ModelManager.getBattlefield().getMap().getStyle().diffuses);
		while(allTextures.size() < 8) {
			allTextures.add(null);
		}
		allTextures.addAll(ModelManager.getBattlefield().getMap().getStyle().coverDiffuses);
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
		MapArtisanUtil.cleanSowing(ModelManager.getBattlefield().getMap(), pencil.getCoord(), pencil.size / 2);
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
		MapArtisanUtil.cleanSowing(ModelManager.getBattlefield().getMap(), pencil.getCoord(), pencil.size / 2);
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
		Atlas toPaint = getAtlasToPaint();
		AtlasLayer layer = getActualLayer(toPaint);

		for (Point2D p : pixels) {
			AtlasArtisanUtil.incrementPixel(toPaint, p, layer, getAttenuatedIncrement(p));
		}
	}

	private void decrement(ArrayList<Point2D> pixels) {
		Atlas toPaint = getAtlasToPaint();
		AtlasLayer layer = getActualLayer(toPaint);

		for (Point2D p : pixels) {
			AtlasArtisanUtil.decrementPixel(toPaint, p, layer, getAttenuatedIncrement(p));
		}
	}

	private void propagate(ArrayList<Point2D> pixels) {
		Atlas atlas = ModelManager.getBattlefield().getMap().getAtlas();
		if (!pencil.maintained) {
			pencil.maintain();
			autoLayer = atlas.getLayers().get(0);
			Point2D center = explorer.getInAtlasSpace(pencil.getCoord());
			int centerX = (int) Math.round(center.x);
			int centerY = (int) Math.round(center.y);
			for (AtlasLayer l : atlas.getLayers()) {
				if (l.get(centerX, centerY) > autoLayer.get(centerX, centerY)) {
					autoLayer = l;
				}
			}
		}
		for (Point2D p : pixels) {
			AtlasArtisanUtil.incrementPixel(atlas, p, autoLayer, getAttenuatedIncrement(p));
		}
	}

	private void smooth(ArrayList<Point2D> pixels) {
		for (Point2D p : pixels) {
			AtlasArtisanUtil.smoothPixel(ModelManager.getBattlefield().getMap().getAtlas(), p, getAttenuatedIncrement(p));
		}

	}

	private AtlasLayer getActualLayer(Atlas atlas){
		if(set.actual > 8) {
			return atlas.getLayers().get(set.actual-8);
		}
		return atlas.getLayers().get(set.actual);
	}

	private Atlas getAtlasToPaint(){
		if(set.actual > 8) {
			return ModelManager.getBattlefield().getMap().getCover();
		}
		return ModelManager.getBattlefield().getMap().getAtlas();
	}

	private double getAttenuatedIncrement(Point2D p){
		return Math.round(increment * pencil.strength * pencil.getApplicationRatio(explorer.getInMapSpace(p)));
	}
}
