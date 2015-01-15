/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.map.cliff.faces.natural;

import collections.Ring;
import geometry3D.Point3D;

import java.awt.Color;
import java.util.ArrayList;

import math.MyRandom;
import model.battlefield.map.Tile;
import model.battlefield.map.cliff.Cliff;
import model.battlefield.map.cliff.Ramp;
import static model.battlefield.map.cliff.faces.natural.Dug1.MAX_RIDGE_POS;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class Dug1Ortho extends Dug1 {
    private final static double RIDGE_PROTRUDE = 0.6;
    private final static double RIDGE_RETREAT = 0.6;

    public Dug1Ortho(Cliff cliff, double noiseX, double noiseY, double noiseZ, double ridgeDepth, double ridgePos, Color color, String texturePath) {
        super(cliff, noiseX, noiseY, noiseZ, ridgeDepth, ridgePos, color, texturePath);
        buildMesh();
    }

    @Override
    protected void extrudeProfile() {
        int i = 0;
        double ridgeDepth = MyRandom.between(1+RIDGE_PROTRUDE*ridgeDepthRange, 1-RIDGE_RETREAT*ridgeDepthRange);
        double ridgePos = MyRandom.between(1+MAX_RIDGE_POS*ridgePosRange, 1-MAX_RIDGE_POS*ridgePosRange);
        
        for(Point3D v : parentProfile)
            grid[0][i++] = v.getAddition(0, 1, 0);
        i = 0;
        for(Point3D v : middleProfile)
            grid[1][i++] = v.getAddition(0, 0.5*ridgePos, 0).getMult(ridgeDepth, 1, 1);
        i = 0;
        for(Point3D v : childProfile)
            grid[2][i++] = v;
    }        

    @Override
    public ArrayList<Ring<Point3D>> getGrounds() {
        Point3D sw = new Point3D(-0.5, -0.5, 0);
        Point3D se = new Point3D(0.5, -0.5, 0);
        Point3D ne = new Point3D(0.5, 0.5, 0);
        Point3D nw = new Point3D(-0.5, 0.5, 0);

        Ring<Point3D> lowerPoints = new Ring<>();
        Ring<Point3D> upperPoints = new Ring<>();

        lowerPoints.add(se);
        lowerPoints.add(ne);
        for(int i=0; i<NB_VERTEX_COL; i++)
            lowerPoints.add(grid[i][0]);

        upperPoints.add(nw);
        upperPoints.add(sw);
        for(int i=NB_VERTEX_COL-1; i>=0; i--)
            upperPoints.add(grid[i][NB_VERTEX_ROWS-1]);
        
        ArrayList<Ring<Point3D>> res = new ArrayList<>();
        res.add(lowerPoints);
        res.add(upperPoints);
        return res;
    }
}
