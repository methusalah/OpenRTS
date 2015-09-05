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

import com.google.inject.Inject;
import com.google.inject.Injector;

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

	private Map<String, Map<String, Builder>> builders = new HashMap<>();
	
	@Inject
	Injector injector;
	
	@Inject
	BuilderManager() {
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
		for (Map<String, Builder> map : builders.values()) {
			for (Builder b : map.values()) {
				b.readFinalizedLibrary();
			}
		}
	}

	public void submit(Definition def) {
		Map<String, Builder> typed = builders.get(def.getType());
		if (typed == null) {
			throw new RuntimeException("Type '" + def.getType() + "' is unknown.");
		}
		Builder b = null;
		switch (def.getType()) {
			case UNIT:
				b = injector.getInstance(UnitBuilder.class);
				b.setDefinition(def);
				typed.put(def.getId(),b ); 
				break;
			case MOVER:
				b = injector.getInstance(MoverBuilder.class);
				b.setDefinition(def);
				typed.put(def.getId(),b );
				break;
			case WEAPON:
				b = injector.getInstance( WeaponBuilder.class);
				b.setDefinition(def);
				typed.put(def.getId(),b );
				break;
			case TURRET:
				b = injector.getInstance(TurretBuilder.class);
				b.setDefinition(def);
				typed.put(def.getId(), b);
				break;
			case EFFECT:
				b = injector.getInstance(EffectBuilder.class);
				b.setDefinition(def);
				typed.put(def.getId(), b);
				break;
			case PROJECTILE:
				b = injector.getInstance(ProjectileBuilder.class);
				b.setDefinition(def);
				typed.put(def.getId(), b);
				break;
			case ACTOR:
				String actorType = def.getElement(ActorBuilder.TYPE) == null ? "" : def.getElement(ActorBuilder.TYPE).getVal();
				switch (actorType) {
					case ActorBuilder.TYPE_ANIMATION:
						b = injector.getInstance(AnimationActorBuilder.class);
						b.setDefinition(def);
						typed.put(def.getId(),b );
						break;
					case ActorBuilder.TYPE_PARTICLE:
						b = injector.getInstance(ParticleActorBuilder.class);
						b.setDefinition(def);
						typed.put(def.getId(),b );
						break;
					case ActorBuilder.TYPE_PHYSIC:
						b = injector.getInstance(PhysicActorBuilder.class);
						b.setDefinition(def);
						typed.put(def.getId(),b );
						break;
					case ActorBuilder.TYPE_MODEL:
						b = injector.getInstance(ModelActorBuilder.class);
						b.setDefinition(def);
						typed.put(def.getId(),b );
						break;
					case ActorBuilder.TYPE_SOUND:
						b = injector.getInstance(SoundActorBuilder.class);
						b.setDefinition(def);
						typed.put(def.getId(),b );
						break;
					default:
						b = injector.getInstance(ActorBuilder.class);
						b.setDefinition(def);
						typed.put(def.getId(),b );
				}
				break;
			case MAP_STYLE:
				b = injector.getInstance( MapStyleBuilder.class);
				b.setDefinition(def);
				typed.put(def.getId(),b );
				break;
			case CLIFF_SHAPE:
				b = injector.getInstance( CliffShapeBuilder.class);
				b.setDefinition(def);
				typed.put(def.getId(),b );
				break;
			case TRINKET:
				b = injector.getInstance( TrinketBuilder.class);
				b.setDefinition(def);
				typed.put(def.getId(), b);
				break;
			case NATURAL_FACE:
				b = injector.getInstance( NaturalFaceBuilder.class);
				b.setDefinition(def);
				typed.put(def.getId(),b );
				break;
			case MANMADE_FACE:
				b = injector.getInstance(ManmadeFaceBuilder.class);
				b.setDefinition(def);
				typed.put(def.getId(), b);
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
