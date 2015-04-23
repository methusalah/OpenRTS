/*
 * To change this template, choose Tools | Templates
 * and open the template in the toolManager.
 */
package view.mapDrawing;

import geometry.collections.PointRing;
import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import geometry.math.Angle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;

import model.battlefield.map.Tile;
import model.battlefield.map.parcel.ParcelMesh;
import model.editor.Pencil;
import view.View;
import view.material.MaterialManager;
import view.math.Translator;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;

/**
 *
 * @author Benoît
 */
public class EditorRenderer implements ActionListener {
    public static final int CIRCLE_PENCIL_SAMPLE_COUNT = 30;
    public static final double QUAD_PENCIL_SAMPLE_LENGTH = 0.5;
    public static final int PENCIL_THICKNESS = 3;
    
    View view;
    private final MaterialManager mm;
    
    public Node mainNode = new Node();
    public Node gridNode = new Node();
    public Node CliffPencilNode = new Node();
    public Node HeightPencilNode = new Node();
    public Node AtlasPencilNode = new Node();
    private HashMap<ParcelMesh, GridMesh> gridMeshes = new HashMap<>();
    private HashMap<ParcelMesh, Geometry> gridGeoms = new HashMap<>();
    

    public EditorRenderer(View view, MaterialManager mm) {
        this.view = view;
        this.mm = mm;
                
        for(ParcelMesh parcel : view.model.battlefield.parcelManager.meshes){
        	GridMesh grid = new GridMesh(parcel);
        	gridMeshes.put(parcel, grid);

            Geometry g = new Geometry();
            g.setMesh(Translator.toJMEMesh(grid));
            Material mat = mm.getColor(ColorRGBA.Black);
            mat.getAdditionalRenderState().setWireframe(true);
            g.setMaterial(mat);
            gridNode.attachChild(g);
            gridGeoms.put(parcel, g);
        }
        
        
        mainNode.attachChild(gridNode);
        
        mainNode.attachChild(CliffPencilNode);
        mainNode.attachChild(HeightPencilNode);
        mainNode.attachChild(AtlasPencilNode);
        
        BuildCliffPencil();
        BuildHeightPencil();
        BuildAtlasPencil();
    }
    
    private void BuildCliffPencil(){
        for(int i=0; i< Pencil.MAX_SIZE*Pencil.MAX_SIZE; i++){
            Node n = new Node();
            Geometry l1 = new Geometry();
            Line l = new Line(new Vector3f(0, 0, 0.1f), new Vector3f(0, 1, 0.1f));
            l.setLineWidth(PENCIL_THICKNESS);
            l1.setMesh(l);
            l1.setMaterial(mm.getColor(ColorRGBA.Orange));
            n.attachChild(l1);

            Geometry l2 = new Geometry();
            l = new Line(new Vector3f(0, 1, 0.1f), new Vector3f(1, 1, 0.1f));
            l.setLineWidth(PENCIL_THICKNESS);
            l2.setMesh(l);
            l2.setMaterial(mm.getColor(ColorRGBA.Orange));
            n.attachChild(l2);
            
            Geometry l3 = new Geometry();
            l = new Line(new Vector3f(1, 1, 0.1f), new Vector3f(1, 0, 0.1f));
            l.setLineWidth(PENCIL_THICKNESS);
            l3.setMesh(l);
            l3.setMaterial(mm.getColor(ColorRGBA.Orange));
            n.attachChild(l3);
            
            Geometry l4 = new Geometry();
            l = new Line(new Vector3f(1, 0, 0.1f), new Vector3f(0, 0, 0.1f));
            l.setLineWidth(PENCIL_THICKNESS);
            l4.setMesh(l);
            l4.setMaterial(mm.getColor(ColorRGBA.Orange));
            n.attachChild(l4);
            
            Geometry lv1 = new Geometry();
            lv1.setMesh(new Line(new Vector3f(0, 0, 0.1f), new Vector3f(0, 0, -10)));
            lv1.setMaterial(mm.getColor(ColorRGBA.Orange));
            n.attachChild(lv1);
            Geometry lv2 = new Geometry();
            lv2.setMesh(new Line(new Vector3f(0, 1, 0.1f), new Vector3f(0, 1, -10)));
            lv2.setMaterial(mm.getColor(ColorRGBA.Orange));
            n.attachChild(lv2);
            Geometry lv3 = new Geometry();
            lv3.setMesh(new Line(new Vector3f(1, 1, 0.1f), new Vector3f(1, 1, -10)));
            lv3.setMaterial(mm.getColor(ColorRGBA.Orange));
            n.attachChild(lv3);
            Geometry lv4 = new Geometry();
            lv4.setMesh(new Line(new Vector3f(1, 0, 0.1f), new Vector3f(1, 0, -10)));
            lv4.setMaterial(mm.getColor(ColorRGBA.Orange));
            n.attachChild(lv4);
            CliffPencilNode.attachChild(n);
        }
    }
    
    private void BuildHeightPencil(){
        for(int i=0; i<Pencil.MAX_SIZE*Pencil.MAX_SIZE; i++){
            Geometry g = new Geometry();
            g.setMesh(new Line(new Vector3f(-1000, -1000, 0), new Vector3f(-1000, -1000, 1)));
            g.setMaterial(mm.getColor(ColorRGBA.Orange));
            HeightPencilNode.attachChild(g);
        }
    }

    private void BuildAtlasPencil(){
        for(int i=0; i<Pencil.MAX_SIZE*8; i++){
            Geometry g = new Geometry();
            g.setMesh(new Line(new Vector3f(-1000, -1000, 0), new Vector3f(-1000, -1000, 1)));
            g.setMaterial(mm.getColor(ColorRGBA.Orange));
            AtlasPencilNode.attachChild(g);
        }
    }

    
    public void drawPencil() {
        if(view.model.toolManager.actualTool == view.model.toolManager.cliffTool ||
                view.model.toolManager.actualTool == view.model.toolManager.rampTool){
            drawCliffPencil();
        } else if(view.model.toolManager.actualTool == view.model.toolManager.heightTool){
            drawHeightPencil();
        } else if(view.model.toolManager.actualTool == view.model.toolManager.atlasTool){
            drawAtlasPencil();
        }
    }
    
    private void drawCliffPencil() {
        List<Tile> tiles = view.model.toolManager.cliffTool.pencil.getTiles();
        int index = 0;
        for(Spatial s : CliffPencilNode.getChildren()){
            if(index < tiles.size())
                s.setLocalTranslation(Translator.toVector3f(tiles.get(index).getCoord(), (float)view.model.toolManager.cliffTool.pencil.getElevation()+0.1f));
            else
                s.setLocalTranslation(new Vector3f(-1000, -1000, 0));
            index++;
        }
    }
    private void drawHeightPencil() {
        List<Tile> tiles = view.model.toolManager.heightTool.pencil.getNodes();
        int index = 0;
        for(Spatial s : HeightPencilNode.getChildren()){
            if(index < tiles.size()){
                Point3D start = tiles.get(index).getPos();
                Point3D end = tiles.get(index).getPos().getAddition(0, 0, 0.5);
                Line l = new Line(Translator.toVector3f(start), Translator.toVector3f(end));
                l.setLineWidth(PENCIL_THICKNESS);
                ((Geometry)s).setMesh(l);
                s.setLocalTranslation(Vector3f.ZERO);
//                s.setLocalTranslation(Translator.toVector3f(tiles.get(index).getPos2D(), (float)toolManager.selector.getElevation()+0.1f));
            } else
                s.setLocalTranslation(new Vector3f(-1000, -1000, 0));
            index++;
        }
    }
    private void drawAtlasPencil() {
        Pencil s = view.model.toolManager.atlasTool.pencil;
        PointRing pr = new PointRing();
        Point2D center = view.model.toolManager.actualTool.pencil.getCoord();
        
        if(s.shape == Pencil.Shape.Square ||
                s.shape == Pencil.Shape.Diamond){
            for(double i=-s.size/2; i<s.size/2; i+=QUAD_PENCIL_SAMPLE_LENGTH)
                pr.add(center.getAddition(i, -s.size/2));
            for(double i=-s.size/2; i<s.size/2; i+=QUAD_PENCIL_SAMPLE_LENGTH)
                pr.add(center.getAddition(s.size/2, i));
            for(double i=s.size/2; i>-s.size/2; i-=QUAD_PENCIL_SAMPLE_LENGTH)
                pr.add(center.getAddition(i, s.size/2));
            for(double i=s.size/2; i>-s.size/2; i-=QUAD_PENCIL_SAMPLE_LENGTH)
                pr.add(center.getAddition(-s.size/2, i));
            if(s.shape == Pencil.Shape.Diamond){
                PointRing newPR = new PointRing();
                for(Point2D p : pr)
                    newPR.add(p.getRotation(Angle.RIGHT/2, center));
                pr = newPR;
            }
        } else {
            Point2D revol = center.getAddition(s.size/2, 0);
            for(int i=0; i<CIRCLE_PENCIL_SAMPLE_COUNT; i++)
                pr.add(revol.getRotation(Angle.FLAT*2*i/CIRCLE_PENCIL_SAMPLE_COUNT, center));
        }
            
        int index = 0;
        for(Spatial spatial : AtlasPencilNode.getChildren()){
            if(index < pr.size() &&
                    view.model.battlefield.map.isInBounds(pr.get(index)) &&
                    view.model.battlefield.map.isInBounds(pr.getNext(index))){
                Point3D start = pr.get(index).get3D(view.model.battlefield.map.getAltitudeAt(pr.get(index))+0.1);
                Point3D end = pr.getNext(index).get3D(view.model.battlefield.map.getAltitudeAt(pr.getNext(index))+0.1);
                Line l = new Line(Translator.toVector3f(start), Translator.toVector3f(end));
                l.setLineWidth(PENCIL_THICKNESS);
                ((Geometry)spatial).setMesh(l);
                spatial.setLocalTranslation(Vector3f.ZERO);
            } else
                spatial.setLocalTranslation(new Vector3f(-1000, -1000, 0));
            index++;
        }
    }
    
    private void hideCliffPencil(){
        for(Spatial s : CliffPencilNode.getChildren()){
            s.setLocalTranslation(new Vector3f(-1000, -1000, 0));
        }
    }
    private void hideHeightPencil(){
        for(Spatial s : HeightPencilNode.getChildren()){
            s.setLocalTranslation(new Vector3f(-1000, -1000, 0));
        }
    }
    private void hideAtlasPencil(){
        for(Spatial s : AtlasPencilNode.getChildren()){
            s.setLocalTranslation(new Vector3f(-1000, -1000, 0));
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
        switch(e.getActionCommand()){
            case "tool" : 
                if(view.model.toolManager.actualTool != view.model.toolManager.cliffTool &&
                        view.model.toolManager.actualTool != view.model.toolManager.rampTool)
                    hideCliffPencil();
                if(view.model.toolManager.actualTool != view.model.toolManager.heightTool)
                    hideHeightPencil();
                if(view.model.toolManager.actualTool != view.model.toolManager.atlasTool)
                    hideAtlasPencil();
                break;
            case "parcels" : 
            	List<ParcelMesh> updatedParcels = (List<ParcelMesh>)(e.getSource());
            	for(ParcelMesh parcel : updatedParcels){
            		GridMesh m = gridMeshes.get(parcel); 
            		m.update();
            		gridGeoms.get(parcel).setMesh(Translator.toJMEMesh(m));
            	}
                break;
        }
    }
}
