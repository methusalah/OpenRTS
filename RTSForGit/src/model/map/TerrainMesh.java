/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map;

import collections.PointRing;
import geometry.Point2D;
import geometry.Polygon;
import geometry.algorithm.Triangulator;
import geometry3D.Triangle3D;
import geometry3D.MyMesh;
import geometry3D.Point3D;
import java.util.ArrayList;
import java.util.HashMap;
import math.MyRandom;
import tools.LogUtil;


/**
 *
 * @author Benoît
 */
public class TerrainMesh extends MyMesh {
    
    HashMap<Tile, ArrayList<Triangle3D>> tiles = new HashMap<>();
    
    public void add(Tile t){
        tiles.put(t, new ArrayList<Triangle3D>());
    }
    
    private ArrayList<Triangle3D> getTriangles(Tile t){
        if(t.isCliff())
            return getTrianglesFrom((Cliff)t);
        else
            return getTrianglesFrom(t);
    }
    
    private ArrayList<Triangle3D> getTrianglesFrom(Tile t){
        ArrayList<Triangle3D> triangles = tiles.get(t);
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

        
        
//        int div = 2;
//        double step = 1/div;
//        
//        
//        Point3D[][] grid = new Point3D[div+1][div+1];
//        boolean pair = true;
//        for(int i=0; i<div+1; i++){
//            pair = !pair;
//            for(int j=0; j<div+1; j++){
//                double x = (double)i/div+t.x;
//                double y = (double)j*step+t.y;
//                if(!pair)
//                    y = (double)j*step+t.y-step/2;
//                    
//                Point3D p = new Point3D(x, y, MyRandom.between(-0.2, 0.2));
//                
//                grid[i][j] = p;
//            }
//        }
//        
//        pair = true;
//        for(int i=0; i<div; i++){
//            for(int j=0; j<div; j++){
//                triangles.add(new Triangle3D(grid[i][j], grid[i+1][j+1], grid[i+1][j]));
//                triangles.add(new Triangle3D(grid[i+1][j], grid[i][j+1], grid[i+1][j+1]));
//            }
//        }
        
        
        return triangles;
    }
    
    private ArrayList<Triangle3D> getTrianglesFrom(Cliff c){
        ArrayList<Triangle3D> triangles = tiles.get(c);
        if(!triangles.isEmpty())
            return triangles;
        
        if(c.e == null || c.n == null)
            return triangles;
        

        ArrayList<Triangle3D> toTranslate = new ArrayList<>();
        Point3D[][] vertices = c.getVertices();
        
        if(c.acuteDiag){
            Point3D o = new Point3D(-0.5, -0.5, Cliff.STAGE_HEIGHT);
            Point3D e = new Point3D(0.5, -0.5, 0);
            Point3D ne = new Point3D(0.5, 0.5, 0);
            Point3D n = new Point3D(-0.5, 0.5, 0);
            
            for(int i=0; i<Cliff.NB_VERTEX_COL-1; i++){
                Point3D p1 = vertices[i][Cliff.NB_VERTEX_ROWS-1];
                Point3D p2 = vertices[i+1][Cliff.NB_VERTEX_ROWS-1];
                toTranslate.add(new Triangle3D(o, p2, p1));
            }
            
            toTranslate.add(new Triangle3D(ne, n, vertices[0][0]));
            for(int i=0; i<Cliff.NB_VERTEX_COL-1; i++){
                Point3D p1 = vertices[i][0];
                Point3D p2 = vertices[i+1][0];
                toTranslate.add(new Triangle3D(ne, p1, p2));
            }
            toTranslate.add(new Triangle3D(ne, vertices[2][0], e));
            
        } else if(c.obtuseDiag){
            Point3D o = new Point3D(-0.5, -0.5, 0);
            Point3D e = new Point3D(0.5, -0.5, Cliff.STAGE_HEIGHT);
            Point3D ne = new Point3D(0.5, 0.5, Cliff.STAGE_HEIGHT);
            Point3D n = new Point3D(-0.5, 0.5, Cliff.STAGE_HEIGHT);
            
            for(int i=0; i<Cliff.NB_VERTEX_COL-1; i++){
                Point3D p1 = vertices[i][0];
                Point3D p2 = vertices[i+1][0];
                toTranslate.add(new Triangle3D(o, p1, p2));
            }
            
            toTranslate.add(new Triangle3D(ne, vertices[0][Cliff.NB_VERTEX_ROWS-1], e));
            for(int i=0; i<Cliff.NB_VERTEX_COL-1; i++){
                Point3D p1 = vertices[i][Cliff.NB_VERTEX_ROWS-1];
                Point3D p2 = vertices[i+1][Cliff.NB_VERTEX_ROWS-1];
                toTranslate.add(new Triangle3D(ne, p2, p1));
            }
            toTranslate.add(new Triangle3D(ne, n, vertices[Cliff.NB_VERTEX_COL-1][Cliff.NB_VERTEX_ROWS-1]));
        } else {
            Point3D o = new Point3D(-0.5, -0.5, Cliff.STAGE_HEIGHT);
            Point3D e = new Point3D(0.5, -0.5, 0);
            Point3D ne = new Point3D(0.5, 0.5, 0);
            Point3D n = new Point3D(-0.5, 0.5, Cliff.STAGE_HEIGHT);
            Point3D middleE = new Point3D(-0.5, 0, Cliff.STAGE_HEIGHT);
            Point3D middleW = new Point3D(0.5, 0, 0);
            
            toTranslate.add(new Triangle3D(middleE, vertices[0][Cliff.NB_VERTEX_ROWS-1], n));
            for(int i=0; i<Cliff.NB_VERTEX_COL-1; i++){
                Point3D p1 = vertices[i][Cliff.NB_VERTEX_ROWS-1];
                Point3D p2 = vertices[i+1][Cliff.NB_VERTEX_ROWS-1];
                toTranslate.add(new Triangle3D(middleE, p2, p1));
            }
            toTranslate.add(new Triangle3D(middleE, o, vertices[Cliff.NB_VERTEX_COL-1][Cliff.NB_VERTEX_ROWS-1]));
            
            toTranslate.add(new Triangle3D(middleW, ne, vertices[0][0]));
            for(int i=0; i<Cliff.NB_VERTEX_COL-1; i++){
                Point3D p1 = vertices[i][0];
                Point3D p2 = vertices[i+1][0];
                toTranslate.add(new Triangle3D(middleW, p1, p2));
            }
            toTranslate.add(new Triangle3D(middleW, vertices[Cliff.NB_VERTEX_COL-1][0], e));
        }
        
        
        for(Triangle3D t : toTranslate)
            triangles.add(t.getRotationAroundZ(c.angle).getTranslation(c.getPos().x+0.5, c.getPos().y+0.5, c.level*Cliff.STAGE_HEIGHT));
        return triangles;
    }
    
    private ArrayList<Triangle3D> getTrianglesFromNeihbours(Tile t){
        ArrayList<Triangle3D> res = new ArrayList<>();
        if(t.n != null){
            res.addAll(getTriangles(t.n));
            if(t.n.e != null)
                res.addAll(getTriangles(t.n.e));
            if(t.n.w != null)
                res.addAll(getTriangles(t.n.w));
        }
        if(t.e != null)
            res.addAll(getTriangles(t.e));
        if(t.s != null){
            res.addAll(getTriangles(t.s));
            if(t.s.e != null)
                res.addAll(getTriangles(t.s.e));
            if(t.s.w != null)
                res.addAll(getTriangles(t.s.w));
        }
        if(t.w != null)
            res.addAll(getTriangles(t.w));
        res.addAll(getTriangles(t));
        return res;
        
    }

    
    public void compute(){
        double texScale = 1d/128d;
        for(Tile tile : tiles.keySet()){
            ArrayList<Triangle3D> triangles = new ArrayList<>();
            triangles.addAll(getTriangles(tile));
            for(Triangle3D t : triangles){
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

                textCoord.add(new Point2D(t.a.x, t.a.y).getMult(texScale));
                textCoord.add(new Point2D(t.b.x, t.b.y).getMult(texScale));
                textCoord.add(new Point2D(t.c.x, t.c.y).getMult(texScale));
            }
        }
    }
}
