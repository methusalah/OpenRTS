/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.battlefield.map.parcel;

import model.battlefield.map.cliff.Cliff;
import model.battlefield.map.cliff.faces.natural.NaturalFace;
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
import javax.management.RuntimeErrorException;
import math.Angle;
import math.MyRandom;
import model.battlefield.map.Tile;
import model.battlefield.map.cliff.faces.natural.Dug1Corner;
import tools.LogUtil;


/**
 *
 * @author Benoît
 */
public class ParcelMesh extends MyMesh {
    
    ParcelManager manager;
    HashMap<Tile, ArrayList<Triangle3D>> tiles = new HashMap<>();
    
    public ParcelMesh(ParcelManager pm){
        this.manager = pm;
    }
    public void add(Tile t){
        tiles.put(t, new ArrayList<Triangle3D>());
    }
    
    private ArrayList<Triangle3D> getGroundTriangles(Tile t){
        if(t.e == null || t.n == null)
            return new ArrayList<>();

        if(tiles.containsKey(t)){
            if(tiles.get(t).isEmpty())
                if(t.isCliff())
                    tiles.get(t).addAll(getCliffGrounds(t));
                else
                    tiles.get(t).addAll(getTileGround(t));
            return tiles.get(t);
        } else {
            for(ParcelMesh mesh : manager.meshes)
                if(mesh.tiles.containsKey(t))
                    return mesh.getGroundTriangles(t);
        }
        throw new RuntimeException("strange");
    }
    
    private ArrayList<Triangle3D> getTileGround(Tile t){
        Point3D sw = new Point3D(t.x, t.y, t.getZ());
        Point3D se = new Point3D(t.e.x, t.e.y, t.e.getZ());
        Point3D ne = new Point3D(t.e.n.x, t.e.n.y, t.e.n.getZ());
        Point3D nw = new Point3D(t.n.x, t.n.y, t.n.getZ());

        ArrayList<Triangle3D> triangles = new ArrayList<>();
        triangles.add(new Triangle3D(sw, se, ne));
        triangles.add(new Triangle3D(sw, ne, nw));
        return triangles;
    }
    
    private ArrayList<Triangle3D> getCliffGrounds(Tile t){
        if(t.cliff.face == null)
            return new ArrayList<>();

        Point2D sw = new Point2D(-0.5, -0.5);
        Point2D se = new Point2D(0.5, -0.5);
        Point2D ne = new Point2D(0.5, 0.5);
        Point2D nw = new Point2D(-0.5, 0.5);
        
        ArrayList<Polygon3D> polygons = new ArrayList<>();
        for(Ring<Point3D> ring : t.cliff.face.getGrounds()){
            Ring<Point3D> elevatedRing = new Ring<>();
            for(Point3D p : ring){
                p = p.get2D().getRotation(t.cliff.angle).get3D(p.z);
                if(p.get2D().equals(sw))
                    p = p.getAddition(0, 0, t.getZ());
                else if(p.get2D().equals(se))
                    p = p.getAddition(0, 0, t.e.getZ());
                else if(p.get2D().equals(ne))
                    p = p.getAddition(0, 0, t.n.e.getZ());
                else if(p.get2D().equals(nw))
                    p = p.getAddition(0, 0, t.n.getZ());
                else
                    p = p.getAddition(0, 0, t.level*Tile.STAGE_HEIGHT);
                    
                elevatedRing.add(p);
            }
            try {
                if(!elevatedRing.isEmpty())
                    polygons.add(new Polygon3D(elevatedRing));
            } catch (Exception e) {
                LogUtil.logger.info("can't generate cliff ground at "+t+" because "+e);
            }
        }

        ArrayList<Triangle3D> res = new ArrayList<>();
        for(Polygon3D p : polygons){
            Triangulator triangulator = new Triangulator(p.getTranslation(t.getPos().x+0.5, t.getPos().y+0.5, 0));
            res.addAll(triangulator.getTriangles());
        }
        return res;
    }
    
    private ArrayList<Triangle3D> getNearbyTriangles(Tile t){
        ArrayList<Triangle3D> res = new ArrayList<>();
        for(Tile n : t.get9Neighbors())
//            if(!neib.isCliff())
                res.addAll(getGroundTriangles(n));
        return res;
    }
    
    public void compute(){
        double xScale = 1.0/manager.map.width;
        double yScale = 1.0/manager.map.height;
        
        for(Tile tile : tiles.keySet()){
            for(Triangle3D t : getGroundTriangles(tile)){
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
                    if(t.normal.getAngleWith(n.normal)>Angle.RIGHT){
                        continue;
                    }
                    if(shared.size() == 3)
                        continue;
                    if(shared.contains(t.a))
                        normal1 = normal1.getAddition(n.normal);//.getMult(0.35));

                    if(shared.contains(t.b))
                        normal2 = normal2.getAddition(n.normal);//.getMult(0.35));

                    if(shared.contains(t.c))
                        normal3 = normal3.getAddition(n.normal);//.getMult(0.35));
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

                textCoord.add(t.a.get2D().getMult(xScale, yScale));
                textCoord.add(t.b.get2D().getMult(xScale, yScale));
                textCoord.add(t.c.get2D().getMult(xScale, yScale));
            }
        }
    }
    
    public void reset(){
        vertices.clear();
        textCoord.clear();
        normals.clear();
        indices.clear();
        for(Tile t : tiles.keySet())
            tiles.get(t).clear();
    }
}
