/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.builders.entity;

import geometry.math.AngleUtil;
import model.battlefield.army.components.Turret;
import model.battlefield.army.components.Unit;
import model.builders.entity.definitions.DefElement;
import model.builders.entity.definitions.Definition;

/**
 * @author Benoît
 */
public class TurretBuilder extends Builder {
	private static final String SPEED = "Speed";
	private static final String IDLE_SPEED = "IdleSpeed";
	private static final String ON_IDLE = "OnIdle";
	private static final String BONE_NAME = "BoneName";
	private static final String BONE_AXIS = "BoneAxis";

	private static final String RESET_ON_MOVE = "ResetOnMove";
	private static final String RESET = "Reset";
	private static final String SPIN = "Spin";
	private static final String HOLD = "Hold";

	private double speed;
	private double idleSpeed;
	private Turret.OnIdleBehave onIdle;
	private String boneName;
	private String boneAxis = "Y";

	public TurretBuilder(Definition def) {
		super(def);
		for (DefElement de : def.getElements()) {
			switch (de.name) {
				case SPEED:
					speed = AngleUtil.toRadians(de.getDoubleVal());
					break;
				case IDLE_SPEED:
					idleSpeed = AngleUtil.toRadians(de.getDoubleVal());
					break;
				case ON_IDLE:
					switch (de.getVal()) {
						case RESET_ON_MOVE:
							onIdle = Turret.OnIdleBehave.RESET_ON_MOVE;
							break;
						case RESET:
							onIdle = Turret.OnIdleBehave.RESET;
							break;
						case SPIN:
							onIdle = Turret.OnIdleBehave.SPIN;
							break;
						case HOLD:
							onIdle = Turret.OnIdleBehave.HOLD;
							break;
						default:
							throw new IllegalArgumentException(de.getVal() + " is not a valid onIdle value for Turret " + def.getId());
					}
					break;
				case BONE_NAME:
					boneName = de.getVal();
					break;
				case BONE_AXIS:
					boneAxis = de.getVal();
					break;
			}
		}
	}

	public Turret build(Unit holder) {
		return new Turret(speed, idleSpeed, onIdle, boneName, boneAxis, holder);
	}

	@Override
	public void readFinalizedLibrary() {
	}
}
