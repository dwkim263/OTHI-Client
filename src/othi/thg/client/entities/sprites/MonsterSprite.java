package othi.thg.client.entities.sprites;


import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Log;

import othi.thg.client.*;
import othi.thg.common.Commands.Direction;

/**
 * Monster sprite
 * @author Dong Won Kim
 */
public class MonsterSprite extends THGSprite {
        private static final int WIDTH = THGClientDefault.TILEWIDTH; 
        private static final int HEIGHT = THGClientDefault.TILEHEIGHT;    
	private static SpriteSheet[] monsterSpriteSheets = new SpriteSheet[1];
	
	public MonsterSprite(String name, float x, float y, Direction direction, String imgRef) {          
            monsterSpriteSheets[0] = getAnimationSprite(imgRef);  
            setName(name);
            setSpriteSheets(monsterSpriteSheets);          
            setXY(x,y);
            setFacing(direction);
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
}
