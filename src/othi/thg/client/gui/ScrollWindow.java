package othi.thg.client.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

import org.newdawn.slick.AngelCodeFont;
import mdes.slick.sui.*;
import mdes.slick.sui.event.*;

/**
 * Scroll window
 * @author Dong Won Kim
 */
public class ScrollWindow extends SuiWindow {
    private static final int SCROLLWINDOW_WIDTH = 400;
    private static final int SCROLLWINDOW_HEIGHT = 500;

    private SuiTextArea textArea = null;
    private SuiButton vsDown = null;
    private SuiButton vsUp = null; 
    private AngelCodeFont font = null;    

    public ScrollWindow (AngelCodeFont font) {
        super();
        this.font = font;
        setSize(SCROLLWINDOW_WIDTH, SCROLLWINDOW_HEIGHT);
        setResizable(false);
        textArea = new SuiTextArea();
    }

    public ScrollWindow (AngelCodeFont font, float x, float y, int width, int height) {
        super();
        this.font = font;
        setLocation(x,y);
        setSize(width, height);    
        setResizable(false);                            
        textArea = new SuiTextArea();          
    }
    
    public ScrollWindow (AngelCodeFont font, int width, int height) {
        super();
        this.font = font;        
        setSize(width, height);    
        setResizable(false);                            
        textArea = new SuiTextArea();          
    }

    public static int getSCROLLWINDOW_HEIGHT() {
        return SCROLLWINDOW_HEIGHT;
    }

    public static int getSCROLLWINDOW_WIDTH() {
        return SCROLLWINDOW_WIDTH;
    }
    
    @Override
    public boolean hasFocus(){
        boolean textAreaHasFocuse = textArea == null ? false : textArea.hasFocus();
        boolean vsDownHasFocuse = vsDown == null ? false : vsDown.hasFocus();
        boolean vsUpHasFocuse = vsUp == null ? false : vsUp.hasFocus();           
        return super.hasFocus() || titlePane.hasFocus() || textAreaHasFocuse || vsDownHasFocuse || vsUpHasFocuse;
    }

    public void addTextArea(int textAreaHeight) {
        try {           
            Image vsUpDefault = new Image("gui/vscroll_up_default.png");            
            vsUp = new SuiButton(vsUpDefault); 
            vsUp.pack();   
            vsUp.setLocationRelativeTo(this);

            //creates a new text area and sets it up            
            textArea.setSize(getWidth()-vsUp.getWidth(), textAreaHeight);
            textArea.setAdjustingHeight(false);
            textArea.wrap();
            textArea.setGlassPane(true);
            textArea.setLocationRelativeTo(this);
            textArea.setX(0);
            textArea.setY(textArea.getPadding());              
            textArea.setFont(font);

            vsUp.setLocation(textArea.getX() + textArea.getWidth(), textArea.getY() );  
            vsUp.setVisible(false);

            Image vsDownDefault = new Image("gui/vscroll_down_default.png");            
            vsDown = new SuiButton(vsDownDefault);   
            vsDown.pack();   
            vsDown.setLocationRelativeTo(this);
            vsDown.setLocation(textArea.getX() + textArea.getWidth(),
                               textArea.getY() + textArea.getHeight() - vsDown.getHeight());            
            vsDown.setVisible(false);
            vsDown.addActionListener(new SuiActionListener() {
              @Override  
              public void actionPerformed(SuiActionEvent e) {              
                    textArea.increaseCurrentHighestIndex();
              }
            });

            vsUp.addActionListener(new SuiActionListener() {
              @Override  
              public void actionPerformed(SuiActionEvent e) {              
                    textArea.decreaseCurrentHighestIndex();
              }
            });        

            add(textArea);
            add(vsUp);
            add(vsDown);                           

        } catch (SlickException e) {
            // TODO Auto-generated catch block
            Log.error(e);
        }      
    }        

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            if (isVisibleVS()){
                  vsUp.setVisible(true);
                  vsDown.setVisible(true);
            } else {
                  vsUp.setVisible(false);
                  vsDown.setVisible(false);                                  
            }      
        }
    }

    public SuiTextArea getTextArea() {
        return textArea;
    }

    public SuiButton getVsDown() {
        return vsDown;
    }

    public SuiButton getVsUp() {
        return vsUp;
    }

    public boolean isVisibleVS() {
       return (textArea.getMaxLines() < textArea.getLineCount()) ||
           (textArea.getMaxLines() < textArea.getCurrentHighestIndex());
    }            

    public AngelCodeFont getFont(){
        return font;
    }
    
    @Override
    public void setTitle(String title) {
        super.setTitle(title);    
        titlePane.setWidth(getWidth());                 
    }

    public void setTitle(Image img, String title) {
        titlePane.removeAll();                  
        //set padding to 0
        titlePane.setPadding(0);

        //set an image and set up size
        titlePane.setImage(img);
        titlePane.setHeight(img.getHeight());

        //add a text label for titlePane.
        SuiLabel l = new SuiLabel(title);
        Color color = new Color(Sui.getTheme().getPrimary4());
        l.setForegroundColor(color);
        l.pack();
        l.setLocationRelativeTo(titlePane);
        l.setX(10);
        l.setGlassPane(true);
        titlePane.add(l);
    }

    /**
     * Renders a custom border: doesn't draw a line between title bar
     * and content pane.
     */
    @Override
    protected void renderBorder(GameContainer c, Graphics g) {
        Color oldColor = g.getColor();
        g.setColor(getBorderColor());

        g.drawRect(getAbsoluteX(), getAbsoluteY()-titlePane.getHeight(),
                       getWidth(), getHeight()+titlePane.getHeight()-1);
        g.setColor(oldColor);
    }

    /** 
     * Overridden to ensure that there will be visible no gap between the
     * custom titlebar and the content pane.
     */
    @Override
    protected void renderContentPane(GameContainer c, Graphics g) {
        Color oldColor = g.getColor();
        g.setColor(getBackgroundColor());
        g.fillRect(getAbsoluteX(), getAbsoluteY()-titlePane.getHeight(),
                    getWidth(), getHeight()+titlePane.getHeight());
        g.setColor(oldColor);
    }        
}       
