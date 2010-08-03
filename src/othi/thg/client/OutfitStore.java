package othi.thg.client;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Log;


/**
 * Providing player sprite's outfit images
 * @author Marauroa
 * @author Dong Won Kim
 */
public class OutfitStore {

	private static OutfitStore  sharedInstance = null;
        
        private static final int WIDTH = (int) (THGClientDefault.TILEWIDTH * 1.5); 

        private static final int HEIGHT = THGClientDefault.TILEHEIGHT * 2; 
                
        public static enum Part {
            BASE, DRESS, HEAD, HAIR
        }

	protected SpriteSheet	store;

	public OutfitStore() {

	}

	public static OutfitStore get() {
            if (sharedInstance == null) {
                sharedInstance = new OutfitStore();
            }
	    return sharedInstance;
	}

        /**
	 * Build an outfit sprite.
	 *
	 * The outfit is described by an "outfit code".
	 * It is an 8-digit integer of the form RRHHDDBB where RR is the
	 * number of the hair graphics, HH for the head, DD for the dress,
	 * and BB for the base.
	 * 
	 * @param	code The outfit code.
	 *
	 * @return	An walking state tileset.
	 */
        
	public SpriteSheet[] getOutfit(int code) throws IllegalArgumentException {
		int	idx;

                SpriteSheet[] sprites = new SpriteSheet[4]; 
	
                try {
                         /*
                         * Base (body) layer
                         */
                        idx = code % 100;
                        code /= 100;

                        sprites[Part.BASE.ordinal()] = getBaseSprite(idx);

                        if(sprites[0] == null) {
                                throw new IllegalArgumentException(
                                        "No base image found for outfit: " + code);
                        }

                        /*
                         * Dress layer
                         */
                        idx = code % 100;
                        code /= 100;


                        sprites[Part.DRESS.ordinal()] = getDressSprite(idx);

                        /*
                         * Head layer
                         */
                        idx = code % 100;
                        code /= 100;

                      sprites[Part.HEAD.ordinal()] = getHeadSprite(idx);


                        /*
                         * Hair layer
                         */
                        idx = code % 100;

                        sprites[Part.HAIR.ordinal()] = getHairSprite(idx);
                        
                } catch (SlickException e) {
                      Log.error("OutfitStore : ", e);
                }	

		return sprites;
	}

	public SpriteSheet getBaseSprite(int index)  throws SlickException {                
		return new SpriteSheet(new Image( "sprites/outfit/player_base_" + index + ".png"),
                                            WIDTH, HEIGHT );
	}


	public SpriteSheet getDressSprite(int index) throws SlickException {
		if (index==0) return getEmptySprite();
		return  new SpriteSheet(new Image("sprites/outfit/dress_" + index + ".png"),
                                            WIDTH, HEIGHT );
	}


	private SpriteSheet getEmptySprite() throws SlickException {
                 return new SpriteSheet(new Image("sprites/outfit/sprite_empty" + ".png"),
                                            WIDTH, HEIGHT );
	}


	public SpriteSheet[] getFailsafeOutfit() {
		// TODO: Need a failsafe that depends on minimal resources
		return getOutfit(0);
	}

	public SpriteSheet getHairSprite(int index) throws SlickException {
		if (index==0) return  getEmptySprite();
		return new SpriteSheet(new Image("sprites/outfit/hair_" + index + ".png"),
                                            WIDTH, HEIGHT );                        
	}

	public SpriteSheet getHeadSprite(int index) throws SlickException {
		return new SpriteSheet(new Image("sprites/outfit/head_" + index + ".png"),
                                            WIDTH, HEIGHT );
	}
}
