package othi.thg.client.gui;

import org.newdawn.slick.Image;
/**
 * abstract of cursors
 * @author Dong Won Kim
 */
public class AbstractCursor {	
    public Image img;
    public float posX;
    public float posY;
    public boolean enabled = false;
    
    public int width;

    public int height;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
    
    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }
    
    public void setPosX(float posX) {
        this.posX = posX;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }
	
    public void draw(){                                       
        float   x = getPosX()  - (getWidth()/2);
        float   y =  getPosY()  - (getHeight()/2);
        img.draw(x, y);           
    }
}
