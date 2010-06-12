package othi.thg.client;

import othi.thg.client.GameDefault.Terrain;

/**
 * managing a game map
 * @author Dong Won Kim
 */

public class THGTerrain {

	//map layers
	private static final int COLLISION_LAYER = 6;
	private static final int OBJECT_LAYER = 5;

	private TiledMap thgMap;    
	private Collision collisionManger;
	private int width = Integer.MIN_VALUE;
	private int height = Integer.MIN_VALUE;
	
	private static THGTerrain  myTHOTerrain = null;

	public static THGTerrain get() {

		if (myTHOTerrain == null) {
			myTHOTerrain = new THGTerrain();
		}
		return myTHOTerrain;
	}

	private THGTerrain() {

	}

	public TiledMap getThgMap() {
		return thgMap;
	}

	public void setThgMap(TiledMap map) {
		this.thgMap =  map;
		this.width = thgMap.width;
		this.height = thgMap.height;
	}

	public Collision getCollisionManger() {
		return collisionManger;
	}

	public void setCollisionManger(Collision collisionManger) {
		this.collisionManger = collisionManger;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	// Player: x000, Treasure: x001, Npc: x002, Monster: x003, Food: x004, Portal: x005 
	public boolean isPortalAt(float x, float y) {
		int tileId = getTileId(x, y);
		return (tileId % 1000 == 5);
	}	

	public void setTileId(float x, float y, int tileId) {
		thgMap.setTileId((int) x, (int) y, OBJECT_LAYER, tileId);
	}	

	public int getTileId(float x, float y) {
		return thgMap.getTileId((int) x, (int) y, OBJECT_LAYER);
	}

	public boolean collides(float x, float y) {
		return collisionManger.collides((int) x, (int) y);
	}

	public void setCollide(float x, float y){
		collisionManger.setCollide( (int) x, (int) y);
	}
	
	// Player: x000, Treasure: x001, Npc: x002, Monster: x003, Food: x004, Portal: x005 
	public boolean isTreasureAt(float x, float y) {
		int tileId = getTileId(x, y);
		return (tileId % 1000 == 1);
	}

	public Terrain getTerrain(float x, float y) {
		int tileId = getTileId(x, y);
		return Terrain.values()[tileId % 1000];
	}

	public void render(int x,int y,int sx,int sy,int width,int height,int l) {
		thgMap.render(x, y, sx, sy, width, height, l, false);
	}
	
}
