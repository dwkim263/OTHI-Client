/**
 * A* path finder across a tile map
 * @author Kevin Glass
 * @author Dong Won Kim
 */
package othi.thg.client.pathFinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.Collections;

public strictfp class AStarPathFinder implements PathFinder {
	private static final Logger logger = Logger.getLogger(AStarPathFinder.class.getName());

	private boolean[][] blocked = null;

	private int mapWidth;

	private int mapHeight;
	/** The set describing the properties of the tiles on the map */
	// private TileSet set;
	/** The distance from the end point each point on the map is */
	private int[] distance;
	/** The maximum search depth before giving up */
	private int maxsearch;
	/** The starting x coordinate */
	private int sx;
	/** The starting y coordiante */
	private int sy;
	/** The callback notified as nodes are processed */
	private PathFinderCallback callback;
	/** The currently open nodes */
	private ArrayList<Step> open = new ArrayList<Step>();

	/**
	 * Create a new path finder based on the A* algorithm
	 * 
	 * @param map The map being searched
	 * @param set The set describing the tiles on the map
	 */
	public AStarPathFinder(int width, int height, boolean[][] blocked) {
		this(width, height, blocked, null);
	}

	/**
	 * Create a new path finder based on the A* algorithm
	 * 
	 * @param map The map being searched
	 * @param set The set describing the tiles on the map
	 * @param callback The callback notified as nodes are traversed
	 */
	public AStarPathFinder(int width, int height, boolean[][] blocked,PathFinderCallback callback) {
		this.callback = callback;
		mapWidth = width;
		mapHeight = height;      
		this.blocked = blocked;
		distance = new int[mapWidth* mapHeight];
	}

	/**
	 * @see org.newdawn.util.map.PathFinder#reset()
	 */
	public void reset() {
		open.clear();
		Arrays.fill(distance,0);
	}

	/**
	 * @see org.newdawn.util.map.PathFinder#getSearchData()
	 */
	public int[] getSearchData() {
		return distance;
	}

	/**
	 * @see org.newdawn.util.map.PathFinder#findPath(int, int, int, int, int)
	 */
	public synchronized Path findPath(int sx,int sy,int dx,int dy, int maxsearch) {
		this.sx = sx;
		this.sy = sy;

		this.maxsearch = maxsearch;

		Step step = new Step(null,dx,dy);		
		open.add(step);

		return processNodes();
	}

	/**
	 * Process the nodes of the search graph
	 * 
	 * @return The path found or null if no path could be found
	 */
	private Path processNodes() {
		Step step = findBest();

		while (!step.is(sx,sy)) {
			for (int x=-1;x<2;x++) {
				for (int y=-1;y<2;y++) {
					if ((x != 0) || (y != 0)) {
						int xp = step.x + x;
						int yp = step.y + y;

						if (!checkDiaganolBlock(step.x,step.y,x,y)) {
							if (validNode(xp,yp)) {
								/*                                       
                                      if (x == -1 && y == -1) {
                                          if (!checkDiaganolBlock(step.x,step.y,0,y) &&
                                              !checkDiaganolBlock(step.x,step.y,x,y) ) 
                                          {
                                              open.add(new Step(step,step.x,yp));
                                          } else if (!checkDiaganolBlock(step.x,step.y,x,0) &&
                                                     !checkDiaganolBlock(step.x,step.y,x,y))
                                          {
                                              open.add(new Step(step,xp,step.y));                                              
                                          }
                                      } else if (x == 1 && y == -1) {
                                          if (!checkDiaganolBlock(step.x,step.y,0,y) &&
                                              !checkDiaganolBlock(step.x,step.y,x,y) ) 
                                          {
                                              open.add(new Step(step,step.x,yp));                                              
                                          } else if (!checkDiaganolBlock(step.x,step.y,x,0) &&
                                                     !checkDiaganolBlock(step.x,step.y,x,y))
                                          {
                                              open.add(new Step(step,xp,step.y));                                                                                            
                                          }                                     
                                      } else if (x == -1 && y == +1) {
                                          if (!checkDiaganolBlock(step.x,step.y,0,y) &&
                                              !checkDiaganolBlock(step.x,step.y,x,y) ) 
                                          {
                                              open.add(new Step(step,step.x,yp));                                              
                                          } else if (!checkDiaganolBlock(step.x,step.y,x,0) &&
                                                     !checkDiaganolBlock(step.x,step.y,x,y))
                                          {
                                              open.add(new Step(step,xp,step.y));                                              
                                          }                                           
                                      } else if (x == 1 && y == 1) {
                                          if (!checkDiaganolBlock(step.x,step.y,0,y) &&
                                              !checkDiaganolBlock(step.x,step.y,x,y) ) 
                                          {
                                              open.add(new Step(step,step.x,yp));                                              
                                          } else if (!checkDiaganolBlock(step.x,step.y,x,0) &&
                                                     !checkDiaganolBlock(step.x,step.y,x,y))
                                          {
                                              open.add(new Step(step,xp,step.y));                                              
                                          }                                      
                                      }

                                      open.add(new Step(step,step.x,yp));    
								 */
								open.add(new Step(step,xp,yp));
							}
						}
					}
				}
			}

			step = findBest();
			if (step == null) {
				return null;
			}

			if (callback != null) {
				distance[step.x+(step.y*mapWidth)] = 1;
				callback.fireNode(step.x,step.y);
			}
		}

		Path path = new Path();

		while (step.parent != null) {
			path.appendStep(step.x,step.y);
			step = step.parent;
		}
		path.appendStep(step.x,step.y);
		printPath(path);
		return path;
	}

	/** Print the area around the (x,y) useful for debugging */
	private void printPath(Path path) {
		int height = mapHeight;
		int width = mapWidth;
		int x = path.getX(0);
		int y = path.getY(0);
		int length = path.getSize();
		int size = Math.max(Math.abs(x - path.getX(length-1)),
				Math.abs(y - path.getY(length-1)));
		for (int j = y - size; j < y + size; j++) {
			for (int i = x - size; i < x + size; i++) {
				if ((j >= 0) && (j < height) && (i >= 0) && (i < width)) {
					boolean found = false;
					int k = 0;
					for (; k < length; ++k ) {
						if (i == path.getX(k) && j==path.getY(k)) {
							found = true;
							break;
						}
					}
					if (found) {
						logger.info(Integer.toString(k));
						found = false;
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

	/**
	 * Check whether a particular move is actually valid
	 * 
	 * @param x The original x coordinate
	 * @param y The original y coordinate
	 * @param xo The x direction of movement
	 * @param yo The y direction of movement
	 * @return True if the move is valid
	 */
	private boolean checkDiaganolBlock(int x,int y,int xo,int yo) {
		if ((xo == 0) || (yo == 0)) {
			return false;
		}

		if (blocked[x+xo][y]) {
			return true;
		}
		if (blocked[x][y+yo]) {
			return true;
		}

		return false;
	}

	/**
	 * Check if a particular node on the graph is valid to check
	 * 
	 * @param x The x position of the node to check
	 * @param y The y position of the node to check
	 * @return True if the node is valid to evaluate
	 */
	private boolean validNode(int x,int y) {
		if (blocked[x][y]) {
			return false;
		}

		return distance[x+(y*mapWidth)] == 0;
	}

	/**
	 * Find the best open state currently available, i.e. the
	 * best direction to move in
	 * 
	 * @return The best step to take or null if there are no more
	 * steps left (no path)
	 */
	private Step findBest() {
		Collections.sort(open);

		if (open.size() == 0) {
			return null;
		}
		Step best = (Step) open.get(0);
		best.count++;
		if ((best.count > 9) || (best.depth > maxsearch)) {
			open.remove(best);
			return findBest();
		}

		return best;
	}

	/**
	 * Evaluate the heuristic for a particular node
	 * 
	 * @param x The x position of the node to evaluate
	 * @param y The y position of the node to evaluate
	 * @return The heuristic for the specified node
	 */
	private int evalH(int x,int y) {
		return Math.abs(sx-x) + Math.abs(sy-y);
	}

	/**
	 * A step on the search path
	 * 
	 * @author Kevin Glass
	 */
	private class Step implements Comparable {
		/** The x position of this step */
		public int x;
		/** The y position of this step */
		public int y;
		/** The heuristic for this step's node */
		public int h;
		/** The number of times this step has been used as a source */
		public int count;
		/** The depth of this search */
		public int depth;
		/** The parent step this step was spawned from */
		public Step parent;

		/**
		 * Create a new step
		 * 
		 * @param parent The step we came from
		 * @param x The x position we've moved to
		 * @param y The y position we've moved to
		 */
		public Step(Step parent,int x,int y) {
			this.x = x;
			this.y = y;
			this.parent = parent;
			if (parent != null) {
				depth = parent.depth+1;
			} else {
				depth = 0;
			}

			h = Math.abs(sx-x) + Math.abs(sy-y);

			if (depth <= maxsearch) {
				distance[x+(y*mapWidth)] = 2;
			}
		}

		/**
		 * Like an equals method, only for the data inside
		 * 
		 * @param x The x position to check against
		 * @param y The y position to check against
		 * @return True if this steps position is that specified
		 */
		public boolean is(int x,int y) {
			return (x == this.x) && (y == this.y);
		}

		/**
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Object o) {
			return h - ((Step) o).h;
		}
	}	

}
