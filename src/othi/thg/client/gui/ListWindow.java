package othi.thg.client.gui;


import mdes.slick.sui.*;
/**
 * List window
 * @author Dong Won Kim
 */
public class ListWindow extends SuiWindow {   
    private static final int LISTWINDOW_WIDTH = 410;
    private static final int LISTWINDOW_HEIGHT = 200;  
    
    public ListWindow (String title, int x, int y) {
        super(title);
        setLocation(x,y);
        setSize(LISTWINDOW_WIDTH, LISTWINDOW_HEIGHT);    
        setResizable(false);
        setVisible(false);
    }

    public ListWindow (String title) {
        super(title);
        setSize(LISTWINDOW_WIDTH, LISTWINDOW_HEIGHT);    
        setResizable(false);                 
        setLocationRelativeTo(null);
        setY(100);
        setVisible(false);
    }

    @Override
    public boolean hasFocus(){
        boolean focus = super.hasFocus() || 
                titlePane.hasFocus() || 
                getCloseButton().hasFocus();
        int i = 0;
        while (!focus && (i < getChildCount())){
            focus = getChild(i).hasFocus();
            if (focus) return focus;                
            ++i;
        }
        return focus; 
    }

    public static int getLISTWINDOW_HEIGHT() {
        return LISTWINDOW_HEIGHT;
    }

    public static int getLISTWINDOW_WIDTH() {
        return LISTWINDOW_WIDTH;
    }        
}        

