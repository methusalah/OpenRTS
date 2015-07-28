package model.battlefield.map;

import geometry.geom2d.Point2D;
import geometry.geom3d.Point3D;
import geometry.structure.grid3D.Grid3D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.battlefield.abstractComps.FieldComp;
import model.battlefield.map.atlas.Atlas;
import model.battlefield.map.cliff.Ramp;
import model.battlefield.map.parcelling.Parcelling;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains everything to set up a terrain and explore it. Map is mainly : - a tile based grid with relief and cliffs - a texture atlas to paint on the ground -
 * a list of trinkets Also contains methods and fields dedicated to serialization/deserialization.
 */
public final class Map extends Grid3D<Tile> {

	private MapStyle style;
	private String mapStyleID;

	private List<Ramp> ramps = new ArrayList<>();
	private List<TrinketMemento> initialTrinkets = new ArrayList<>();
	private java.util.Map<Long, Trinket> trinkets = new HashMap<>();
	
	private Parcelling parcelling;

	private Atlas atlas, cover;

	/**
	 * for deserialization purpose only
	 */
	public Map(){
		super();
	}
	
	public Map(MapStyle style) {
		super(style.width, style.height);
		this.style = style;
		for(int i = 0; i < xSize*ySize; i++)
			set(i, new Tile(this, i));
		mapStyleID = style.id;
		parcelling = new Parcelling(this);
	}

	public boolean isBlocked(int x, int y) {
		return (get(x, y)).isBlocked();
	}

	public boolean isWalkable(Point2D p) {
		return  !isInBounds(p) || get(p).isBlocked();
	}
	
	public void saveTrinkets(){
		initialTrinkets.clear();
		for(Trinket t : trinkets.values())
			initialTrinkets.add(new TrinketMemento(t));
	}
	
	public void addTrinket(Trinket t){
		trinkets.put(t.getId(), t);
	}
	public void removeTrinket(Trinket t){
		trinkets.remove(t.getId());
	}
	
	public Trinket getTrinket(long id){
		return trinkets.get(id);
	}
	
	/**
	 * Fore serialization purpose only
	 * @return
	 */
	@JsonIgnore
	@Override
	public List<Tile> getAll(){
		return super.getAll();
	}
	
	public List<Tile> getTiles(){
		return getAll();
	}
	
	/**
	 * Fore serialization purpose only
	 * @return
	 */
	public void setTiles(List<Tile> tiles){
		setAll(tiles);
	}
	
	public int getWidth(){
		return xSize;
	}
	
	public int getHeight(){
		return ySize;
	}
	
	
	/**
	 * Fore serialization purpose only
	 * @return
	 */
	public void setWidth(int val){
		xSize = val;
	}
	
	/**
	 * Fore serialization purpose only
	 * @return
	 */
	public void setHeight(int val){
		ySize = val;
	}

	@JsonIgnore
	public MapStyle getStyle() {
		return style;
	}

	@JsonIgnore
	public void setStyle(MapStyle style) {
		this.style = style;
	}

	public String getMapStyleID() {
		return mapStyleID;
	}

	public void setMapStyleID(String mapStyleID) {
		this.mapStyleID = mapStyleID;
	}

	public List<TrinketMemento> getInitialTrinkets() {
		return initialTrinkets;
	}

	@JsonProperty
	public void setInitialTrinkets(List<TrinketMemento> initialTrinkets) {
		this.initialTrinkets = initialTrinkets;
	}

	@JsonIgnore
	public List<Trinket> getTrinkets() {
		return new ArrayList<Trinket>(trinkets.values());
	}

	public Atlas getAtlas() {
		return atlas;
	}

	public void setAtlas(Atlas atlas) {
		this.atlas = atlas;
	}

	public Atlas getCover() {
		return cover;
	}

	public void setCover(Atlas cover) {
		this.cover = cover;
	}

	public List<Ramp> getRamps() {
		return ramps;
	}
	
	public void setRamps(List<Ramp> ramps) {
		this.ramps = ramps;
	}

	@JsonIgnore
	public Parcelling getParcelling() {
		return parcelling;
	}

	public void setParcelling(Parcelling parcelling) {
		this.parcelling = parcelling;
	}
	
	@JsonIgnore
	public <T extends FieldComp> List<T> getInCircle(Class<T> clazz, Point2D coord, double distance){
		List<T> res = new ArrayList<>();
		for(Tile t : getInCircle(coord, distance))
			for(T fieldComp : t.getData(clazz))
				if(fieldComp.getCoord().getDistance(coord) < distance)
					res.add(fieldComp);
		return res;
	}
}
