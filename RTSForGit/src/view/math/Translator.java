package view.math;

import geometry.Point2D;
import geometry3D.MyMesh;
import geometry3D.Point3D;

import java.awt.Color;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;

public class Translator {

	public static Vector2f toVector2f(Point2D p) {
		return new Vector2f((float) p.x, (float) p.y);
	}

	public static Vector3f toVector3f(Point2D p) {
		return new Vector3f((float) p.x, 0, (float) p.y);
	}

	public static Vector3f toVector3f(Point2D p, double elevation) {
		return new Vector3f((float)p.x, (float)p.y, (float)elevation);
	}

	public static ColorRGBA toColorRGBA(Color color) {
		float r = (float)color.getRed()/255f;
		float g = (float)color.getGreen()/255f;
		float b = (float)color.getBlue()/255f;
		float a = (float)color.getAlpha()/255f;
		return new ColorRGBA(r, g, b, a);
	}

	public static Point2D toPoint2D(Vector3f v) {
		return new Point2D(v.x, v.y);
	}
	public static Point3D toPoint3D(Vector3f v) {
		return new Point3D(v.x, v.y, v.z);
	}

        public static Point2D toPoint2D(Vector2f v) {
		return new Point2D(v.x, v.y);
	}
	
	public static Vector3f toVector3f(Point3D p) {
		return new Vector3f((float)p.x, (float)p.y, (float)p.z);
	}
	
	public static Vector3f toVector3fWithRelativeAngle(Point2D p, double angle){
		double x = p.x*Math.cos(angle);
		double y = Point2D.ORIGIN.getDistance(p)*Math.sin(angle);
		double z = p.y*Math.cos(angle);
		return new Vector3f((float)x, (float)y, (float)z);
	}

	public static Vector3f toAngledVector3f(Point2D p, double angleXY){
		double x = p.x*Math.cos(angleXY);
		double y = p.x*Math.sin(angleXY);
		double z = p.y;
		return new Vector3f((float)x, (float)y, (float)z);
	}

	public static Vector3f toAngledVector3f(Point2D p, Point2D pivot, double angleXY){
		double x = (p.x-pivot.x)*Math.cos(angleXY)+pivot.x;
		double y = (p.x-pivot.x)*Math.sin(angleXY)+pivot.x;
		double z = p.y;
		return new Vector3f((float)x, (float)y, (float)z);
	}
	
	public static Mesh toJMEMesh(MyMesh m) {
		Mesh res = new Mesh();
		float vertices[] = new float[m.vertices.size() * 3];
		float textCoord[] = new float[m.textCoord.size() * 2];
		float normals[] = new float[m.normals.size() * 3];
		int indices[] = new int[m.indices.size()];
		int j = 0;
		for (int i = 0; i < m.vertices.size() * 3; i += 3) {
			vertices[i] = (float)m.vertices.get(j).x;
			vertices[i + 1] = (float)m.vertices.get(j).y;
			vertices[i + 2] = (float)m.vertices.get(j).z;
			j++;
		}

		j = 0;
		for (int i = 0; i < m.textCoord.size() * 2; i += 2) {
			textCoord[i] = (float) m.textCoord.get(j).x;
			textCoord[i + 1] = (float) m.textCoord.get(j).y;
			j++;
		}

		j = 0;
		for (int i = 0; i < m.normals.size() * 3; i += 3) {
			normals[i] = (float)m.normals.get(j).x;
			normals[i + 1] = (float)m.normals.get(j).y;
			normals[i + 2] = (float)m.normals.get(j).z;
			j++;
		}

		for (int i = 0; i < m.indices.size(); i++) {
//			indices[i] = (short) (int) m.indices.get(i);
			indices[i] = m.indices.get(i);
		}

		res.setBuffer(Type.Position, 3, vertices);
		res.setBuffer(Type.TexCoord, 2, textCoord);
		res.setBuffer(Type.Normal, 3, normals);
		res.setBuffer(Type.Index, 3, indices);

		res.updateBound();
		return res;
	}
	
}
