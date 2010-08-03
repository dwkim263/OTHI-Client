package othi.thg.client.entities.sprites;


import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Log;

import othi.thg.client.*;

/**
 * Food sprite
 * @author Dong Won Kim
 */
public class FoodSprite extends THGSprite {
    private static final int WIDTH = THGClientDefault.TILEWIDTH; 
    private static final int HEIGHT = THGClientDefault.TILEHEIGHT;    
    private static SpriteSheet[] foodSpriteSheets = new SpriteSheet[1];

    private int attractionPoint;

    public FoodSprite(int id, String name, float x, float y, int attractionPoint, String imgRef) {
    	this.myId = id;
    	this.name = name;
    	this.posX = x;
    	this.posY = y;
        this.attractionPoint = attractionPoint;
        foodSpriteSheets[0] = getAnimationSprite(imgRef);  
        setSpriteSheets(foodSpriteSheets);          
    }        

    public SpriteSheet getAnimationSprite(String imgRef) {
        Image spriteImage = null;
        try {
            spriteImage = new Image(imgRef);
        } catch (SlickException e) {
              Log.error("get Sprite Image : ", e);
        }	
        return new SpriteSheet(spriteImage, WIDTH, HEIGHT );                
    }

    public int getAttractionPoint() {
        return attractionPoint;
    }                
}
