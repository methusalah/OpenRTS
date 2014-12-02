/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.mapDrawing;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Quad;
import geometry.Point2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import model.map.Map;
import model.map.Tile;
import model.map.editor.MapEditor;
import model.map.editor.TileSelector;
import view.material.MaterialManager;
import view.math.Translator;

/**
 *
 * @author Benoît
 */
public class EditorRenderer implements ActionListener {
    private final Map map;
    private final MapEditor editor;
    private final MaterialManager mm;
    
    public Node mainNode = new Node();
    public Node gridNode = new Node();
    public Node pencilNode = new Node();
    public ArrayList<Node> pencilTiles = new ArrayList<>();
    private Geometry gridGeom;
    private GridMesh gridMesh;

    public EditorRenderer(Map map, MapEditor editor, MaterialManager mm) {
        this.map = map;
        this.mm = mm;
        this.editor = editor;
                
        
        gridMesh = new GridMesh(map);
        gridGeom = new Geometry();
        gridGeom.setMesh(Translator.toJMEMesh(gridMesh));
        Material mat = mm.getColor(ColorRGBA.Black);
        mat.getAdditionalRenderState().setWireframe(true);
        gridGeom.setMaterial(mat);

        gridNode.attachChild(gridGeom);
        mainNode.attachChild(gridNode);
        
        Geometry g = new Geometry();
        g.setMesh(new Box(0.5f, 0.5f, (float)Tile.STAGE_HEIGHT/2));
        Material mat2 = mm.getColor(ColorRGBA.Green);
        mat2.getAdditionalRenderState().setWireframe(true);
        g.setMaterial(mat2);
        g.setLocalTranslation(0, 0, (float)Tile.STAGE_HEIGHT/2+0.1f);
//        pencilNode.attachChild(g);
        mainNode.attachChild(pencilNode);
        
        for(int i=0; i<150; i++){
            Node n = new Node();
            Geometry l1 = new Geometry();
            l1.setMesh(new Line(new Vector3f(0, 0, 0.1f), new Vector3f(0, 1, 0.1f)));
            l1.setMaterial(mm.greenMaterial);
            n.attachChild(l1);
            Geometry l2 = new Geometry();
            l2.setMesh(new Line(new Vector3f(0, 1, 0.1f), new Vector3f(1, 1, 0.1f)));
            l2.setMaterial(mm.greenMaterial);
            n.attachChild(l2);
            Geometry l3 = new Geometry();
            l3.setMesh(new Line(new Vector3f(1, 1, 0.1f), new Vector3f(1, 0, 0.1f)));
            l3.setMaterial(mm.greenMaterial);
            n.attachChild(l3);
            Geometry l4 = new Geometry();
            l4.setMesh(new Line(new Vector3f(1, 0, 0.1f), new Vector3f(0, 0, 0.1f)));
            l4.setMaterial(mm.greenMaterial);
            n.attachChild(l4);
            
            Geometry lv1 = new Geometry();
            lv1.setMesh(new Line(new Vector3f(0, 0, 0.1f), new Vector3f(0, 0, -10)));
            lv1.setMaterial(mm.greenMaterial);
            n.attachChild(lv1);
            Geometry lv2 = new Geometry();
            lv2.setMesh(new Line(new Vector3f(0, 1, 0.1f), new Vector3f(0, 1, -10)));
            lv2.setMaterial(mm.greenMaterial);
            n.attachChild(lv2);
            Geometry lv3 = new Geometry();
            lv3.setMesh(new Line(new Vector3f(1, 1, 0.1f), new Vector3f(1, 1, -10)));
            lv3.setMaterial(mm.greenMaterial);
            n.attachChild(lv3);
            Geometry lv4 = new Geometry();
            lv4.setMesh(new Line(new Vector3f(1, 0, 0.1f), new Vector3f(1, 0, -10)));
            lv4.setMaterial(mm.greenMaterial);
            n.attachChild(lv4);
            pencilNode.attachChild(n);
        }
    }
    
    
    public void drawPencilPreview(Point2D coord) {
        Quaternion q = new Quaternion();
        q.fromAngles(0, 0, (float)editor.selector.getShapeAngle());
        pencilNode.setLocalRotation(q);
        pencilNode.setLocalScale((float)editor.selector.radius*2, (float)editor.selector.radius*2, 0);
        pencilNode.setLocalTranslation(Translator.toVector3f(editor.selector.pos, 0.1));
    }
    
    public void drawPencilPreview() {
        ArrayList<Tile> tiles = editor.selector.getTiles();
        int index = 0;
        for(Spatial s : pencilNode.getChildren()){
            if(index < tiles.size())
                s.setLocalTranslation(Translator.toVector3f(tiles.get(index).getPos2D(), (float)editor.selector.getElevation()+0.1f));
            else
                s.setLocalTranslation(Vector3f.ZERO);
            index++;
        }
    }
    
    public void toggleGrid(){
        if(mainNode.hasChild(gridNode))
            mainNode.detachChild(gridNode);
        else
            mainNode.attachChild(gridNode);
            
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        ArrayList<Tile> updated = (ArrayList<Tile>)(e.getSource());
        if(!updated.isEmpty()){
            gridMesh.update();
            gridGeom.setMesh(Translator.toJMEMesh(gridMesh));
        }
    }


}
