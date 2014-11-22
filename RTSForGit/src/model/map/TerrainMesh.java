/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map;

import model.map.CliffShape.CliffShape;
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
        
        if(c.shape == null || c.e == null || c.n == null)
            return triangles;
        

        ArrayList<Triangle3D> toTranslate = new ArrayList<>();
        Point3D[][] grid = c.shape.getVertices();
        
        switch (c.shape.getType()){
            case Salient : 
                Point3D o = new Point3D(-0.5, -0.5, Cliff.STAGE_HEIGHT);
                Point3D e = new Point3D(0.5, -0.5, 0);
                Point3D ne = new Point3D(0.5, 0.5, 0);
                Point3D n = new Point3D(-0.5, 0.5, 0);

                for(int i=0; i<CliffShape.NB_VERTEX_COL-1; i++){
                    Point3D p1 = grid[i][CliffShape.NB_VERTEX_ROWS-1];
                    Point3D p2 = grid[i+1][CliffShape.NB_VERTEX_ROWS-1];
                    toTranslate.add(new Triangle3D(o, p2, p1));
                }

                toTranslate.add(new Triangle3D(ne, n, grid[0][0]));
                for(int i=0; i<CliffShape.NB_VERTEX_COL-1; i++){
                    Point3D p1 = grid[i][0];
                    Point3D p2 = grid[i+1][0];
                    toTranslate.add(new Triangle3D(ne, p1, p2));
                }
                toTranslate.add(new Triangle3D(ne, grid[2][0], e));
                break;
                
            case Corner : 
                o = new Point3D(-0.5, -0.5, 0);
                e = new Point3D(0.5, -0.5, Cliff.STAGE_HEIGHT);
                ne = new Point3D(0.5, 0.5, Cliff.STAGE_HEIGHT);
                n = new Point3D(-0.5, 0.5, Cliff.STAGE_HEIGHT);

                for(int i=0; i<CliffShape.NB_VERTEX_COL-1; i++){
                    Point3D p1 = grid[i][0];
                    Point3D p2 = grid[i+1][0];
                    toTranslate.add(new Triangle3D(o, p1, p2));
                }

                toTranslate.add(new Triangle3D(ne, grid[0][CliffShape.NB_VERTEX_ROWS-1], e));
                for(int i=0; i<CliffShape.NB_VERTEX_COL-1; i++){
                    Point3D p1 = grid[i][CliffShape.NB_VERTEX_ROWS-1];
                    Point3D p2 = grid[i+1][CliffShape.NB_VERTEX_ROWS-1];
                    toTranslate.add(new Triangle3D(ne, p2, p1));
                }
                toTranslate.add(new Triangle3D(ne, n, grid[CliffShape.NB_VERTEX_COL-1][CliffShape.NB_VERTEX_ROWS-1]));
                break;
                
            case Orthogonal : 
                o = new Point3D(-0.5, -0.5, Cliff.STAGE_HEIGHT);
                e = new Point3D(0.5, -0.5, 0);
                ne = new Point3D(0.5, 0.5, 0);
                n = new Point3D(-0.5, 0.5, Cliff.STAGE_HEIGHT);
                Point3D middleE = new Point3D(-0.5, 0, Cliff.STAGE_HEIGHT);
                Point3D middleW = new Point3D(0.5, 0, 0);

                toTranslate.add(new Triangle3D(middleE, grid[0][CliffShape.NB_VERTEX_ROWS-1], n));
                for(int i=0; i<CliffShape.NB_VERTEX_COL-1; i++){
                    Point3D p1 = grid[i][CliffShape.NB_VERTEX_ROWS-1];
                    Point3D p2 = grid[i+1][CliffShape.NB_VERTEX_ROWS-1];
                    toTranslate.add(new Triangle3D(middleE, p2, p1));
                }
                toTranslate.add(new Triangle3D(middleE, o, grid[CliffShape.NB_VERTEX_COL-1][CliffShape.NB_VERTEX_ROWS-1]));

                toTranslate.add(new Triangle3D(middleW, ne, grid[0][0]));
                for(int i=0; i<CliffShape.NB_VERTEX_COL-1; i++){
                    Point3D p1 = grid[i][0];
                    Point3D p2 = grid[i+1][0];
                    toTranslate.add(new Triangle3D(middleW, p1, p2));
                }
                toTranslate.add(new Triangle3D(middleW, grid[CliffShape.NB_VERTEX_COL-1][0], e));
                break;
                
            default: throw new RuntimeException("trying to computes cliff's grounds for an unspecialized cliff : "+c.getPos());
        }
        
        
        for(Triangle3D t : toTranslate)
            triangles.add(t.getRotationAroundZ(c.shape.angle).getTranslation(c.getPos().x+0.5, c.getPos().y+0.5, c.level*Cliff.STAGE_HEIGHT));
        return triangles;
    }
    
    private ArrayList<Triangle3D> getNearbyTriangles(Tile t){
        ArrayList<Triangle3D> res = new ArrayList<>();
        for(Tile neib : t.get8Neighbors())
            res.addAll(getTriangles(neib));
        res.addAll(getTriangles(t));
        return res;
        
    }

    
    public void compute(){
        double texScale = 1d/128d;
        
//        ArrayList<Triangle3D> triangul = new ArrayList<>();
//        Point3D[][] points = new Point3D[128][128];
//        
//        double noise = 0.2;
//        
//        for(Tile t : tiles.keySet()){
//            if(t.x % 2 != 0)
//                // pair
//                points[t.x][t.y] = new Point3D(t.x+MyRandom.between(-noise, noise), t.y+0.25+MyRandom.between(-noise, noise), t.z);
//            else 
//                points[t.x][t.y] = new Point3D(t.x+MyRandom.between(-noise, noise), t.y-0.25+MyRandom.between(-noise, noise), t.z);
//            if(t.isCliff()){
//                triangul.addAll(getTriangles(t));
//            }
//        }
//
//        for(int i=0; i<128; i++)
//            for(int j=0; j<128; j++){
//                if(i <127 && j<127 && 
//                        (points[i+1][j].z-points[i][j].z < 1 &&
//                        points[i][j+1].z-points[i][j].z < 1 &&
//                        points[i+1][j+1].z-points[i][j].z < 1))
//                if(i%2!=0){
//                    triangul.add(new Triangle3D(points[i][j], points[i+1][j+1], points[i][j+1]));
//                    triangul.add(new Triangle3D(points[i][j], points[i+1][j], points[i+1][j+1]));
//                } else {
//                    triangul.add(new Triangle3D(points[i][j], points[i+1][j], points[i][j+1]));
//                    triangul.add(new Triangle3D(points[i][j+1], points[i+1][j], points[i+1][j+1]));
//
//                }
//            }
//        
//        for(Triangle3D t : triangul){
//            int index = vertices.size();
//                vertices.add(t.a);
//                vertices.add(t.b);
//                vertices.add(t.c);
//
//                indices.add(index);
//                indices.add(index+1);
//                indices.add(index+2);
//
//                normals.add(t.normal);
//                normals.add(t.normal);
//                normals.add(t.normal);
//
//                textCoord.add(new Point2D(t.a.x, t.a.y).getMult(texScale));
//                textCoord.add(new Point2D(t.b.x, t.b.y).getMult(texScale));
//                textCoord.add(new Point2D(t.c.x, t.c.y).getMult(texScale));
//        }
        
        
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

                textCoord.add(new Point2D(t.a.x, t.a.y).getMult(texScale));
                textCoord.add(new Point2D(t.b.x, t.b.y).getMult(texScale));
                textCoord.add(new Point2D(t.c.x, t.c.y).getMult(texScale));
            }
        }
    }
}
