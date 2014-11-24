/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map.parcel;

import model.map.cliff.Cliff;
import model.map.cliff.CliffShape;
import collections.PointRing;
import collections.Ring;
import geometry.Point2D;
import geometry.Polygon;
import geometry.algorithm.Triangulator;
import geometry3D.Triangle3D;
import geometry3D.MyMesh;
import geometry3D.Point3D;
import geometry3D.Polygon3D;
import java.util.ArrayList;
import java.util.HashMap;
import math.MyRandom;
import model.map.Tile;
import model.map.cliff.CornerCliffShape;
import tools.LogUtil;


/**
 *
 * @author Benoît
 */
public class ParcelMesh extends MyMesh {
    
    ParcelManager pm;
    HashMap<Tile, ArrayList<Triangle3D>> tiles = new HashMap<>();
    
    public ParcelMesh(ParcelManager pm){
        this.pm = pm;
    }
    public void add(Tile t){
        tiles.put(t, new ArrayList<Triangle3D>());
    }
    
    private ArrayList<Triangle3D> getTileGround(Tile t){
        ArrayList<Triangle3D> triangles = new ArrayList<>();
        for(ParcelMesh mesh : pm.meshes){
            if(mesh.tiles.get(t) != null)
                triangles.addAll(mesh.tiles.get(t));
        }
        
        if(!triangles.isEmpty())
            return triangles;
        
        if(t.e == null || t.n == null)
            return triangles;
        
        Point3D sw;
        Point3D se;
        Point3D ne;
        Point3D nw;
        if(t.x % 2 != 0){
            // pair
            sw = new Point3D(t.x, t.y+0.25, t.z);
            se = new Point3D(t.e.x, t.e.y-0.25, t.e.z);
            ne = new Point3D(t.e.n.x, t.e.n.y-0.25, t.e.n.z);
            nw = new Point3D(t.n.x, t.n.y+0.25, t.n.z);
            triangles.add(new Triangle3D(sw, se, ne));
            triangles.add(new Triangle3D(sw, ne, nw));
        } else {
            sw = new Point3D(t.x, t.y-0.25, t.z);
            se = new Point3D(t.e.x, t.e.y+0.25, t.e.z);
            ne = new Point3D(t.e.n.x, t.e.n.y+0.25, t.e.n.z);
            nw = new Point3D(t.n.x, t.n.y-0.25, t.n.z);
            triangles.add(new Triangle3D(sw, se, nw));
            triangles.add(new Triangle3D(nw, se, ne));
        }
        return triangles;
    }
    
    private ArrayList<Polygon3D> getCliffGrounds(Cliff c){
        ArrayList<Polygon3D> rawRes = new ArrayList<>();
        if(c.shape == null)
            return rawRes;

        Point2D sw = new Point2D(-0.5, -0.5);
        Point2D se = new Point2D(0.5, -0.5);
        Point2D ne = new Point2D(0.5, 0.5);
        Point2D nw = new Point2D(-0.5, 0.5);
        
        double offset = 0.25;
        if(c.x % 2 == 0)
            offset = -offset;
        for(Ring<Point3D> ring : c.shape.getGrounds()){
            Ring<Point3D> offRing = new Ring<>();
            for(Point3D p : ring){
                p = p.get2D().getRotation(c.shape.angle).get3D(p.z);
                if(p.get2D().equals(sw) || p.get2D().equals(nw))
                    p = p.getAddition(0, offset, 0);
                if(p.get2D().equals(se) || p.get2D().equals(ne))
                    p = p.getAddition(0, -offset, 0);
                offRing.add(p);
            }
            try {
                rawRes.add(new Polygon3D(offRing));
            } catch (Exception e) {
                LogUtil.logger.info("can't generate cliff ground at "+c);
            }
        }

        ArrayList<Polygon3D> res = new ArrayList<>();
        for(Polygon3D p : rawRes){
            res.add(p.getTranslation(c.getPos().x+0.5, c.getPos().y+0.5, c.level*Cliff.STAGE_HEIGHT));
        }
        return res;
    }
    
    private ArrayList<Triangle3D> getNearbyTriangles(Tile t){
        ArrayList<Triangle3D> res = new ArrayList<>();
        for(Tile neib : t.get8Neighbors())
            if(!neib.isCliff())
            res.addAll(getTileGround(neib));
        res.addAll(getTileGround(t));
        return res;
    }
    
    public void compute(){
        double texScale = 1d/128d;
        
        for(Tile tile : tiles.keySet()){
            if(!tile.isCliff())
                for(Triangle3D t : getTileGround(tile)){
                    int index = vertices.size();
                    vertices.add(t.a);
                    vertices.add(t.b);
                    vertices.add(t.c);

                    indices.add(index);
                    indices.add(index+1);
                    indices.add(index+2);

                    Point3D normal1 = t.normal;
                    Point3D normal2 = t.normal;
                    Point3D normal3 = t.normal;

                    for(Triangle3D n : getNearbyTriangles(tile)){
                        ArrayList<Point3D> shared = t.getCommonPoints(n);
                        if(shared.size() == 3)
                            continue;
                        if(shared.contains(t.a))
                            normal1 = normal1.getAddition(n.normal.getMult(0.35));

                        if(shared.contains(t.b))
                            normal2 = normal2.getAddition(n.normal.getMult(0.35));

                        if(shared.contains(t.c))
                            normal3 = normal3.getAddition(n.normal.getMult(0.35));
                    }

                    if(normal1.isOrigin())
                        normals.add(t.normal);
                    else
                        normals.add(normal1.getNormalized());

                    if(normal2.isOrigin())
                        normals.add(t.normal);
                    else
                        normals.add(normal2.getNormalized());

                    if(normal3.isOrigin())
                        normals.add(t.normal);
                    else
                        normals.add(normal3.getNormalized());

                    textCoord.add(t.a.get2D().getMult(texScale));
                    textCoord.add(t.b.get2D().getMult(texScale));
                    textCoord.add(t.c.get2D().getMult(texScale));
                }
            else
                for(Polygon3D polygon : getCliffGrounds((Cliff)tile)){
                    Triangulator t = new Triangulator(polygon);
                    int lastIndex = vertices.size();
                    for (int i = t.getIndices().size() - 1; i >= 0; i--)
                            indices.add(t.getIndices().get(i) + lastIndex);

                    for (Point3D point : polygon.points) {
                            vertices.add(point);
                            normals.add(new Point3D(0, 0, 1));
                            textCoord.add(point.get2D().getMult(texScale));
                    }
                }
        }
    }
}
