/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.builders.entity;

import geometry.geom3d.Point3D;
import model.battlefield.army.ArmyManager;
import model.battlefield.army.components.Projectile;
import model.battlefield.army.effects.EffectSource;
import model.battlefield.army.effects.EffectTarget;
import model.builders.entity.actors.ModelActorBuilder;
import model.builders.entity.definitions.BuilderManager;
import model.builders.entity.definitions.DefElement;
import model.builders.entity.definitions.Definition;

/**
 * @author Benoît
 */
public class ProjectileBuilder extends Builder {
	private static final String SPEED = "Speed";
	private static final String ACCELERATION = "Acceleration";
	private static final String MASS = "Mass";
	private static final String MOVER_LINK = "MoverLink";
	private static final String ACTOR_LINK = "ActorLink";

	private static final String PRECISION = "Precision";
	private static final String PRECISION_CENTER = "Center";
	private static final String PRECISION_IN_RADIUS = "InRadius";

	private double radius = 0;
	private double speed;
	private double acceleration = 1000;
	private double mass;
	private String moverLink;
	private MoverBuilder moverBuilder;
	private String actorLink;
	private ModelActorBuilder actorBuilder;
	private Projectile.PRECISION_TYPE precisionType;
	private double precision;

	public ProjectileBuilder(Definition def) {
		super(def);
		for (DefElement de : def.getElements()) {
			switch (de.name) {
				case SPEED:
					speed = de.getDoubleVal();
					break;
				case ACCELERATION:
					acceleration = de.getDoubleVal();
					break;
				case MASS:
					mass = de.getDoubleVal();
					break;
				case MOVER_LINK:
					moverLink = de.getVal();
					break;
				case ACTOR_LINK:
					actorLink = de.getVal();
					break;
				case PRECISION:
					switch (de.getVal()) {
						case PRECISION_IN_RADIUS:
							precisionType = Projectile.PRECISION_TYPE.IN_RADIUS;
							break;
						case PRECISION_CENTER:
							precisionType = Projectile.PRECISION_TYPE.CENTER;
							break;
						default:
							precisionType = Projectile.PRECISION_TYPE.OTHER;
							precision = de.getDoubleVal();
							break;
					}
			}
		}
	}

	public Projectile build(EffectSource source, EffectTarget target, Point3D targetPoint) {
		Projectile res = new Projectile(radius, speed, acceleration, 1000, mass, source, moverBuilder, precisionType, precision, actorBuilder, target, targetPoint);
		ArmyManager.registerProjectile(res);
		return res;
	}

	@Override
	public void readFinalizedLibrary() {
		moverBuilder = BuilderManager.getMoverBuilder(moverLink);
		actorBuilder = (ModelActorBuilder) BuilderManager.getActorBuilder(actorLink);
	}

}
