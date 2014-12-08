/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.data;

import java.util.ArrayList;
import math.MyRandom;
import model.map.cliff.Cliff;
import static model.map.cliff.Cliff.Type.Orthogonal;
import model.map.cliff.faces.manmade.CornerManmadeFace;
import model.map.cliff.faces.manmade.ManmadeFace;
import model.map.cliff.faces.manmade.OrthogonalManmadeFace;
import model.map.cliff.faces.manmade.SalientManmadeFace;
import ressources.definitions.BuilderLibrary;
import ressources.definitions.DefElement;
import ressources.definitions.Definition;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class ManmadeFaceBuilder {
    static final String ORTHOGONAL_LIST = "OrthogonalList";
    static final String SALIENT_LIST = "SalientList";
    static final String CORNER_LIST = "CornerList";
    static final String PATH = "path";
    static final String WEIGHT = "weight";

    Definition def;
    BuilderLibrary lib;
            
    public ManmadeFaceBuilder(Definition def){
        this.def = def;
        this.lib = lib;
    }
    
    public ManmadeFace build(Cliff cliff){
        ArrayList<String> orthos = new ArrayList<>();
        ArrayList<String> salients = new ArrayList<>();
        ArrayList<String> corners = new ArrayList<>();
        
        for(DefElement de : def.elements)
            switch(de.name){
                case ORTHOGONAL_LIST : addWithWheight(de.getVal(PATH), de.getIntVal(WEIGHT), orthos); break;
                case SALIENT_LIST : addWithWheight(de.getVal(PATH), de.getIntVal(WEIGHT), salients); break;
                case CORNER_LIST : addWithWheight(de.getVal(PATH), de.getIntVal(WEIGHT), corners); break;
            }
        
        int index = 0;
        switch (cliff.type){
            case Orthogonal :
                if(orthos.size()>1)
                    index = MyRandom.nextInt(orthos.size()-1);
                return new OrthogonalManmadeFace(orthos.get(index));
            case Salient :
                if(salients.size()>1)
                    index = MyRandom.nextInt(salients.size()-1);
                return new SalientManmadeFace(salients.get(index));
            case Corner :
                if(corners.size()>1)
                    index = MyRandom.nextInt(corners.size()-1);
                return new CornerManmadeFace(corners.get(index));
        }
        return null;
    }
    
    private void addWithWheight(String s, int weight, ArrayList<String> list){
        if(weight<=0 || weight>50){
            LogUtil.logger.warning("Invalid weight ("+weight+") for manmade face "+def.id+". Weight must be between 1 and 50.");
            return;
        }
        for(int i=0; i<weight; i++)
            list.add(s);
    }
    
}
