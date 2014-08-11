/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map;

import geometry.Point2D;
import geometry3D.Triangle3D;
import geometry3D.MyMesh;
import geometry3D.Point3D;
import java.util.ArrayList;
import tools.LogUtil;


/**
 *
 * @author Benoît
 */
public class TerrainMesh extends MyMesh {
    
    ArrayList<Tile> tiles = new ArrayList<Tile>();
    
    public void add(Tile t){
        if(t.e == null || t.n == null)
            return;
        if(t.e.n == null)
            throw new RuntimeException("strange");
        
        tiles.add(t);
    }
    
    private ArrayList<Triangle3D> getTrianglesFrom(Tile t){
        ArrayList<Triangle3D> res = new ArrayList<Triangle3D>();
        if(t.e == null || t.n == null)
            return res;
        if(!t.isCliff()) {
            Point3D o = new Point3D(t.x, t.y, t.z);
            Point3D e = new Point3D(t.e.x, t.e.y, t.e.z);
            Point3D en = new Point3D(t.e.n.x, t.e.n.y, t.e.n.z);
            Point3D n = new Point3D(t.n.x, t.n.y, t.n.z);

            res.add(new Triangle3D(o, e, en));
            res.add(new Triangle3D(o, en, n));
        } else {
            double level;
            if(t.cliff.obtuseDiag)
                level = t.level+2;
            else
                level = t.level;
                
            Point2D origin = new Point2D(t.x, t.y);
            Point2D pivot = new Point2D(t.x+0.5, t.y+0.5);

            Point2D a = origin.getRotation(t.cliff.angle, pivot);
            Point2D b = origin.getAddition(1, 0).getRotation(t.cliff.angle, pivot);
            Point2D c = origin.getAddition(1, 1).getRotation(t.cliff.angle, pivot);

            res.add(new Triangle3D(new Point3D(a.x, a.y, level),
                    new Point3D(b.x, b.y, level),
                    new Point3D(c.x, c.y, level)));
        }
        return res;
    }
    
    private ArrayList<Triangle3D> getTrianglesFromNeihbours(Tile t){
        ArrayList<Triangle3D> res = new ArrayList<Triangle3D>();
        if(t.n != null)
            res .addAll(getTrianglesFrom(t.n));
        if(t.e != null)
            res .addAll(getTrianglesFrom(t.e));
        if(t.s != null)
            res .addAll(getTrianglesFrom(t.s));
        if(t.w != null)
            res .addAll(getTrianglesFrom(t.w));
        return res;
        
    }

    
    public void compute(){
        for(Tile tile : tiles)
            for(Triangle3D t : getTrianglesFrom(tile)){
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
                
                for(Triangle3D n : getTrianglesFromNeihbours(tile)){
                    if(n==t)
                        continue;
                    if(t.a.equals(n.a) || t.a.equals(n.b) || t.a.equals(n.c))
                        normal1 = normal1.getAddition(n.normal);

                    if(t.b.equals(n.a) || t.b.equals(n.b) || t.b.equals(n.c))
                        normal2 = normal2.getAddition(n.normal);

                    if(t.c.equals(n.a) || t.c.equals(n.b) || t.c.equals(n.c))
                        normal3 = normal3.getAddition(n.normal);
                }
                normals.add(normal1.getDivision(normal1.getNorm()));
                normals.add(normal2.getDivision(normal2.getNorm()));
                normals.add(normal3.getDivision(normal3.getNorm()));

                double s = 1d/128d;
                textCoord.add(new Point2D(t.a.x, t.a.y).getMult(s));
                textCoord.add(new Point2D(t.b.x, t.b.y).getMult(s));
                textCoord.add(new Point2D(t.c.x, t.c.y).getMult(s));
        }
    }
}
