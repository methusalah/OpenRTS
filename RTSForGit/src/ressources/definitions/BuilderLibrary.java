/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ressources.definitions;

import geometry.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import math.MyRandom;
import model.battlefield.army.ArmyManager;
import model.builders.actors.ActorBuilder;
import model.builders.EffectBuilder;
import model.builders.MoverBuilder;
import model.builders.ProjectileBuilder;
import model.builders.TurretBuilder;
import model.builders.UnitBuilder;
import model.builders.WeaponBuilder;
import ressources.definitions.Definition;
import model.battlefield.map.Map;
import model.builders.CliffShapeBuilder;
import model.builders.ManmadeFaceBuilder;
import model.builders.MapStyleBuilder;
import model.builders.NaturalFaceBuilder;
import model.builders.TrinketBuilder;
import model.battlefield.warfare.Faction;
import model.builders.actors.AnimationActorBuilder;
import model.builders.actors.ModelActorBuilder;
import model.builders.actors.ParticleActorBuilder;
import model.builders.actors.PhysicActorBuilder;
import tools.LogUtil;

/**
 *
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


    
    private HashMap<String, UnitBuilder> unitBuilders = new HashMap<>();
    private HashMap<String, MoverBuilder> moverBuilders = new HashMap<>();
    private HashMap<String, WeaponBuilder> weaponBuilders = new HashMap<>();
    private HashMap<String, TurretBuilder> turretBuilders = new HashMap<>();
    private HashMap<String, EffectBuilder> effectBuilders = new HashMap<>();
    private HashMap<String, ProjectileBuilder> projectileBuilders = new HashMap<>();
    private HashMap<String, ActorBuilder> actorBuilders = new HashMap<>();

    private HashMap<String, MapStyleBuilder> mapStyleBuilders = new HashMap<>();
    private HashMap<String, CliffShapeBuilder> cliffShapeBuilders = new HashMap<>();
    private HashMap<String, TrinketBuilder> trinketBuilders = new HashMap<>();
    private HashMap<String, NaturalFaceBuilder> naturalFaceBuilders = new HashMap<>();
    private HashMap<String, ManmadeFaceBuilder> manmadeFaceBuilders = new HashMap<>();

    public Map map;
    public ArmyManager armyManager;
    
    public BuilderLibrary(){
    }
    
    
    public void submit(Definition def){
        switch (def.type){
            case UNIT : submitUnit(def); break;
            case MOVER : submitMover(def); break;
            case WEAPON : submitWeapon(def); break;
            case TURRET : submitTurret(def); break;
            case EFFECT : submitEffect(def); break;
            case PROJECTILE : submitProjectile(def); break;
            case ACTOR : submitActor(def); break;

            case MAP_STYLE : submitMapStyle(def); break;
            case CLIFF_SHAPE : submitCliffShape(def); break;
            case TRINKET : submitTrinket(def); break;
            case NATURAL_FACE : submitNaturalFace(def); break;
            case MANMADE_FACE : submitManmadeFace(def); break;
        }
    }
    
    private void submitUnit(Definition def){
        unitBuilders.put(def.id, new UnitBuilder(def, this));
    }

    private void submitMover(Definition def){
        moverBuilders.put(def.id, new MoverBuilder(def, this));
    }

    private void submitWeapon(Definition def){
        weaponBuilders.put(def.id, new WeaponBuilder(def, this));
    }

    private void submitTurret(Definition def){
        turretBuilders.put(def.id, new TurretBuilder(def, this));
    }

    private void submitEffect(Definition def){
        effectBuilders.put(def.id, new EffectBuilder(def, this));
    }

    private void submitProjectile(Definition def){
        projectileBuilders.put(def.id, new ProjectileBuilder(def, this));
    }
    private void submitActor(Definition def){
        ActorBuilder b = null;
        String type = def.getElement(ActorBuilder.TYPE) == null? "": def.getElement(ActorBuilder.TYPE).getVal();
        switch(type){
            case ActorBuilder.TYPE_ANIMATION : b = new AnimationActorBuilder(def, this); break;
            case ActorBuilder.TYPE_PARTICLE : b = new ParticleActorBuilder(def, this); break;
            case ActorBuilder.TYPE_PHYSIC : b = new PhysicActorBuilder(def, this); break;
            case ActorBuilder.TYPE_PROJECTILE : b = new ModelActorBuilder(def, this); break;
            case ActorBuilder.TYPE_UNIT : b = new ModelActorBuilder(def, this); break;
            case ActorBuilder.TYPE_DEFAULT :
            default: b = new ActorBuilder(def, this);
        }
        actorBuilders.put(def.id, b);
    }
    
    private void submitMapStyle(Definition def){
        mapStyleBuilders.put(def.id, new MapStyleBuilder(def, this));
    }
    private void submitCliffShape(Definition def){
        cliffShapeBuilders.put(def.id, new CliffShapeBuilder(def, this));
    }
    private void submitTrinket(Definition def){
        trinketBuilders.put(def.id, new TrinketBuilder(def));
    }
    private void submitNaturalFace(Definition def){
        naturalFaceBuilders.put(def.id, new NaturalFaceBuilder(def));
    }
    private void submitManmadeFace(Definition def){
        manmadeFaceBuilders.put(def.id, new ManmadeFaceBuilder(def));
    }
    
    
    
    
    public void buildUnitFromRace(String race, Faction faction, Point2D pos){
        ArrayList<UnitBuilder> subList = new ArrayList<>();
        for(UnitBuilder ub : unitBuilders.values())
            if(ub.hasRace(race))
                subList.add(ub);
        
        int i = (int)Math.floor(MyRandom.next()*subList.size());
        subList.get(i).build(faction, pos.get3D(0));
    }
    
    public UnitBuilder getUnitBuilder(String id){
        UnitBuilder res = unitBuilders.get(id);
        if(res == null)
            throw new IllegalArgumentException(ERROR+id);
        return res;
    }
    public ArrayList<UnitBuilder> getAllUnitBuilders(){
        ArrayList<UnitBuilder> res = new ArrayList<>();
        for(UnitBuilder ub : unitBuilders.values())
            res.add(ub);
        return res;
    }
    
    public MoverBuilder getMoverBuilder(String id){
        MoverBuilder res = moverBuilders.get(id);
        if(res == null)
            throw new IllegalArgumentException(ERROR+id);
        return res;
    }
    public WeaponBuilder getWeaponBuilder(String id){
        WeaponBuilder res = weaponBuilders.get(id);
        if(res == null)
            throw new IllegalArgumentException(ERROR+id);
        return res;
    }
    public TurretBuilder getTurretBuilder(String id){
        TurretBuilder res = turretBuilders.get(id);
        if(res == null)
            throw new IllegalArgumentException(ERROR+id);
        return res;
    }
    public EffectBuilder getEffectBuilder(String id){
        EffectBuilder res = effectBuilders.get(id);
        if(res == null)
            throw new IllegalArgumentException(ERROR+id);
        return res;
    }
    public ProjectileBuilder getProjectileBuilder(String id){
        ProjectileBuilder res = projectileBuilders.get(id);
        if(res == null)
            throw new IllegalArgumentException(ERROR+id);
        return res;
    }
    public ActorBuilder getActorBuilder(String id){
        ActorBuilder res = actorBuilders.get(id);
        if(res == null)
            throw new IllegalArgumentException(ERROR+id);
        return res;
    }
    
    
    
    public MapStyleBuilder getMapStyleBuilder(String id){
        MapStyleBuilder res = mapStyleBuilders.get(id);
        if(res == null)
            throw new IllegalArgumentException(ERROR+id);
        return res;
    }
    public CliffShapeBuilder getCliffShapeBuilder(String id){
        CliffShapeBuilder res = cliffShapeBuilders.get(id);
        if(res == null)
            throw new IllegalArgumentException(ERROR+id);
        return res;
    }
    public TrinketBuilder getTrinketBuilder(String id){
        TrinketBuilder res = trinketBuilders.get(id);
        if(res == null)
            throw new IllegalArgumentException(ERROR+id);
        return res;
    }
    
    public ArrayList<TrinketBuilder> getAllTrinketBuilders(){
        ArrayList<TrinketBuilder> res = new ArrayList<>();
        for(TrinketBuilder b : trinketBuilders.values())
            res.add(b);
        return res;
    }

    public NaturalFaceBuilder getNaturalFaceBuilder(String id){
        NaturalFaceBuilder res = naturalFaceBuilders.get(id);
        if(res == null)
            throw new IllegalArgumentException(ERROR+id);
        return res;
    }
    public ManmadeFaceBuilder getManmadeFaceBuilder(String id){
        ManmadeFaceBuilder res = manmadeFaceBuilders.get(id);
        if(res == null)
            throw new IllegalArgumentException(ERROR+id);
        return res;
    }
    
    
}
