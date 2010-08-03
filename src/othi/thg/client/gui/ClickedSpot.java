package othi.thg.client.gui;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;

import othi.thg.client.THGClientDefault;

/**
 * managing clicked spot
 * @author Dong Won Kim
 */
public class ClickedSpot extends AbstractCursor {
    private static final long NANOMILLIS = 1000 * 1000;
    private static final long CLICKEDSPOTNS = NANOMILLIS * 1000;
    
    private Sound sound;
    
    private long endTime;
    
    public ClickedSpot(Image img) {
            this.img = img;
            width = img.getWidth();
            height = img.getHeight();
            setSound();
    }

    public Sound getSound() {
        return sound;
    }

    private void setSound() {
        try {
            sound = new Sound("sounds/click-spot.wav");
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    //set clicked position for drawing the clicked spot image    
    public void setClick(float x, float y){
        sound.play();
        setPosX(x);
        setPosY(y);
        resetEndTime();        
    }
        
    public void resetEndTime() {
        endTime = CLICKEDSPOTNS + System.nanoTime();
    }        	

    public boolean isDone(){
        return System.nanoTime()> endTime;
    }
    
    @Override
    public void draw(){                    
        float x = THGClientDefault.TILEWIDTH * getPosX();
        float y = THGClientDefault.TILEHEIGHT * getPosY();                     
        x = x - (getWidth()/2);
        y = y - (getHeight()/2);
        img.draw(x, y);           
    }    
}
