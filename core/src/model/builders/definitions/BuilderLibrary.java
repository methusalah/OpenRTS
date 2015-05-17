/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.builders.definitions;

import exception.TechnicalException;
import geometry.tools.LogUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.battlefield.Battlefield;
import model.builders.Builder;
import model.builders.CliffShapeBuilder;
import model.builders.EffectBuilder;
import model.builders.ManmadeFaceBuilder;
import model.builders.MapStyleBuilder;
import model.builders.MoverBuilder;
import model.builders.NaturalFaceBuilder;
import model.builders.ProjectileBuilder;
import model.builders.TrinketBuilder;
import model.builders.TurretBuilder;
import model.builders.UnitBuilder;
import model.builders.WeaponBuilder;
import model.builders.actors.ActorBuilder;
import model.builders.actors.AnimationActorBuilder;
import model.builders.actors.ModelActorBuilder;
import model.builders.actors.ParticleActorBuilder;
import model.builders.actors.PhysicActorBuilder;
import model.builders.actors.SoundActorBuilder;

/**
 * @author Benoît
 */
public class BuilderLibrary {
	private static final String ERROR = "Impossible to find ";

	private static final String UNIT = "Unit";
	private static final String MOVER = "Mover";
	private static final String WEAPON = "Weapon";
	private static final String TURRET = "Turret";
	private static final String EFFECT = "Effect";
	private static final String PROJECTILE = "Projectile";
	private static final String ACTOR = "Actor";

	private static final String MAP_STYLE = "MapStyle";
	private static final String CLIFF_SHAPE = "CliffShape";
	private static final String TRINKET = "Trinket";
	private static final String NATURAL_FACE = "NaturalFace";
	private static final String MANMADE_FACE = "ManmadeFace";

	private Map<String, Map<String, Builder>> builders = new HashMap<>();

	public Battlefield battlefield;

	public BuilderLibrary() {
		builders.put(UNIT, new HashMap<String, Builder>());
		builders.put(MOVER, new HashMap<String, Builder>());
		builders.put(WEAPON, new HashMap<String, Builder>());
		builders.put(TURRET, new HashMap<String, Builder>());
		builders.put(EFFECT, new HashMap<String, Builder>());
		builders.put(PROJECTILE, new HashMap<String, Builder>());
		builders.put(ACTOR, new HashMap<String, Builder>());
		builders.put(MAP_STYLE, new HashMap<String, Builder>());
		builders.put(CLIFF_SHAPE, new HashMap<String, Builder>());
		builders.put(TRINKET, new HashMap<String, Builder>());
		builders.put(NATURAL_FACE, new HashMap<String, Builder>());
		builders.put(MANMADE_FACE, new HashMap<String, Builder>());
	}

	public void buildLinks() {
		LogUtil.logger.info("buildings links...");
		for (Map<String, Builder> map : builders.values()) {
			for (Builder b : map.values()) {
				b.readFinalizedLibrary();
			}
		}
	}

	public void submit(Definition def) {
		Map<String, Builder> typed = builders.get(def.type);
		if (typed == null) {
			throw new RuntimeException("Type '" + def.type + "' is unknown.");
		}

		switch (def.type) {
			case UNIT:
				typed.put(def.id, new UnitBuilder(def, this));
				break;
			case MOVER:
				typed.put(def.id, new MoverBuilder(def, this));
				break;
			case WEAPON:
				typed.put(def.id, new WeaponBuilder(def, this));
				break;
			case TURRET:
				typed.put(def.id, new TurretBuilder(def, this));
				break;
			case EFFECT:
				typed.put(def.id, new EffectBuilder(def, this));
				break;
			case PROJECTILE:
				typed.put(def.id, new ProjectileBuilder(def, this));
				break;
			case ACTOR:
				String actorType = def.getElement(ActorBuilder.TYPE) == null ? "" : def.getElement(ActorBuilder.TYPE).getVal();
				switch (actorType) {
					case ActorBuilder.TYPE_ANIMATION:
						typed.put(def.id, new AnimationActorBuilder(def, this));
						break;
					case ActorBuilder.TYPE_PARTICLE:
						typed.put(def.id, new ParticleActorBuilder(def, this));
						break;
					case ActorBuilder.TYPE_PHYSIC:
						typed.put(def.id, new PhysicActorBuilder(def, this));
						break;
					case ActorBuilder.TYPE_MODEL:
						typed.put(def.id, new ModelActorBuilder(def, this));
						break;
					case ActorBuilder.TYPE_SOUND:
						typed.put(def.id, new SoundActorBuilder(def, this));
						break;
					default:
						typed.put(def.id, new ActorBuilder(def, this));
				}
				break;
			case MAP_STYLE:
				typed.put(def.id, new MapStyleBuilder(def, this));
				break;
			case CLIFF_SHAPE:
				typed.put(def.id, new CliffShapeBuilder(def, this));
				break;
			case TRINKET:
				typed.put(def.id, new TrinketBuilder(def, this));
				break;
			case NATURAL_FACE:
				typed.put(def.id, new NaturalFaceBuilder(def, this));
				break;
			case MANMADE_FACE:
				typed.put(def.id, new ManmadeFaceBuilder(def, this));
				break;
		}
	}

	private <T extends Builder> T getBuilder(String type, String id, Class<T> clazz) {
		Builder res = builders.get(type).get(id);
		if (res == null) {
			throw new IllegalArgumentException(ERROR + type + "/" + id);
		}
		return (T) res;
	}

	public UnitBuilder getUnitBuilder(String id) {
		return getBuilder(UNIT, id, UnitBuilder.class);
	}

	public MoverBuilder getMoverBuilder(String id) {
		return getBuilder(MOVER, id, MoverBuilder.class);
	}

	public WeaponBuilder getWeaponBuilder(String id) {
		return getBuilder(WEAPON, id, WeaponBuilder.class);
	}

	public TurretBuilder getTurretBuilder(String id) {
		return getBuilder(TURRET, id, TurretBuilder.class);
	}

	public EffectBuilder getEffectBuilder(String id) {
		return getBuilder(EFFECT, id, EffectBuilder.class);
	}

	public ProjectileBuilder getProjectileBuilder(String id) {
		return getBuilder(PROJECTILE, id, ProjectileBuilder.class);
	}

	public ActorBuilder getActorBuilder(String id) {
		return getBuilder(ACTOR, id, ActorBuilder.class);
	}

	public MapStyleBuilder getMapStyleBuilder(String id) {
		return getBuilder(MAP_STYLE, id, MapStyleBuilder.class);
	}

	public CliffShapeBuilder getCliffShapeBuilder(String id) {
		return getBuilder(CLIFF_SHAPE, id, CliffShapeBuilder.class);
	}

	public TrinketBuilder getTrinketBuilder(String id) {
		return getBuilder(TRINKET, id, TrinketBuilder.class);
	}

	public NaturalFaceBuilder getNaturalFaceBuilder(String id) {
		return getBuilder(NATURAL_FACE, id, NaturalFaceBuilder.class);
	}

	public ManmadeFaceBuilder getManmadeFaceBuilder(String id) {
		return getBuilder(MANMADE_FACE, id, ManmadeFaceBuilder.class);
	}

	private <T extends Builder> List<T> getAllBuilders(String type, Class<T> clazz) {
		List<T> res = new ArrayList<>();
		res.addAll((Collection<? extends T>) (builders.get(type)).values());
		if (res.isEmpty()) {
			throw new TechnicalException("type '" + type + "' dosen't seem to have any element.");
		}
		return res;
	}

	public List<UnitBuilder> getAllUnitBuilders() {
		return getAllBuilders(UNIT, UnitBuilder.class);
	}

	public List<TrinketBuilder> getAllTrinketBuilders() {
		return getAllBuilders(TRINKET, TrinketBuilder.class);
	}

	public List<TrinketBuilder> getAllEditableTrinketBuilders() {
		List<TrinketBuilder> all = getAllBuilders(TRINKET, TrinketBuilder.class);
		List<TrinketBuilder> res = new ArrayList<>();
		for (TrinketBuilder b : all) {
			if (b.isEditable()) {
				res.add(b);
			}
		}
		return res;
	}

	public List<MapStyleBuilder> getAllMapStyleBuilders() {
		return getAllBuilders(MAP_STYLE, MapStyleBuilder.class);
	}

}
