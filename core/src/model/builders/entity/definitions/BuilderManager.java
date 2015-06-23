/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model.builders.entity.definitions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.builders.entity.Builder;
import model.builders.entity.CliffShapeBuilder;
import model.builders.entity.EffectBuilder;
import model.builders.entity.ManmadeFaceBuilder;
import model.builders.entity.MapStyleBuilder;
import model.builders.entity.MoverBuilder;
import model.builders.entity.NaturalFaceBuilder;
import model.builders.entity.ProjectileBuilder;
import model.builders.entity.TrinketBuilder;
import model.builders.entity.TurretBuilder;
import model.builders.entity.UnitBuilder;
import model.builders.entity.WeaponBuilder;
import model.builders.entity.actors.ActorBuilder;
import model.builders.entity.actors.AnimationActorBuilder;
import model.builders.entity.actors.ModelActorBuilder;
import model.builders.entity.actors.ParticleActorBuilder;
import model.builders.entity.actors.PhysicActorBuilder;
import model.builders.entity.actors.SoundActorBuilder;
import exception.TechnicalException;

/**
 * @author Benoît
 */
public class BuilderManager {
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

	private static Map<String, Map<String, Builder>> builders = new HashMap<>();

	private BuilderManager() {
	}

	static {
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


	public static void buildLinks() {
		for (Map<String, Builder> map : builders.values()) {
			for (Builder b : map.values()) {
				b.readFinalizedLibrary();
			}
		}
	}

	public static void submit(Definition def) {
		Map<String, Builder> typed = builders.get(def.getType());
		if (typed == null) {
			throw new RuntimeException("Type '" + def.getType() + "' is unknown.");
		}

		switch (def.getType()) {
			case UNIT:
				typed.put(def.getId(), new UnitBuilder(def));
				break;
			case MOVER:
				typed.put(def.getId(), new MoverBuilder(def));
				break;
			case WEAPON:
				typed.put(def.getId(), new WeaponBuilder(def));
				break;
			case TURRET:
				typed.put(def.getId(), new TurretBuilder(def));
				break;
			case EFFECT:
				typed.put(def.getId(), new EffectBuilder(def));
				break;
			case PROJECTILE:
				typed.put(def.getId(), new ProjectileBuilder(def));
				break;
			case ACTOR:
				String actorType = def.getElement(ActorBuilder.TYPE) == null ? "" : def.getElement(ActorBuilder.TYPE).getVal();
				switch (actorType) {
					case ActorBuilder.TYPE_ANIMATION:
						typed.put(def.getId(), new AnimationActorBuilder(def));
						break;
					case ActorBuilder.TYPE_PARTICLE:
						typed.put(def.getId(), new ParticleActorBuilder(def));
						break;
					case ActorBuilder.TYPE_PHYSIC:
						typed.put(def.getId(), new PhysicActorBuilder(def));
						break;
					case ActorBuilder.TYPE_MODEL:
						typed.put(def.getId(), new ModelActorBuilder(def));
						break;
					case ActorBuilder.TYPE_SOUND:
						typed.put(def.getId(), new SoundActorBuilder(def));
						break;
					default:
						typed.put(def.getId(), new ActorBuilder(def));
				}
				break;
			case MAP_STYLE:
				typed.put(def.getId(), new MapStyleBuilder(def));
				break;
			case CLIFF_SHAPE:
				typed.put(def.getId(), new CliffShapeBuilder(def));
				break;
			case TRINKET:
				typed.put(def.getId(), new TrinketBuilder(def));
				break;
			case NATURAL_FACE:
				typed.put(def.getId(), new NaturalFaceBuilder(def));
				break;
			case MANMADE_FACE:
				typed.put(def.getId(), new ManmadeFaceBuilder(def));
				break;
		}
	}

	private static <T extends Builder> T getBuilder(String type, String id, Class<T> clazz) {
		Builder res = builders.get(type).get(id);
		if (res == null) {
			throw new IllegalArgumentException(ERROR + type + "/" + id);
		}
		return (T) res;
	}

	public static UnitBuilder getUnitBuilder(String id) {
		return getBuilder(UNIT, id, UnitBuilder.class);
	}

	public static MoverBuilder getMoverBuilder(String id) {
		return getBuilder(MOVER, id, MoverBuilder.class);
	}

	public static WeaponBuilder getWeaponBuilder(String id) {
		return getBuilder(WEAPON, id, WeaponBuilder.class);
	}

	public static TurretBuilder getTurretBuilder(String id) {
		return getBuilder(TURRET, id, TurretBuilder.class);
	}

	public static EffectBuilder getEffectBuilder(String id) {
		return getBuilder(EFFECT, id, EffectBuilder.class);
	}

	public static ProjectileBuilder getProjectileBuilder(String id) {
		return getBuilder(PROJECTILE, id, ProjectileBuilder.class);
	}

	public static ActorBuilder getActorBuilder(String id) {
		return getBuilder(ACTOR, id, ActorBuilder.class);
	}

	public static MapStyleBuilder getMapStyleBuilder(String id) {
		return getBuilder(MAP_STYLE, id, MapStyleBuilder.class);
	}

	public static CliffShapeBuilder getCliffShapeBuilder(String id) {
		return getBuilder(CLIFF_SHAPE, id, CliffShapeBuilder.class);
	}

	public static TrinketBuilder getTrinketBuilder(String id) {
		return getBuilder(TRINKET, id, TrinketBuilder.class);
	}

	public static NaturalFaceBuilder getNaturalFaceBuilder(String id) {
		return getBuilder(NATURAL_FACE, id, NaturalFaceBuilder.class);
	}

	public static ManmadeFaceBuilder getManmadeFaceBuilder(String id) {
		return getBuilder(MANMADE_FACE, id, ManmadeFaceBuilder.class);
	}

	private static <T extends Builder> List<T> getAllBuilders(String type, Class<T> clazz) {
		List<T> res = new ArrayList<>();
		res.addAll((Collection<? extends T>) (builders.get(type)).values());
		if (res.isEmpty()) {
			throw new TechnicalException("type '" + type + "' dosen't seem to have any element.");
		}
		return res;
	}

	public static List<UnitBuilder> getAllUnitBuilders() {
		return getAllBuilders(UNIT, UnitBuilder.class);
	}

	public static List<TrinketBuilder> getAllTrinketBuilders() {
		return getAllBuilders(TRINKET, TrinketBuilder.class);
	}

	public static List<TrinketBuilder> getAllEditableTrinketBuilders() {
		List<TrinketBuilder> all = getAllBuilders(TRINKET, TrinketBuilder.class);
		List<TrinketBuilder> res = new ArrayList<>();
		for (TrinketBuilder b : all) {
			if (b.isEditable()) {
				res.add(b);
			}
		}
		return res;
	}

	public static List<MapStyleBuilder> getAllMapStyleBuilders() {
		return getAllBuilders(MAP_STYLE, MapStyleBuilder.class);
	}

}
