package othi.thg.client.entities.sprites;


import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Font;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import othi.thg.client.THGClientDefault;
import othi.thg.client.OutfitStore;
import othi.thg.client.OutfitStore.Part;
import othi.thg.client.entities.sprites.THGSprite.SpriteState;
import othi.thg.common.Commands.Direction;

/**
 * player sprite
 * @author Dong Won Kim
 */
public class PlayerSprite extends THGSprite {
    private int exp = 0;
            
    private int maxExp = 0;
    
    private int money = 0;
    
    private int outfitCode;
    
    private SpriteSheet[] playerSheets = new SpriteSheet[Part.values().length];

    public  PlayerSprite(int outfitCode, int x, int y, Direction direction) {
            this.outfitCode = outfitCode;
            playerSheets = getAnimationSprite();  
            setSpriteSheets(playerSheets);                
            setXY(x,y);          
//                setSize((int) (GameScreen.TILEWIDTH * 1.5) ,(GameScreen.TILEHEIGHT * 2));
            setFacing(direction);
    }  

    public SpriteSheet getAnimationSprite(String imgRef) {
        return null;
    }
    
    /**
     * Get the full directional animation tile set for this entity.
     *
     * @return	A tile sprite containing all animation images.
     */
    private SpriteSheet[] getAnimationSprite() {
            OutfitStore store = OutfitStore.get();

            try {
                    return store.getOutfit(outfitCode);
            } catch (Exception e) {
                    Log.error("Cannot build outfit", e);
                    return store.getFailsafeOutfit();
            }
    }        

    public Image getCurrentImage(int pNumber) {
            return playerSheets[pNumber].getSprite(getCurrentSpriteIdx(), getCurrentSpriteSheetIdx());
    }

    public int getCurrentFrameCount(int pNumber) {
            return playerSheets[pNumber].getHorizontalCount();
    }

    @SuppressWarnings("fallthrough")
    @Override
    public void draw(Graphics g) {
            float x = THGClientDefault.TILEWIDTH * getPosX();
            float y = THGClientDefault.TILEHEIGHT * getPosY();
            x = x + THGClientDefault.TILEWIDTH - getWidth();  
            y = y + THGClientDefault.TILEHEIGHT - getHeight() ;               
                     
            switch (getSpriteState()) {
            case ANIMATING: // fall-through case on purpose
                int currentFc = getCurrentFrameCount(0);
                int currImgNum = (int) ((System.nanoTime() - getAnimationRootTime()) / (ANIMPERIODNS / currentFc));
                if (currImgNum >= currentFc) {
                        endAnim();
                } else {
                    setCurrentImage(currImgNum);
                }
                    // fall through to REST to render
            case REST:   
            case LEVELUP:
            case LEVELDOWN:                
                for (Part p : Part.values()) {
                    Image img = getCurrentImage(p.ordinal());                        
                    img.draw(x, y);                        
                }   
                // draw name
                Font font = g.getFont();

                Color oldColor = g.getColor();
                g.setColor(Color.white);

                String name = getName();                
                if (isMouseOver()) {
                    //draw level
                    g.drawString("Lv." + getGameLevel(), x, y - font.getHeight("Lv.")/2);
                }                
                g.drawString("["+name+"]", x, y+getHeight());

                g.setColor(oldColor);      
                
                break;
            }
    }
        
    protected int getOutfitCode() {
        return outfitCode;
    }
    
    protected void setOutfitCode(int code) {
        outfitCode = code;
    }

    public int getMoney() {
        return money;
    }

    public int getExp() {
        return exp;
    }

    public void setMaxExp(int maxExp) {
        this.maxExp = maxExp;
    }

    public int getMaxExp() {
        return maxExp;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }
    
    public void addMoney(int money) {
        this.money += money;
        
        if (this.money < 0) this.money = 0;        
    }

    public void addExp(int exp) {
        this.exp += exp;
        
        if (this.exp >= maxExp) setSpriteState(SpriteState.LEVELUP);
        else if (this.exp < 0) setSpriteState(SpriteState.LEVELDOWN);
    }        
}
