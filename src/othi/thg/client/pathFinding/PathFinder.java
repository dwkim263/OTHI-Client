/**
 * A description of any implementation of path finding. The class will
 * be responsible for finding paths between two points. The map and tileset
 * being searched will be specified in the constructor to the particular
 * search object.
 * 
 * @author Kevin Glass
 */
package othi.thg.client.pathFinding;

public interface PathFinder {
	/**
	 * Reset the internal state of the path finder. This is expected to be
	 * called between path finds to clear out search data. 
	 * 
	 * Note, implementations should endevour to keep this an efficient method
	 */
	public abstract void reset();

	/**
	 * Retrieve an array of distances for different points on the map. This
	 * data is likely to be cleared after a call to <code>reset()</code>. 
	 * 
	 * This data is probably only useful for test/debug tools. 
	 * 
	 * @return An array of distances from the end point of the last search
	 * for different points on the map.
	 */
	public abstract int[] getSearchData();

	/**
	 * Find a path from a starting location to a destination point. 
	 * 
	 * @param sx The starting x coordinate
	 * @param sy The starting y coordinate
	 * @param dx The destination x coordinate
	 * @param dy The destination y coordinate
	 * @param maxsearch The maximum search depth that will be reached
	 * during the search. This is helpful for restricting the amount of 
	 * time a search may take
	 * @return The path found or null if no path could be determined
	 */
	public abstract Path findPath(int sx, int sy, int dx, int dy,
                                      int maxsearch);
}