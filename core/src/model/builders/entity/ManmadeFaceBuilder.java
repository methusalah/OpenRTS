/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.builders.entity;

import geometry.math.RandomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import model.battlefield.map.cliff.Cliff;
import model.battlefield.map.cliff.faces.manmade.CornerManmadeFace;
import model.battlefield.map.cliff.faces.manmade.ManmadeFace;
import model.battlefield.map.cliff.faces.manmade.OrthogonalManmadeFace;
import model.battlefield.map.cliff.faces.manmade.SalientManmadeFace;
import model.builders.entity.definitions.DefElement;
import model.builders.entity.definitions.Definition;

/**
 * @author Benoît
 */
public class ManmadeFaceBuilder extends Builder {

	private static final Logger logger = Logger.getLogger(ManmadeFaceBuilder.class.getName());

	private static final String ORTHOGONAL_LIST = "OrthogonalList";
	private static final String SALIENT_LIST = "SalientList";
	private static final String CORNER_LIST = "CornerList";
	private static final String PATH = "path";
	private static final String WEIGHT = "weight";

	private List<String> orthos = new ArrayList<>();
	private List<String> salients = new ArrayList<>();
	private List<String> corners = new ArrayList<>();

	public ManmadeFaceBuilder(Definition def) {
		super(def);
		for (DefElement de : def.getElements()) {
			switch (de.name) {
				case ORTHOGONAL_LIST:
					addWithWheight(de.getVal(PATH), de.getIntVal(WEIGHT), orthos);
					break;
				case SALIENT_LIST:
					addWithWheight(de.getVal(PATH), de.getIntVal(WEIGHT), salients);
					break;
				case CORNER_LIST:
					addWithWheight(de.getVal(PATH), de.getIntVal(WEIGHT), corners);
					break;
			}
		}
	}

	public ManmadeFace build(Cliff cliff) {
		int index = 0;
		switch (cliff.type) {
			case Orthogonal:
				if (orthos.size() > 1) {
					index = RandomUtil.nextInt(orthos.size() - 1);
				}
				return new OrthogonalManmadeFace(cliff, orthos.get(index));
			case Salient:
				if (salients.size() > 1) {
					index = RandomUtil.nextInt(salients.size() - 1);
				}
				return new SalientManmadeFace(cliff, salients.get(index));
			case Corner:
				if (corners.size() > 1) {
					index = RandomUtil.nextInt(corners.size() - 1);
				}
				return new CornerManmadeFace(cliff, corners.get(index));
			case Border:
			case Bugged:
				return null;
			default:
				throw new RuntimeException();
		}
	}

	private void addWithWheight(String s, int weight, List<String> list) {
		if (weight <= 0 || weight > 50) {
			logger.warning("Invalid weight (" + weight + ") for manmade face " + def.getId() + ". Weight must be between 1 and 50.");
			return;
		}
		for (int i = 0; i < weight; i++) {
			list.add(s);
		}
	}

	@Override
	public void readFinalizedLibrary() {
	}
}
