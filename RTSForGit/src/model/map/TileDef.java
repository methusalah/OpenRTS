/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map;

/**
 *
 * @author Benoît
 */
public class TileDef {
    public int x;
    public int y;
    public double z;
    public int level;
    public boolean rampComp = false;
    public boolean rampStart = false;
    public boolean cliff = false;
    public boolean urban = false;
    
    public void setLevel(int level){
        this.level = level;
        z = level*Tile.STAGE_HEIGHT;
    }
}
