package model.battlefield.map.cliff.faces.manmade;

import model.battlefield.map.cliff.Cliff;
import model.battlefield.map.cliff.faces.Face;

/**
 * A face that uses a model as shape.
 * 
 */
public abstract class ManmadeFace extends Face {
    public final String modelPath;
    
    public ManmadeFace(Cliff cliff, String modelPath){
    	super(cliff);
        this.modelPath = modelPath;
    }
    
    @Override
    public String getType() {
        return "manmade";
    }
}
