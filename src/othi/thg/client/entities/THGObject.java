package othi.thg.client.entities;

/**
 * abstract of THGObject
 * @author Dong Won Kim
 */
public abstract class THGObject {
	
    protected float posX;

    protected float posY;

    protected int myId;

    protected String name;
        
    protected boolean mouseOver = false;

    public void setXY(float x, float y) {
            posX = x;
            posY = y;
    }

    public float getPosX() {
            return posX;
    }

    public float getPosY() {
            return posY;
    }

    public void setID(int id){
        myId = id;
    }

    public int getId(){
        return myId;
    }        

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }         
    
    public boolean isAt(float x, float y){
        float dx = Math.min(Math.abs(posX - x), Math.abs(posX + x));
        float dy = Math.min(Math.abs(posY - y), Math.abs(posY + y));   
         return (dx < 1 && dy < 1);                 
    }

    public boolean isMouseOver() {
        return mouseOver;
    }
    
    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }
}
