package othi.thg.client;

/**
 * This is a timer for use in a mono threaded app like a Slick app
 * @author Jeff Kesselman
 *
 */
public class THGTimer {
	private long ringTime;
	
	public THGTimer(long deltaNS){
		ringTime = System.nanoTime()+deltaNS;
	}
	
	public boolean checkTimeExpired(){
		if (System.nanoTime()>=ringTime){
			return true;
		} else {
			return false;
		}
	}
}
