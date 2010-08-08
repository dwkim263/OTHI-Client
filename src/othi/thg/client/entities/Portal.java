package othi.thg.client.entities;


/**
 * Treasure sprite
 * @author Dong Won Kim
 */
public class Portal extends THGObject {
    
	private boolean oneWay = false;
	
    public Portal(int id, String name, float x, float y, boolean oneWay) {
    	myId = id;
    	this.name = name;
    	posX = x;
    	posY = y;
    	this.oneWay = oneWay;
    }

	public boolean isOneWay() {
		return oneWay;
	}
}
