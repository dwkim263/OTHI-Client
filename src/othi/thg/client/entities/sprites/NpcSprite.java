package othi.thg.client.entities.sprites;


import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Log;

import othi.thg.client.*;
import othi.thg.common.Commands.Direction;

/**
 * NPC sprite
 * @author Dong Won Kim
 */
public class NpcSprite extends THGSprite {
        private static final int WIDTH = (int) (THGClientDefault.TILEWIDTH * 1.5); 
        private static final int HEIGHT = THGClientDefault.TILEHEIGHT * 2;         
	private SpriteSheet[] npcSpriteSheets = new SpriteSheet[1];
	
	public NpcSprite(String imgRef, int x, int y, Direction direction) {
            npcSpriteSheets[0] = getAnimationSprite(imgRef);  
            setSpriteSheets(npcSpriteSheets);          
            setXY(x,y);
//                setSize(80,60);
            setFacing(direction);      
	}                
        
	public SpriteSheet getAnimationSprite(String imgRef) {
            Image spriteImage = null;
            try {
                spriteImage = new Image("sprites/npc/" + imgRef + ".png");
            } catch (SlickException e) {
                  Log.error("get Sprite Image : ", e);
            }	
            return new SpriteSheet(spriteImage, WIDTH, HEIGHT );                
	}             
}
