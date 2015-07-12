package model.battlefield.army.effects;

import geometry.geom3d.Point3D;

public interface EffectTarget {

	public void damage(EffectSource source, int amount);

	public double getRadius();

	public Point3D getPos();
}
