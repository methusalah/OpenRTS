/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.mapDrawing;

import geometry.algorithm.Triangulator;
import geometry3D.MyMesh;
import geometry3D.Point3D;
import geometry3D.Polygon3D;
import geometry3D.Triangle3D;
import java.util.ArrayList;
import model.map.Map;
import model.map.Tile;


/**
 *
 * @author Benoît
 */
public class GridMesh extends MyMesh {

    static final double Z_OFFSET = 0.1;
    Map map;

    public GridMesh(Map map) {
        this.map = map;
        for(Tile t : map.getTiles()){
            if(t.n == null || t.e == null)
                continue;
            
            int index = vertices.size();
            vertices.add(t.getPos().getAddition(0, 0, Z_OFFSET));
            vertices.add(t.n.getPos().getAddition(0, 0, Z_OFFSET));
            vertices.add(t.n.e.getPos().getAddition(0, 0, Z_OFFSET));
            vertices.add(t.e.getPos().getAddition(0, 0, Z_OFFSET));

            normals.add(Point3D.UNIT_Z);
            normals.add(Point3D.UNIT_Z);
            normals.add(Point3D.UNIT_Z);
            normals.add(Point3D.UNIT_Z);

            textCoord.add(t.getPos2D());
            textCoord.add(t.n.getPos2D());
            textCoord.add(t.n.e.getPos2D());
            textCoord.add(t.e.getPos2D());

            indices.add(index);
            indices.add(index+2);
            indices.add(index+1);
            indices.add(index);
            indices.add(index+3);
            indices.add(index+2);
        }
    }
    
    public void update(){
        vertices.clear();
        for(Tile t : map.getTiles()){
            if(t.n == null || t.e == null)
                continue;
            vertices.add(t.getPos().getAddition(0, 0, Z_OFFSET));
            vertices.add(t.n.getPos().getAddition(0, 0, Z_OFFSET));
            vertices.add(t.n.e.getPos().getAddition(0, 0, Z_OFFSET));
            vertices.add(t.e.getPos().getAddition(0, 0, Z_OFFSET));
        }

    }
}
