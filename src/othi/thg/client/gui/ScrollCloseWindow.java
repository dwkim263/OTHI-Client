package othi.thg.client.gui;

import org.newdawn.slick.AngelCodeFont;

import othi.thg.client.GameDefault;
import mdes.slick.sui.*;
import mdes.slick.sui.event.*;

/**
 * Scroll close window
 * @author Dong Won Kim
 */
public class ScrollCloseWindow extends ScrollWindow {
    
    private SuiButton closeBtn = null; 
    
    private int treasureID = Integer.MIN_VALUE;
    private SuiActionListener closeListener = null; 

    public ScrollCloseWindow(AngelCodeFont font) {
        super(font);       
        closeBtn = new SuiButton("close");          
        closeBtn.pack();           
    }   
    
    public ScrollCloseWindow(AngelCodeFont font, int x, int y, int width, int height) {
        super(font, x, y, width, height);       
        closeBtn = new SuiButton("close");          
        closeBtn.pack();           
    }    

    public ScrollCloseWindow(AngelCodeFont font, int width, int height) {
        super(font, width, height);                   
        int x = GameDefault.SCREEN_WIDTH /2 - width /2 - 5;
     //   int y = GameDefault.SCREEN_HEIGHT /2 - height/2;
        int y = 100;
        setLocation(x, y);
        closeBtn = new SuiButton("close");          
        closeBtn.pack();
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        if (closeBtn != null) closeBtn.setY(getHeight() - closeBtn.getHeight() - 5);
        SuiTextArea textArea = getTextArea();
        if (textArea != null) textArea.setHeight(getHeight() - getTextArea().getPadding() - 5 - closeBtn.getHeight() - 5);
    }

    public SuiActionListener getCloseListener() {
        return closeListener;
    }

    public void setCloseListener(SuiActionListener closeListener) {
        this.closeListener = closeListener;            
    }

    @Override
    public boolean hasFocus(){
        boolean closeBtnHasFocuse = closeBtn == null ? false : closeBtn.hasFocus();            
        return super.hasFocus() || closeBtnHasFocuse;
    }

    public void addButton(SuiButton button, int x, int y) {
        button.setLocationRelativeTo(this);
        button.setY(y);
        button.setX(x); //local coordinates, relative to the parent    
        add(button);
    }

    public void formDefaultComponents() {    
        int closeButtonx = getWidth()/2 - closeBtn.getWidth()/2;
        int closeButtonY = getHeight() - closeBtn.getHeight() - 5;

        closeListener = new SuiActionListener() {
            @Override
            public void actionPerformed(SuiActionEvent e) {
                setVisible(false);
            }
        };

        closeBtn.addActionListener(closeListener);    

        addButton(closeBtn, closeButtonx, closeButtonY);            
        addTextArea(getHeight() - getTextArea().getPadding() - 5 - closeBtn.getHeight() - 5);                         
    }

    public void formDefaultComponents(int closeButtonx, int textAreaHeight) {
        int closeButtonY = getHeight() - closeBtn.getHeight() - 5;

        closeBtn.addActionListener(new SuiActionListener() {
            @Override
            public void actionPerformed(SuiActionEvent e) {
                setVisible(false);
            }
        }); 

        addButton(closeBtn, closeButtonx, closeButtonY);            
        addTextArea(textAreaHeight);                             
    }        

    @Override
    public SuiButton getCloseButton() {
        return closeBtn;
    }

/*
    public void performClose(){
           setVisible(false);
    }
*/
    public void setTreasureID(int id){
        this.treasureID = id;
    }

    public int getTreasureID(){
        return treasureID;
    }
}    

