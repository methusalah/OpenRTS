/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.editor.tools;

import event.EventManager;
import event.ParcelUpdateEvent;
import geometry.geom2d.Point2D;
import geometry.math.RandomUtil;

import java.util.List;

import model.ModelManager;
import model.battlefield.map.Tile;
import model.builders.MapArtisanUtil;
import model.editor.Pencil;
import model.editor.ToolManager;

/**
 * @author Benoît
 */
public class HeightTool extends Tool {
	private static final String RAISE_LOW_OP = "raise/low";
	private static final String NOISE_SMOOTH_OP = "noise/smooth";
	private static final String UNIFOMR_RESET_OP = "uniform/reset";

	private static final int MAX_HEIGHT = 30, MIN_HEIGHT = -10;

	double amplitude = 0.5;
	double maintainedElevation;

	public HeightTool() {
		super(RAISE_LOW_OP, NOISE_SMOOTH_OP, UNIFOMR_RESET_OP);
	}

	@Override
	protected void createPencil() {
		pencil = new Pencil();
		pencil.size = 4;
		pencil.sizeIncrement = 1;
		pencil.strength = 0.5;
		pencil.strengthIncrement = 0.01;
	}

	@Override
	public void primaryAction() {
		List<Tile> group = pencil.getNodes();
		switch (actualOp) {
			case RAISE_LOW_OP:
				raise(group);
				break;
			case NOISE_SMOOTH_OP:
				noise(group);
				break;
			case UNIFOMR_RESET_OP:
				uniform(group);
				break;
		}
		MapArtisanUtil.updateParcelsFor(group);
		MapArtisanUtil.cleanSowing(ModelManager.getBattlefield().getMap(), pencil.getCoord(), pencil.size);
	}

	@Override
	public void secondaryAction() {
		List<Tile> group = pencil.getNodes();
		switch (actualOp) {
			case RAISE_LOW_OP:
				low(group);
				break;
			case NOISE_SMOOTH_OP:
				smooth(group);
				break;
			case UNIFOMR_RESET_OP:
				reset(group);
				break;
		}
		MapArtisanUtil.updateParcelsFor(group);
		MapArtisanUtil.cleanSowing(ModelManager.getBattlefield().getMap(), pencil.getCoord(), pencil.size);
	}

	private void raise(List<Tile> tiles) {
		for (Tile t : tiles) {
			t.elevate(getAttenuatedAmplitude(t.getCoord()));
			if(t.getElevation() > MAX_HEIGHT) {
				t.setElevation(MAX_HEIGHT);
			}
		}
	}

	private void low(List<Tile> tiles) {
		for (Tile t : tiles) {
			t.elevate(-getAttenuatedAmplitude(t.getCoord()));
			if(t.getElevation() < MIN_HEIGHT) {
				t.setElevation(MIN_HEIGHT);
			}
		}
	}

	private void uniform(List<Tile> tiles) {
		if (!pencil.maintained) {
			pencil.maintain();
			maintainedElevation = ModelManager.getBattlefield().getMap().getAltitudeAt(pencil.getCoord());
		}
		for (Tile t : tiles) {
			double diff = maintainedElevation - t.getElevation();
			double attenuatedAmplitude = getAttenuatedAmplitude(t.getCoord());
			if (diff > 0) {
				t.elevate(Math.min(diff, attenuatedAmplitude));
			} else if (diff < 0) {
				t.elevate(Math.max(diff, -attenuatedAmplitude));
			}
		}
	}

	private void noise(List<Tile> tiles) {
		for (Tile t : tiles) {
			t.elevate(RandomUtil.between(-1.0, 1.0) * getAttenuatedAmplitude(t.getCoord()));
		}
	}

	private void smooth(List<Tile> tiles) {
		for (Tile t : tiles) {
			double average = 0;
			for (Tile n : ModelManager.getBattlefield().getMap().get4Around(t)) {
				average += n.getElevation();
			}
			average /= ModelManager.getBattlefield().getMap().get4Around(t).size();

			double diff = average - t.getElevation();
			if (diff > 0) {
				t.elevate(Math.min(diff, getAttenuatedAmplitude(t.getCoord())));
			} else if (diff < 0) {
				t.elevate(Math.max(diff, -getAttenuatedAmplitude(t.getCoord())));
			}
		}
	}

	private void reset(List<Tile> tiles) {
		for (Tile t : tiles) {
			t.setElevation(0);
		}
	}
	
	private double getAttenuatedAmplitude(Point2D p){
		return amplitude * pencil.strength * pencil.getApplicationRatio(p);
	}
}
