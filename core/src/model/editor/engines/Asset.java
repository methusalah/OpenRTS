package model.editor.engines;

import com.jme3.scene.Spatial;

import geometry.geom3d.Point3D;

public class Asset {

	public String modelPath;
	public double yaw, scale;
	public Point3D pos;
	public Spatial s = null;
	
	public Asset(String modelPath, double scale, double yaw, Point3D pos){
		this.modelPath = modelPath;
		this.scale = scale;
		this.yaw = yaw;
		this.pos = pos;
	}
}
