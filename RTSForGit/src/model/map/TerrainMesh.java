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
import math.MyRandom;
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
        if(t.isCliff() || t.e == null || t.n == null)
            return res;
        
        Point3D sw = new Point3D(t.x, t.y, t.z);
        Point3D se = new Point3D(t.e.x, t.e.y, t.e.z);
        Point3D ne = new Point3D(t.e.n.x, t.e.n.y, t.e.n.z);
        Point3D nw = new Point3D(t.n.x, t.n.y, t.n.z);
        
        res.add(new Triangle3D(sw, se, nw));
        res.add(new Triangle3D(nw, se, ne));
        
        
//        int div = 3;
//        Point3D[][] grid = new Point3D[div+1][div+1];
//        
//        for(int i=0; i<div+1; i++)
//            for(int j=0; j<div+1; j++){
//                Point2D p = new Point2D((double)i/div+t.x, (double)j/div+t.y);
//                
//                Point3D A;
//                Point3D B;
//                Point3D P;
//                double swDist = p.getDistance(sw.get2D());
//                double neDist = p.getDistance(ne.get2D());
//                if(swDist<neDist){
//                    A = sw.getSubtraction(nw);
//                    B = sw.getSubtraction(se);
//                    P = sw.getSubtraction(p.get3D(0));
//                } else {
//                    A = ne.getSubtraction(nw);
//                    B = ne.getSubtraction(se);
//                    P = ne.getSubtraction(p.get3D(0));
//                }
//
//                
//                double a = A.x;
//                double d = A.y;
//                double g = A.z;
//                double b = B.x;
//                double e = B.y;
//                double h = B.z;
//                double c = P.x;
//                double f = P.y;
//                double z = (-b*f*g-c*d*h+c*e*g+a*f*h)/(a*e+b*d);
////                LogUtil.logger.info("test"+(a*e+b*d));
//                
////                if(i>0 && i<d && j>0 && j<d)
////                    interpolatedZ = interpolatedZ+ MyRandom.between(-0.1, +0.1);
//                
//                grid[i][j]=p.get3D(z);
//            }
//        for(int i=0; i<div; i++){
//            for(int j=0; j<div; j++){
//                res.add(new Triangle3D(grid[i][j], grid[i+1][j], grid[i][j+1]));
//                res.add(new Triangle3D(grid[i+1][j], grid[i+1][j+1], grid[i][j+1]));
//            }
//        }
                
        
        
        return res;
    }
    
    private ArrayList<Triangle3D> getTrianglesFrom(Cliff c){
        ArrayList<Triangle3D> triangles = new ArrayList<>();
        if(c.e == null || c.n == null)
            return triangles;
        
        Point3D[][] vertices = c.getVertices();
        
        if(c.acuteDiag){
            Point3D o = new Point3D(-0.5, -0.5, Cliff.STAGE_HEIGHT);
            Point3D e = new Point3D(0.5, -0.5, 0);
            Point3D ne = new Point3D(0.5, 0.5, 0);
            Point3D n = new Point3D(-0.5, 0.5, 0);
            
            for(int i=0; i<Cliff.NB_VERTEX_COL-1; i++){
                Point3D p1 = vertices[i][Cliff.NB_VERTEX_ROWS-1];
                Point3D p2 = vertices[i+1][Cliff.NB_VERTEX_ROWS-1];
                triangles.add(new Triangle3D(o, p2, p1));
            }
            
            triangles.add(new Triangle3D(ne, n, vertices[0][0]));
            for(int i=0; i<Cliff.NB_VERTEX_COL-1; i++){
                Point3D p1 = vertices[i][0];
                Point3D p2 = vertices[i+1][0];
                triangles.add(new Triangle3D(ne, p1, p2));
            }
            triangles.add(new Triangle3D(ne, vertices[2][0], e));
            
        } else if(c.obtuseDiag){
            Point3D o = new Point3D(-0.5, -0.5, 0);
            Point3D e = new Point3D(0.5, -0.5, Cliff.STAGE_HEIGHT);
            Point3D ne = new Point3D(0.5, 0.5, Cliff.STAGE_HEIGHT);
            Point3D n = new Point3D(-0.5, 0.5, Cliff.STAGE_HEIGHT);
            
            for(int i=0; i<Cliff.NB_VERTEX_COL-1; i++){
                Point3D p1 = vertices[i][0];
                Point3D p2 = vertices[i+1][0];
                triangles.add(new Triangle3D(o, p1, p2));
            }
            
            triangles.add(new Triangle3D(ne, vertices[0][Cliff.NB_VERTEX_ROWS-1], e));
            for(int i=0; i<Cliff.NB_VERTEX_COL-1; i++){
                Point3D p1 = vertices[i][Cliff.NB_VERTEX_ROWS-1];
                Point3D p2 = vertices[i+1][Cliff.NB_VERTEX_ROWS-1];
                triangles.add(new Triangle3D(ne, p2, p1));
            }
            triangles.add(new Triangle3D(ne, n, vertices[Cliff.NB_VERTEX_COL-1][Cliff.NB_VERTEX_ROWS-1]));
        } else {
            Point3D o = new Point3D(-0.5, -0.5, Cliff.STAGE_HEIGHT);
            Point3D e = new Point3D(0.5, -0.5, 0);
            Point3D ne = new Point3D(0.5, 0.5, 0);
            Point3D n = new Point3D(-0.5, 0.5, Cliff.STAGE_HEIGHT);
            Point3D middleE = new Point3D(-0.5, 0, Cliff.STAGE_HEIGHT);
            Point3D middleW = new Point3D(0.5, 0, 0);
            
            triangles.add(new Triangle3D(middleE, vertices[0][Cliff.NB_VERTEX_ROWS-1], n));
            for(int i=0; i<Cliff.NB_VERTEX_COL-1; i++){
                Point3D p1 = vertices[i][Cliff.NB_VERTEX_ROWS-1];
                Point3D p2 = vertices[i+1][Cliff.NB_VERTEX_ROWS-1];
                triangles.add(new Triangle3D(middleE, p2, p1));
            }
            triangles.add(new Triangle3D(middleE, o, vertices[Cliff.NB_VERTEX_COL-1][Cliff.NB_VERTEX_ROWS-1]));
            
            triangles.add(new Triangle3D(middleW, ne, vertices[0][0]));
            for(int i=0; i<Cliff.NB_VERTEX_COL-1; i++){
                Point3D p1 = vertices[i][0];
                Point3D p2 = vertices[i+1][0];
                triangles.add(new Triangle3D(middleW, p1, p2));
            }
            triangles.add(new Triangle3D(middleW, vertices[Cliff.NB_VERTEX_COL-1][0], e));
        }
        
        
        ArrayList<Triangle3D> res = new ArrayList<>();
        for(Triangle3D t : triangles)
            res.add(t.getRotationAroundZ(c.angle).getTranslation(c.getPos().x+0.5, c.getPos().y+0.5, c.level*Cliff.STAGE_HEIGHT));
            
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
        res.addAll(getTrianglesFrom(t));
        return res;
        
    }

    
    public void compute(){
        double texScale = 1d/128d;
        for(Tile tile : tiles){
 //           LogUtil.logger.info("tile : "+tile.getPos2D());
            ArrayList<Triangle3D> triangles = new ArrayList<>();
            if(tile.isCliff())
                triangles.addAll(getTrianglesFrom((Cliff)tile));
            else
                triangles.addAll(getTrianglesFrom(tile));
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
                
//                for(Triangle3D n : getTrianglesFromNeihbours(tile)){
//                    if(n==t)
//                        continue;
//                    if(t.a.equals(n.a) || t.a.equals(n.b) || t.a.equals(n.c))
//                        normal1 = normal1.getAddition(n.normal);
//
//                    if(t.b.equals(n.a) || t.b.equals(n.b) || t.b.equals(n.c))
//                        normal2 = normal2.getAddition(n.normal);
//
//                    if(t.c.equals(n.a) || t.c.equals(n.b) || t.c.equals(n.c))
//                        normal3 = normal3.getAddition(n.normal);
//                }
                if(normal1.isOrigin())
                    normals.add(new Point3D(1, 1, 1));
                else
                    normals.add(normal1.getNormalized());
                
                if(normal2.isOrigin())
                    normals.add(new Point3D(1, 1, 1));
                else
                    normals.add(normal2.getNormalized());

                if(normal3.isOrigin())
                    normals.add(new Point3D(1, 1, 1));
                else
                    normals.add(normal3.getNormalized());

                textCoord.add(new Point2D(t.a.x, t.a.y).getMult(texScale));
                textCoord.add(new Point2D(t.b.x, t.b.y).getMult(texScale));
                textCoord.add(new Point2D(t.c.x, t.c.y).getMult(texScale));
            }
        }
    }
}
