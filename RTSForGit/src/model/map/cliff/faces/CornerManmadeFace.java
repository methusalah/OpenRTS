/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.cliff.faces;

import collections.Ring;
import geometry3D.Point3D;
import java.util.ArrayList;
import model.map.Tile;
import model.map.cliff.Cliff;

/**
 *
 * @author Benoît
 */
public class CornerManmadeFace extends ManmadeFace {

    @Override
    public ArrayList<Ring<Point3D>> getGrounds() {
        Point3D sw = new Point3D(-0.5, -0.5, Tile.STAGE_HEIGHT);
        Point3D se = new Point3D(0.5, -0.5, 0);
        Point3D ne = new Point3D(0.5, 0.5, 0);
        Point3D nw = new Point3D(-0.5, 0.5, 0);

        Ring<Point3D> lowerPoints = new Ring<>();
        Ring<Point3D> upperPoints = new Ring<>();

        lowerPoints.add(sw);
        lowerPoints.add(se.getAddition(0, 0, -Tile.STAGE_HEIGHT));
        lowerPoints.add(nw.getAddition(0, 0, -Tile.STAGE_HEIGHT));

        upperPoints.add(se);
        upperPoints.add(ne);
        upperPoints.add(nw);

        ArrayList<Ring<Point3D>> res = new ArrayList<>();
        res.add(lowerPoints);
        res.add(upperPoints);
        return res;
    }
}
