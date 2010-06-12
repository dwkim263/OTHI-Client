package othi.thg.client.entities.sprites;


import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;
import org.newdawn.slick.Sound;
import org.newdawn.slick.Image;
import org.newdawn.slick.Font;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.SlickException;

import othi.thg.client.GameDefault;
import othi.thg.client.OutfitStore.Part;
import othi.thg.client.entities.THGObject;
import othi.thg.client.pathFinding.Path;
import othi.thg.common.Commands;
import othi.thg.common.Commands.Direction;

/**
 * abstract of sprites
 * @author Dong Won Kim
 */
public abstract class THGSprite extends THGObject {
	
    protected static final long ANIMPERIODNS = 200 * 1000 * 1000;

    int currentSpriteSheetIdx = 0;

    int[][] centroids;

    int currentSpriteIdx;

    SpriteSheet[] spriteSheets;

    private Sound sound;
    
    private int width;

    private int height;

    private Path path = null;

    float[] targetPos = new float[2];

    Direction facing;

    Part part;

    private long animationRootTime;

    private int gameLevel = 0;

    private int hp = 0;
    
    private int maxHp = 0;

    private int mp = 0;
    
    private int maxMp = 0;    
    
    private int power = 0;
        
    public static enum SpriteState {
         ANIMATING, DEAD,  REST, LEVELUP, LEVELDOWN
    }

    private SpriteState state = SpriteState.REST;

    public abstract SpriteSheet getAnimationSprite(String imgRef);

    public void setSound(String soundRef) {
        try {
            sound = new Sound(soundRef);
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public Sound getSound() {
        return sound;
    }
    
    public void setPath(Path path) {
        this.path = path;            
    }

    public Path getPath(){
        if (path != null) return path;
        else return null;
    }

    public void setSpriteSheets(SpriteSheet[] sheets)  {
        spriteSheets = sheets;

        height = 0;
        width = 0;

        for(SpriteSheet sheet : sheets) {                        
            height = Math.max(height, sheet.getSprite(0,0).getHeight());
            width = Math.max(width, sheet.getSprite(0,0).getWidth());
        }                 
    }        

    public void setSpriteState(SpriteState state)  {
        this.state = state;
    }

    public SpriteState getSpriteState() {
        return state;
    }

    public void setSheet(int sheetnum) {
            currentSpriteSheetIdx = sheetnum;
    }

    public void setCurrentImage(int imagenum) {
            currentSpriteIdx = imagenum;
    }

    public void incrImage() {
            currentSpriteIdx = (currentSpriteIdx + 1) % spriteSheets[currentSpriteSheetIdx].getHorizontalCount();
    }

    public Image getCurrentImage() {
            return spriteSheets[0].getSprite(getCurrentSpriteIdx(), getCurrentSpriteSheetIdx());
    }

    public Image getCurrentImage(int x, int y) {
            return spriteSheets[0].getSprite(x, y);
    }    
    
    public int getCurrentFrameCount() {
            return spriteSheets[0].getHorizontalCount();
    }

    public int getCurrentSpriteSheetIdx () {
        return currentSpriteSheetIdx;
    }

    public int getCurrentSpriteIdx() {
        return currentSpriteIdx;
    }
    // animation code
    public void setFacing(Direction direction) {
            facing = direction;
            setSheet(facing.ordinal());
    }

    public boolean isSameDirection(Direction inputDirection) {
       if (inputDirection == null) return false;

       Direction d = getCurrentFacing();
       if (inputDirection.equals(d)) 
            return true;
       else
            return false;       
    }        

    public int getCurrentSheet() {
            return currentSpriteSheetIdx;
    }

    public Direction getCurrentFacing() {
            return facing;
    }

    public long getAnimationRootTime() {
        return animationRootTime;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setSize(int width, int height){
         this.width = width;
         this.height = height;
    }       

    @SuppressWarnings("fallthrough")
    public void draw(Graphics g) {
            int x = (int)(GameDefault.TILEWIDTH * getPosX());
            int y = (int)(GameDefault.TILEHEIGHT * getPosY());                        
            x = x  - getWidth() + GameDefault.TILEHEIGHT ;     // - centroids[getCurrentSheet()][0];
            y = y  - getHeight() + (GameDefault.TILEHEIGHT /2) ;     // - centroids[getCurrentSheet()][1];                

            switch (getSpriteState()) {
            case ANIMATING: // fall-through case on purpose
                int currentFc = getCurrentFrameCount();
                int currImgNum = (int) ((System.nanoTime() - getAnimationRootTime()) / (ANIMPERIODNS / currentFc));
                if (currImgNum >= currentFc) {
                        endAnim();
                } else {
                    setCurrentImage(currImgNum);
                }
                    // fall through to REST to render
            case REST: 
                Image img = getCurrentImage();                   
                img.draw(x, y);   
                
                // draw name
                Font font = g.getFont();
                Color oldColor = g.getColor();
                g.setColor(Color.white);
                    
                if (isMouseOver()) {
                    //draw level
                    g.drawString("Lv." + gameLevel, x, y - font.getHeight("Lv.")/2);
                }
                
                g.drawString("["+ getName() +"]", x, y+height);

                g.setColor(oldColor);     
                break;
            }
    }

    public void endAnim() {
            if (state != SpriteState.ANIMATING){
                    return;
            }
            setCurrentImage(0);
            this.setXY(targetPos[0], targetPos[1]);
            
            state = SpriteState.REST; 
//		System.out.println("reached square endAnim " + posX + "," + posY);
    }

    public void walk(Direction direction) {
            if (state == SpriteState.ANIMATING) {
                    endAnim();
            }
//		System.out.println("Waking: " + direction);
            setFacing(direction);
            targetPos = Commands.step(getPosX(), getPosY(), direction);
            startAnimation();
    }

    public void walk(float tx, float ty, Direction direction) {
            if (state == SpriteState.ANIMATING) {
                    endAnim();
            }
            setFacing(direction);
            targetPos[0] = tx;
            targetPos[1] = ty;
            startAnimation();
    }        

    public void turn(Direction direction) {
            if (state == SpriteState.ANIMATING) {
                    endAnim();
            }
    //	System.out.println("Turning: " + direction);
            setFacing(direction);
    //	startAnimation();
    }

    private void startAnimation() {
            animationRootTime = System.nanoTime();
            state = SpriteState.ANIMATING;
    }

    public boolean isAnimating(){
            return state == SpriteState.ANIMATING;
    }

    public void setGameLevel (int level) {
        gameLevel = level;
    }
    
    public int getGameLevel (){
        return gameLevel;
    }
    
    public float getSpeed(){
    	int gameLevel = getGameLevel();
    	return (1.0f + gameLevel/10);
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getMaxMp() {
        return maxMp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public void setMaxMp(int maxMp) {
        this.maxMp = maxMp;
    }
    
    public void setHP (int hp) {
        this.hp = hp;
    }
    
    public int getHP (){
        return hp;
    }
            
    public void setMP (int mp) {
        this.mp = mp;
    }
    
    public void setPower(int power) {
        this.power = power;
    }
    
    public int getMP (){
        return mp;
    }
            
    public void addHP (int hp) {
        this.hp += hp;
        
        if (this.hp > maxHp) this.hp = maxHp;
        else if (this.hp < 0) setSpriteState(SpriteState.DEAD);   
    }
            
    public void addMP (int mp) {
        this.mp += mp;
        
        if (this.mp > maxMp) this.mp = maxMp;
        else if (this.mp < 0) this.mp = 0;
    }
}
