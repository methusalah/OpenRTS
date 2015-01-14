/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ressources.definitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.battlefield.Battlefield;
import model.builders.actors.ActorBuilder;
import model.builders.EffectBuilder;
import model.builders.MoverBuilder;
import model.builders.ProjectileBuilder;
import model.builders.TurretBuilder;
import model.builders.UnitBuilder;
import model.builders.WeaponBuilder;
import model.builders.CliffShapeBuilder;
import model.builders.ManmadeFaceBuilder;
import model.builders.MapStyleBuilder;
import model.builders.NaturalFaceBuilder;
import model.builders.TrinketBuilder;
import model.builders.Builder;
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


    private HashMap<String, HashMap> builders = new HashMap<>();

    public Battlefield battlefield;

    public BuilderLibrary(){
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
    
    public void buildLinks(){
        LogUtil.logger.info("buildings links...");
        for(HashMap<String, Builder> map : builders.values())
            for(Builder b : map.values())
                b.readFinalizedLibrary();
    }
    
    
    public void submit(Definition def){
        HashMap typed = builders.get(def.type);
        if(typed == null)
            throw new RuntimeException("Type '"+def.type+"' is unknown.");
        
        switch (def.type){
            case UNIT : typed.put(def.id, new UnitBuilder(def, this)); break;
            case MOVER : typed.put(def.id, new MoverBuilder(def, this)); break;
            case WEAPON : typed.put(def.id, new WeaponBuilder(def, this)); break;
            case TURRET : typed.put(def.id, new TurretBuilder(def, this)); break;
            case EFFECT : typed.put(def.id, new EffectBuilder(def, this)); break;
            case PROJECTILE : typed.put(def.id, new ProjectileBuilder(def, this)); break;
            case ACTOR :
                String actorType = def.getElement(ActorBuilder.TYPE) == null? "": def.getElement(ActorBuilder.TYPE).getVal();
                switch(actorType){
                    case ActorBuilder.TYPE_ANIMATION : typed.put(def.id, new AnimationActorBuilder(def, this)); break;
                    case ActorBuilder.TYPE_PARTICLE : typed.put(def.id, new ParticleActorBuilder(def, this)); break;
                    case ActorBuilder.TYPE_PHYSIC : typed.put(def.id, new PhysicActorBuilder(def, this)); break;
                    case ActorBuilder.TYPE_MODEL : typed.put(def.id, new ModelActorBuilder(def, this)); break;
                        default: typed.put(def.id, new ActorBuilder(def, this));
                }
                break;
            case MAP_STYLE : typed.put(def.id, new MapStyleBuilder(def, this)); break;
            case CLIFF_SHAPE : typed.put(def.id, new CliffShapeBuilder(def, this)); break;
            case TRINKET : typed.put(def.id, new TrinketBuilder(def, this)); break;
            case NATURAL_FACE : typed.put(def.id, new NaturalFaceBuilder(def, this)); break;
            case MANMADE_FACE : typed.put(def.id, new ManmadeFaceBuilder(def, this)); break;
        }
    }
    
    private Builder getBuilder(String type, String id){
        Builder res = ((HashMap<String, Builder>)builders.get(type)).get(id);
        if(res == null)
            throw new IllegalArgumentException(ERROR+type+"/"+id);
        return res;
    }
    public UnitBuilder getUnitBuilder(String id){
        return (UnitBuilder)getBuilder(UNIT, id);
    }
    public MoverBuilder getMoverBuilder(String id){
        return (MoverBuilder)getBuilder(MOVER, id);
    }
    public WeaponBuilder getWeaponBuilder(String id){
        return (WeaponBuilder)getBuilder(WEAPON, id);
    }
    public TurretBuilder getTurretBuilder(String id){
        return (TurretBuilder)getBuilder(TURRET, id);
    }
    public EffectBuilder getEffectBuilder(String id){
        return (EffectBuilder)getBuilder(EFFECT, id);
    }
    public ProjectileBuilder getProjectileBuilder(String id){
        return (ProjectileBuilder)getBuilder(PROJECTILE, id);
    }
    public ActorBuilder getActorBuilder(String id){
        return (ActorBuilder)getBuilder(ACTOR, id);
    }
    public MapStyleBuilder getMapStyleBuilder(String id){
        return (MapStyleBuilder)getBuilder(MAP_STYLE, id);
    }
    public CliffShapeBuilder getCliffShapeBuilder(String id){
        return (CliffShapeBuilder)getBuilder(CLIFF_SHAPE, id);
    }
    public TrinketBuilder getTrinketBuilder(String id){
        return (TrinketBuilder)getBuilder(TRINKET, id);
    }
    public NaturalFaceBuilder getNaturalFaceBuilder(String id){
        return (NaturalFaceBuilder)getBuilder(NATURAL_FACE, id);
    }
    public ManmadeFaceBuilder getManmadeFaceBuilder(String id){
        return (ManmadeFaceBuilder)getBuilder(MANMADE_FACE, id);
    }

    
    
    
    
    
    private List<Builder> getAllBuilders(String type){
        List<Builder> res = new ArrayList<>();
        res.addAll(((HashMap<String, Builder>)builders.get(type)).values());
        if(res.isEmpty())
            throw new IllegalArgumentException("type '"+type+"' dosen't seem to have any element.");
        return res;
    }
    /**
     * wildcard casting from : http://stackoverflow.com/questions/933447/how-do-you-cast-a-list-of-supertypes-to-a-list-of-subtypes
     * @return 
     */
    public List<UnitBuilder> getAllUnitBuilders(){
        return (List<UnitBuilder>)(List<?>)getAllBuilders(UNIT);
    }
    public List<TrinketBuilder> getAllTrinketBuilders(){
        return (List<TrinketBuilder>)(List<?>)getAllBuilders(TRINKET);
    }
    public List<TrinketBuilder> getAllEditableTrinketBuilders(){
        List<TrinketBuilder> all = (List<TrinketBuilder>)(List<?>)getAllBuilders(TRINKET);
        List<TrinketBuilder> res = new ArrayList<>();
        for(TrinketBuilder b : all)
            if(b.isEditable())
                res.add(b);
        return res;
    }
    
}
