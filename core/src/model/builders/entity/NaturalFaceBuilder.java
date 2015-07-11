/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders.entity;

import java.awt.Color;
import java.util.logging.Logger;

import model.battlefield.map.cliff.Cliff;
import model.battlefield.map.cliff.faces.natural.Dug1Corner;
import model.battlefield.map.cliff.faces.natural.Dug1Ortho;
import model.battlefield.map.cliff.faces.natural.Dug1Salient;
import model.battlefield.map.cliff.faces.natural.NaturalFace;
import model.builders.entity.definitions.DefElement;
import model.builders.entity.definitions.Definition;

/**
 *
 * @author Benoît
 */
public class NaturalFaceBuilder extends Builder{

	private static final Logger logger = Logger.getLogger(NaturalFaceBuilder.class.getName());

	private static final String STYLE = "Style";
	private static final String COLOR = "Color";
	private static final String TEXTURE_PATH = "TexturePath";
	private static final String NOISE = "Noise";
	private static final String NOISE_X = "NoiseX";
	private static final String NOISE_Y = "NoiseY";
	private static final String NOISE_Z = "NoiseZ";
	private static final String RIDGE_DEPTH = "RidgeDepth";
	private static final String RIDGE_POSITION = "RidgePosition";

	private static final String RED = "R";
	private static final String GREEN = "G";
	private static final String BLUE = "B";

	private static final String STYLE_DUG_1 = "dug1";


	private double noiseX, noiseY, noiseZ;
	private double ridgeDepth, ridgePos;
	private Color color;
	private String texturePath;
	private String style;

	public NaturalFaceBuilder(Definition def) {
		super(def);
		for (DefElement de : def.getElements()) {
			switch(de.name){
				case NOISE :
					checkRange(de);
					noiseX = noiseY = noiseZ = de.getDoubleVal();
					break;
				case NOISE_X :
					checkRange(de);
					noiseX = de.getDoubleVal();
					break;
				case NOISE_Y :
					checkRange(de);
					noiseY = de.getDoubleVal();
					break;
				case NOISE_Z :
					checkRange(de);
					noiseZ = de.getDoubleVal();
					break;
				case RIDGE_DEPTH :
					checkRange(de);
					ridgeDepth = de.getDoubleVal();
					break;
				case RIDGE_POSITION :
					checkRange(de);
					ridgePos = de.getDoubleVal();
					break;
				case STYLE : style = de.getVal(); break;
				case COLOR :
					color = new Color(de.getIntVal(RED),
							de.getIntVal(GREEN),
							de.getIntVal(BLUE));
					break;
				case TEXTURE_PATH : texturePath = de.getVal(); break;
				default:printUnknownElement(de.name);
			}
		}
		if(color == null && texturePath == null){
			logger.warning("Natural face '" + getId() + "'has no specified color nor texture. Applying debbuging color.");
			color = Color.ORANGE;
		}
	}

	public NaturalFace build(Cliff cliff){
		switch (style){
			case STYLE_DUG_1 :
				switch (cliff.type){
					case Corner : return new Dug1Corner(cliff, noiseX, noiseY, noiseZ, ridgeDepth, ridgePos, color, texturePath);
					case Salient : return new Dug1Salient(cliff, noiseX, noiseY, noiseZ, ridgeDepth, ridgePos, color, texturePath);
					case Orthogonal : return new Dug1Ortho(cliff, noiseX, noiseY, noiseZ, ridgeDepth, ridgePos, color, texturePath);
					case Bugged : return null;
					case Border : return null;
					default:throw new RuntimeException();
				}
			default:
				printUnknownValue(STYLE, style);
				throw new RuntimeException();
		}
	}

	private boolean checkRange(DefElement de){
		double val = de.getDoubleVal();
		if(val>1 || val<0){
			logger.warning("Range value (" + val + ") incorrect for " + def.getId() + ". Must be between 0 an 1.");
			return false;
		}
		return true;

	}

	@Override
	public void readFinalizedLibrary() {
	}
}
