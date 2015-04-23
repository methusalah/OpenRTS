package model.battlefield.map.cliff.faces.manmade;

import geometry.collections.Ring;
import geometry.geom3d.Point3D;
import model.battlefield.map.cliff.Cliff;



public class SalientManmadeFace extends ManmadeFace {

    public SalientManmadeFace(Cliff cliff, String modelPath) {
        super(cliff, modelPath);
    }

    @Override
    public Ring<Point3D> getLowerGround() {
        Point3D se = new Point3D(0.5, -0.5, 0);
        Point3D ne = new Point3D(0.5, 0.5, 0);
        Point3D nw = new Point3D(-0.5, 0.5, 0);

        Ring<Point3D> res = new Ring<>();
        res.add(se.getAddition(-0.9, 0, 0));
        res.add(se);
        res.add(ne);
        res.add(nw);
        res.add(nw.getAddition(0, -0.9, 0));
        return getRotation(res);
    }
    @Override
    public Ring<Point3D> getUpperGround() {
        Point3D sw = new Point3D(-0.5, -0.5, 0);

        Ring<Point3D> res = new Ring<>();
        res.add(sw);
        res.add(sw.getAddition(0.01, 0, 0));
        res.add(sw.getAddition(0, 0.01, 0));
        return getRotation(res);
    }
}
