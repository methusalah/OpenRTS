package view;

import geometry.Point2D;
import view.math.Translator;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

public class Pointer {
    public Pointer() {
    }

    public Geometry getPointedGeometry(Node n, Ray r) {
        CollisionResult collision = getCollision(n, r);
        if(collision == null)
            return null;
        return collision.getGeometry();
    }

    public Point2D getPointedCoord(Node n, Ray r) {
        CollisionResult collision = getCollision(n, r);
        if(collision == null)
            return null;
//        return Translator.toPoint2D(collision.getContactPoint());
        Vector3f p = collision.getContactPoint();
        return new Point2D(p.x, p.y);
    }

    private CollisionResult getCollision(Node n, Ray r){
        CollisionResults results = new CollisionResults();
        n.collideWith(r, results);
        if (results.size() == 0)
            return null;
        return results.getClosestCollision();
    }
}
