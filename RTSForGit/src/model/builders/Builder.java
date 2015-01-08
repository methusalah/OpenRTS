/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.builders;

import static model.builders.EffectBuilder.TYPE;
import ressources.definitions.BuilderLibrary;
import ressources.definitions.DefElement;
import ressources.definitions.Definition;

/**
 *
 * @author Benoît
 */
public class Builder {
    Definition def;
    BuilderLibrary lib;
    
    public Builder(Definition def, BuilderLibrary lib){
        this.def = def;
        this.lib = lib;
    }    
    
    public String getId(){
        return def.id;
    }
}
