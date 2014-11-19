/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model.map;

import geometry.Point2D;
import geometry3D.MyMesh;
import geometry3D.Point3D;
import geometry3D.Triangle3D;
import tools.LogUtil;

/**
 *
 * @author Benoît
 */
public class CliffMesh extends MyMesh {
    
    public CliffMesh(Cliff cliff){
        Point3D[][] grid = cliff.getVertices();
        Point2D uv0 = new Point2D(grid[0][0].y, grid[0][0].z);
        double x = uv0.x;
        double y = uv0.y; 
        Point2D uv1 = new Point2D(grid[Cliff.NB_VERTEX_COL-1][Cliff.NB_VERTEX_ROWS-1].y, grid[Cliff.NB_VERTEX_COL-1][Cliff.NB_VERTEX_ROWS-1].z);
        double xScale = 1/(uv1.x-uv0.x);
        double yScale = 1/(uv1.y-uv0.y);
        
//        if(cliff.ortho){
//            LogUtil.logger.info("ortho");
//            LogUtil.logger.info("xscale, yscale : "+xScale+yScale);
//            LogUtil.logger.info("origin : "+grid[0][0]);
//            LogUtil.logger.info("end : "+grid[2][4]);
//        } else if(cliff.acuteDiag)
//            LogUtil.logger.info("acute");
//        else
//            LogUtil.logger.info("obtuse");
        
        
        for(int col=0; col<Cliff.NB_VERTEX_COL-1; col++)
            for(int row=0; row<Cliff.NB_VERTEX_ROWS-1; row++){
                Triangle3D t1 = new Triangle3D(
                        grid[col][row],
                        grid[col][row+1],
                        grid[col+1][row]
                        );
                Triangle3D t2 = new Triangle3D(
                        grid[col+1][row],
                        grid[col][row+1],
                        grid[col+1][row+1]
                        );
                
                int index = vertices.size();
                vertices.add(t1.a);
                vertices.add(t1.b);
                vertices.add(t1.c);
                indices.add(index);
                indices.add(index+1);
                indices.add(index+2);
                normals.add(t1.normal);
                normals.add(t1.normal);
                normals.add(t1.normal);
                textCoord.add(new Point2D((t1.a.x-x)*xScale, (t1.a.y-y)*yScale));
                textCoord.add(new Point2D((t1.b.x-x)*xScale, (t1.b.y-y)*yScale));
                textCoord.add(new Point2D((t1.c.x-x)*xScale, (t1.c.y-y)*yScale));
                

                index = vertices.size();
                vertices.add(t2.a);
                vertices.add(t2.b);
                vertices.add(t2.c);
                indices.add(index);
                indices.add(index+1);
                indices.add(index+2);
                normals.add(t2.normal);
                normals.add(t2.normal);
                normals.add(t2.normal);
                textCoord.add(new Point2D((t2.a.x-x)*xScale, (t2.a.y-y)*yScale));
                textCoord.add(new Point2D((t2.b.x-x)*xScale, (t2.b.y-y)*yScale));
                textCoord.add(new Point2D((t2.c.x-x)*xScale, (t2.c.y-y)*yScale));
                
                
            }
    }
}
