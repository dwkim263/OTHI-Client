package othi.thg.client.entities.sprites;


import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Log;

import othi.thg.client.THGClientDefault;
import othi.thg.client.entities.sprites.THGSprite.SpriteState;
import othi.thg.common.Commands.Direction;

/**
 * Shot sprite
 * @author Dong Won Kim
 */
public class ShotSprite extends THGSprite {
	private static SpriteSheet[] ballSheets = new SpriteSheet[1];

	private static int[][] shotCentroids = new int[][] { { 3, 5 } };
        
	private float deltaX;
	private float deltaY;        
	
        private SpriteState state = SpriteState.REST; 
        
	private long animationRootTime;

	public ShotSprite(float startPosX, float startposY, float endPosX, float endposY) {
                ballSheets[0] = getAnimationSprite("cannonball_sheet");
                setSpriteSheets(ballSheets);
                centroids = shotCentroids;                    
		setXY(startPosX, startposY);
 //               setSize(5,5);
		deltaX = endPosX-startPosX;
		deltaY = endposY-startposY;
		setFacing(Direction.NORTH); // only one sheet for all 4 images
		animationRootTime = System.nanoTime();
		state = THGSprite.SpriteState.ANIMATING;
		// TODO Auto-generated constructor stub
	}                

        public SpriteSheet getAnimationSprite(String imgRef) {
            Image spriteImage = null;
            try {
                spriteImage = new Image("gui/" + imgRef + ".png");                              
            } catch (SlickException e) {
                  Log.error("get Sprite Image : ", e);
            }	
            return new SpriteSheet(spriteImage, 5, 5 );    
        }
    
	public void draw() {
		switch (state) {
		case ANIMATING: 
//			int currentFc = getCurrentFrameCount();
			long elapsedTime = System.nanoTime() - animationRootTime;
			if (elapsedTime>ANIMPERIODNS){
				state=SpriteState.REST;
				return;
			}
//			int currImgNum = (int) ((System.nanoTime() - animationRootTime) / (ANIMPERIODNS / currentFc));
			Image img = getCurrentImage();
			int x = (int) (THGClientDefault.TILEWIDTH * getPosX());
			int y = (int) (THGClientDefault.TILEHEIGHT * getPosY());
			x += (THGClientDefault.TILEWIDTH / 2) - shotCentroids[getCurrentSheet()][0];
			y += (THGClientDefault.TILEHEIGHT / 2) - shotCentroids[getCurrentSheet()][1];
			// move for bullet flight
			x += (int)((deltaX*getWidth())*elapsedTime/ANIMPERIODNS);
			y += (int)((deltaY*getHeight())*elapsedTime/ANIMPERIODNS);
			img.draw(x, y);
			break;
		}

	}
	
	public boolean isDone(){
		return state == SpriteState.REST;
	}

}
