/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.map.cliff.faces.manmade;

import collections.Ring;
import geometry3D.Point3D;
import java.util.ArrayList;
import model.battlefield.map.Tile;
import model.battlefield.map.cliff.Cliff;

/**
 *
 * @author Benoît
 */
public class CornerManmadeFace extends ManmadeFace {

    public CornerManmadeFace(String modelPath) {
        super(modelPath);
    }
    
    @Override
    public Ring<Point3D> getLowerGround() {
        Point3D sw = new Point3D(-0.5, -0.5, 0);
        Point3D se = new Point3D(0.5, -0.5, 0);
        Point3D nw = new Point3D(-0.5, 0.5, 0);

        Ring<Point3D> res = new Ring<>();
        res.add(sw);
        res.add(se.getAddition(0, 0, -Tile.STAGE_HEIGHT));
        res.add(nw.getAddition(0, 0, -Tile.STAGE_HEIGHT));
        return getRotation(res);
    }
    @Override
    public Ring<Point3D> getUpperGround() {
        Point3D se = new Point3D(0.5, -0.5, 0);
        Point3D ne = new Point3D(0.5, 0.5, 0);
        Point3D nw = new Point3D(-0.5, 0.5, 0);

        Ring<Point3D> res = new Ring<>();
        res.add(se.getAddition(-0.01, 0, 0));
        res.add(se);
        res.add(ne);
        res.add(nw);
        res.add(nw.getAddition(0, -0.01, 0));
        return getRotation(res);
    }
}
