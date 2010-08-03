package othi.thg.client.entities.sprites;


import org.newdawn.slick.Image;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Log;

import othi.thg.client.THGClientDefault;

/**
 * Treasure sprite
 * @author Dong Won Kim
 */
public class TreasureSprite extends THGSprite {
    private static final long TREASUREOPENNS = 500 * 1000 * 1000 * 1000;    
    private static final int WIDTH = THGClientDefault.TILEWIDTH; 
    private static final int HEIGHT = THGClientDefault.TILEHEIGHT;         
    private SpriteSheet[] treasureSpriteSheets = new SpriteSheet[1];
    
    /** Topic ID = Quest ID */
    private int topicID;
    private boolean opened = false;
    private boolean taken = false;
    private long openTime;
    
    public TreasureSprite(int id, int topicID, int x, int y) {
        treasureSpriteSheets[0] = getAnimationSprite("chest");  
        setSpriteSheets(treasureSpriteSheets);            
        setID(id);
        this.topicID = topicID;
        setXY(x,y);
        setSound("sounds/creaky-door-1.wav");
    }
    
    @Override
    public SpriteSheet getAnimationSprite(String imgRef) {
        Image spriteImage = null;
        try {
            spriteImage = new Image("sprites/" + imgRef + ".png");
        } catch (SlickException e) {
              Log.error("get Sprite Image : ", e);
        }	
        return new SpriteSheet(spriteImage, WIDTH, HEIGHT);                
    }
    
    public boolean isOpened() {
        return opened;
    }

    public void open() {
        getSound().play();
        opened = true;   
        resetOpenTime();    
    }        
        
    public void close() {
        opened = false;           
    }  
    
    public void resetOpenTime() {
        openTime = TREASUREOPENNS + System.nanoTime();
    }        	

    public boolean isDone(){
        return System.nanoTime()> openTime;
    }    
    
    @Override
    public void draw(Graphics g) {
            int x = (int)(THGClientDefault.TILEWIDTH * getPosX() - getWidth()/4) ;
            int y = (int)(THGClientDefault.TILEHEIGHT * getPosY() - 4*getHeight()/10);                        
      /*      x = x  - getWidth() + GameScreen.TILEHEIGHT ;     // - centroids[getCurrentSheet()][0];
            y = y  - getHeight() + (GameScreen.TILEHEIGHT /2) ;     // - centroids[getCurrentSheet()][1];                

        */
             Image img = null;
             if (isOpened()) {
                 img = getCurrentImage(0,1);
             } else {
                 img = getCurrentImage(0,0);                 
             }              
             img.draw(x, y);       
    }

    public int getTopicID() {
        return topicID;
    }

    public void setTopicID(int topicID) {
        this.topicID = topicID;
    }

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }
    
}
