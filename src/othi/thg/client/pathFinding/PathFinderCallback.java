/**
 * A simple interface for testing path finding. This allows you
 * to watch the nodes being searched by the finder
 * 
 * @author Kevin Glass
 */
package othi.thg.client.pathFinding;

public interface PathFinderCallback {
	/**
	 * Notification that a particular node has been reached
	 * 
	 * @param x The x coordinate of the node reached
	 * @param y The y coordinate of the node reached
	 */
	public void fireNode(int x,int y);
}
