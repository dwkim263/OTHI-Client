package othi.thg.client;

import java.util.logging.Logger;
import java.awt.geom.Rectangle2D;

/**
 * This class loads the map and allow you to determine if a player collides or
 * not with any of the non trespasable areas of the world
 * @author Marauroa
 * @author Dong Won Kim
 */
public class Collision {    

	private static final Logger logger = Logger.getLogger(Collision.class.getName());

	private boolean[][] blocked = null;

	private static final int COLLISION_LAYER = 6;

	private TiledMap map = null;

	private int width;

	private int height;

	private int tileWidth;

	private int tileHeight;                          

	public Collision(TiledMap map) {
		this.map = map;
		width=map.getWidth();
		height=map.getHeight();	
		tileWidth = map.getTileWidth();
		tileHeight = map.getTileHeight();
		blocked = new boolean[width][height];
		setCollitionData(COLLISION_LAYER);
	}

	public void clear() {
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				blocked[x][y] = false;				
			}
		}
	}


	public void setCollide(int x, int y) {
		if ((x < 0) || (x >= width) || (y < 0) || (y >= height)) {
			return;
		}

		blocked[x][y] = true;
	}

	public void removeCollide(int x, int y) {
		if ((x < 0) || (x >= width) || (y < 0) || (y >= height)) {
			return;
		}

		blocked[x][y] = false;
	}

	public void setCollide(Rectangle2D shape, boolean value) {
		double x = shape.getX();
		double y = shape.getY();
		double w = shape.getWidth();
		double h = shape.getHeight();

		if ((x < 0) || (x/* +w */>= width)) {
			return;
		}

		if ((y < 0) || (y/* +h */>= height)) {
			return;
		}

		int startx = (int) ((x >= 0) ? x : 0);
		int endx = (int) ((x + w < width) ? x + w : width);
		int starty = (int) ((y) >= 0 ? y : 0);
		int endy = (int) ((y + h) < height ? y + h : height);

		for (int k = starty; k < endy; k++) {
			for (int i = startx; i < endx; i++) {
				blocked[i][k] = value;
			}
		}
	}

	public void setCollitionData(int index) {
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++) {
				/* NOTE:
				 * Right now our collision detection system is binary, so
				 * something or is blocked or is not.
				 */
				blocked[x][y] = (map.getTileId(x, y,index)!=0);
			}
		}
	}

	/** Print the area around the (x,y) useful for debugging */
	public void printaround(int x, int y, int size) {
		for (int j = y - size; j < y + size; j++) {
			for (int i = x - size; i < x + size; i++) {
				if ((j >= 0) && (j < height) && (i >= 0) && (i < width)) {
					if ((j == y) && (i == x)) {
						logger.info("O");
					} else if (blocked[i][j] == false) {
						logger.info(".");
					} else {
						logger.info("X");
					}
				}
			}
			System.out.println();
		}
	}

	public boolean walkable(double x, double y) {
		return !blocked[(int) x][(int) y];
	}


	/**
	 * Returns true if the shape enters in any of the non trespasable areas of
	 * the map
	 */
	public boolean collides(Rectangle2D shape) {
		double x = shape.getX();
		double y = shape.getY();
		double w = shape.getWidth();
		double h = shape.getHeight();

		// expand the collision check for partial moves
		if ((x - Math.floor(x)) > 0.001) {
			w += 1.0;
		}
		if ((y - Math.floor(y)) > 0.001) {
			h += 1.0;
		}

		if ((x < 0) || (x/* +w */>= width)) {
			return true;
		}

		if ((y < 0) || (y/* +h */>= height)) {
			return true;
		}

		int startx = (int) ((x >= 0) ? x : 0);
		int endx = (int) ((x + w < width) ? x + w : width);
		int starty = (int) ((y >= 0) ? y : 0);
		int endy = (int) ((y + h < height) ? y + h : height);

		for (int k = starty; k < endy; k++) {
			for (int i = startx; i < endx; i++) {
				if (blocked[i][k]) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean collides(int x, int y) {
		if ((x < 0) || (x >= width)) {
			return true;
		}

		if ((y < 0) || (y >= height)) {
			return true;
		}
		return blocked[x][y];
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public TiledMap getMap() {
		if (map != null) return map;
		else return null;
	}

	public boolean[][] getBlocked() {
		if (blocked != null) return blocked;
		else return null;
	}

}
