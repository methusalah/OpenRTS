/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.warfare;

import java.awt.Color;
import java.util.ArrayList;
import model.battlefield.army.components.Unit;

/**
 *
 * @author Benoît
 */
public class Faction {
    
    public ArrayList<Faction> allies = new ArrayList<Faction>();
    public ArrayList<Faction> enemies = new ArrayList<Faction>();
    public ArrayList<Faction> neutrals = new ArrayList<Faction>();
    
    public ArrayList<Unit> units = new ArrayList<Unit>();
    public Color c;
    
    public Faction(Color c){
        this.c = c;
    }
    
    public void setAlly(Faction o){
        remove(o);
        allies.add(o);
        o.allies.add(this);
    }
    public void setEnnemy(Faction o){
        remove(o);
        enemies.add(o);
        o.enemies.add(this);
    }
    public void setNeutral(Faction o){
        remove(o);
        neutrals.add(o);
        o.neutrals.add(this);
    }
    
    private void remove(Faction o){
        allies.remove(o);
        enemies.remove(o);
        neutrals.remove(o);
        o.allies.remove(this);
        o.enemies.remove(this);
        o.neutrals.remove(this);
    }
}
