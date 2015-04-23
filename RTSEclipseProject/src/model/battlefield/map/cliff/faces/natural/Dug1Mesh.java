package model.battlefield.map.cliff.faces.natural;

import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import geometry.geom3d.Triangle3D;
import model.battlefield.map.Tile;

public class Dug1Mesh extends NaturalFaceMesh {
    
    public Dug1Mesh(Point3D[][] grid){
        Point2D uv0 = new Point2D(grid[0][0].y, grid[0][0].z);
        double x = uv0.x;
        double y = uv0.y; 
        Point2D uv1 = new Point2D(grid[Dug1.NB_VERTEX_COL-1][Dug1.NB_VERTEX_ROWS-1].y, grid[Dug1.NB_VERTEX_COL-1][Dug1.NB_VERTEX_ROWS-1].z);
        double xScale = 1/(uv1.x-uv0.x);
        double yScale = 1/(uv1.y-uv0.y);
        
        boolean pair = false;
        for(int col=0; col<Dug1.NB_VERTEX_COL-1; col++){
            pair = !pair;
            for(int row=0; row<Dug1.NB_VERTEX_ROWS-1; row++){
                Point3D sw = grid[col][row];
                Point3D nw = grid[col][row+1];
                Point3D ne = grid[col+1][row+1];
                Point3D se = grid[col+1][row];

                Point3D sw2 = sw;
                Point3D nw2 = nw;
                Point3D ne2 = ne;
                Point3D se2 = se;

                double offset = Tile.STAGE_HEIGHT/Dug1.NB_VERTEX_ROWS/4;

                Triangle3D t1;
                Triangle3D t2;
                if(pair){
                    if(row > 0){
                        Point3D seBottom = grid[col+1][row-1];
                        sw2 = sw.getAddition(nw.getSubtraction(sw).getScaled(offset));
                        se2 = se.getAddition(seBottom.getSubtraction(se).getScaled(offset));
                    }
                    if(row < Dug1.NB_VERTEX_ROWS-2){
                        Point3D nwTop = grid[col][row+2];
                        nw2 = nw.getAddition(nwTop.getSubtraction(nw).getScaled(offset));
                        ne2 = ne.getAddition(se.getSubtraction(ne).getScaled(offset));
                    }
                    t1 = new Triangle3D(sw2, nw2, ne2);
                    t2 = new Triangle3D(sw2, ne2, se2);
                } else{
                    if(row > 0){
                        Point3D swBottom = grid[col][row-1];
                        sw2 = sw.getAddition(swBottom.getSubtraction(sw).getScaled(offset));
                        se2 = se.getAddition(ne.getSubtraction(se).getScaled(offset));
                    }
                    if(row < Dug1.NB_VERTEX_ROWS-2){
                        Point3D neTop = grid[col+1][row+2];
                        nw2 = nw.getAddition(sw.getSubtraction(nw).getScaled(offset));
                        ne2 = ne.getAddition(neTop.getSubtraction(ne).getScaled(offset));
                    }
                    t1 = new Triangle3D(sw2, nw2, se2);
                    t2 = new Triangle3D(se2, nw2, ne2);
                }
                
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
}
