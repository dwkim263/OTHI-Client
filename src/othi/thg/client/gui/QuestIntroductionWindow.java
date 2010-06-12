package othi.thg.client.gui;

import org.newdawn.slick.AngelCodeFont;
import mdes.slick.sui.*;
/**
 * Suggest window
 * @author Dong Won Kim
 */
public class QuestIntroductionWindow extends ScrollCloseWindow {
    private SuiButton yesBtn = null;        
    
    public QuestIntroductionWindow(AngelCodeFont font, int x, int y) {
        super(font, x, y, ScrollWindow.getSCROLLWINDOW_WIDTH(),
                          ScrollWindow.getSCROLLWINDOW_HEIGHT()/3);
        yesBtn = new SuiButton("YES");      
        yesBtn.pack();
    } 

    public QuestIntroductionWindow(AngelCodeFont font) {
        super(font, ScrollWindow.getSCROLLWINDOW_WIDTH(),
                    ScrollWindow.getSCROLLWINDOW_HEIGHT()/3);
        yesBtn = new SuiButton("YES");    
        yesBtn.pack();            
    } 

    @Override
    public boolean hasFocus(){
        boolean yesButtonHasFocuse = yesBtn == null ? false : yesBtn.hasFocus();               
        return super.hasFocus() || yesButtonHasFocuse;
    }        

    @Override
    public void formDefaultComponents() {   
        int yesButtonX = 1 * getWidth()/3 - yesBtn.getWidth()/2;
        int yesBbuttonY = getHeight() - yesBtn.getHeight() - 5;              
        addButton(yesBtn, yesButtonX, yesBbuttonY);              

        SuiButton closeBtn = getCloseButton();
        closeBtn.setText("NO");
        int closeButtonx = 2 * getWidth()/3 - closeBtn.getWidth()/2;                       
        int textAreaHeight = getHeight() - getTextArea().getPadding() - 5 - closeBtn.getHeight() - 5;
        formDefaultComponents(closeButtonx, textAreaHeight);            
    }     

    public SuiButton getYesButton() {
        return yesBtn;
    }
}