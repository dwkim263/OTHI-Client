package othi.thg.client.gui;

import mdes.slick.sui.*;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Image;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.SlickException;

import othi.thg.client.GameDefault;

/**
 * control panel
 * @author Dong Won Kim
 */
public class ControlPanel extends SuiLabel {
    private static final int DIALOGUE_HIST_HEIGHT = 400;
    private static final int PANEL_HEIGHT = 24;
    
    private MessageLogWindow messageLogWindow = null;
    private SuiTextArea messageLogArea = null;
    private AngelCodeFont font = null;
    private SuiButton showDiaHisWin = null;
    private SuiButton helloBtn = null;
    private SuiButton helpBtn = null;
    private SuiButton byeBtn = null;
    private SuiButton clearBtn = null;
    private SuiButton stopBtn = null;
    private SuiButton questBtn = null;
    private SuiButton treasuresBtn = null;
    private SuiButton nextBtn = null;

    public ControlPanel(AngelCodeFont font, Image labelImg) {
        super(labelImg);
        this.font = font;
            
        this.pack();
        this.setLocation(0, GameDefault.SCREEN_HEIGHT - PANEL_HEIGHT);

        buildHelloButton();
        buildHelpButton();
        buildByeButton();
        buildClearButton();
        buildShowDiaHisWinButton();
        buildStopButton();        
        buildQuestButton();
        buildTreasureButton();    
        buildNextButton();
        buildDialHistWindow();
    }

    private void buildHelloButton() {
            //add a button
            helloBtn = new SuiButton("HELLO");
            helloBtn.pack(); //pack the button based on text and padding
            helloBtn.setLocationRelativeTo(this);
            helloBtn.setX(4); //local coordinates, relative to the parent
            this.add(helloBtn); //add to the this
    }

    private void buildHelpButton() {
            //add a button
            helpBtn = new SuiButton("HELP");
            helpBtn.pack(); //pack the button based on text and padding
            helpBtn.setLocationRelativeTo(this);
            helpBtn.setX(helloBtn.getX()+ helloBtn.getWidth() + 10); //local coordinates, relative to the parent
            this.add(helpBtn); //add to the this
    }    

    private void buildByeButton() {
            //add a button
            byeBtn = new SuiButton("BYE");
            byeBtn.pack(); //pack the button based on text and padding
            byeBtn.setLocationRelativeTo(this);
            byeBtn.setX(helpBtn.getX()+ helpBtn.getWidth() + 10); //local coordinates, relative to the parent
            this.add(byeBtn); //add to the this
    }

    private void buildClearButton() {
            //add a button
            clearBtn = new SuiButton("CLEAR");
            clearBtn.pack(); //pack the button based on text and padding
            clearBtn.setLocationRelativeTo(this);
            clearBtn.setX(byeBtn.getX()+ byeBtn.getWidth() + 10); //local coordinates, relative to the parent
            this.add(clearBtn); //add to the this
    }

    private void buildShowDiaHisWinButton() {
        try {
            Image button_default = new Image("gui/button_default.png");
            showDiaHisWin = new SuiButton(button_default);
            showDiaHisWin.pack();
            showDiaHisWin.setLocationRelativeTo(this);
            showDiaHisWin.setX(clearBtn.getX()+ clearBtn.getWidth() + 10);
            this.add(showDiaHisWin);
        } catch (SlickException e) {
            // TODO Auto-generated catch block
            Log.error(e);
        }

    }
    
    private void buildStopButton() {
            //add a button
            stopBtn = new SuiButton("STOP");
            stopBtn.pack(); //pack the button based on text and padding
            stopBtn.setLocationRelativeTo(this);
            stopBtn.setX(750); //local coordinates, relative to the parent
            this.add(stopBtn); //add to the this
    }
    
    private void buildQuestButton() {
            //add a request question
            questBtn = new SuiButton("QUESTS");
            questBtn.pack(); //pack the button based on text and padding
            questBtn.setLocationRelativeTo(this);    
            questBtn.setX(stopBtn.getX() - 10 - questBtn.getWidth() ); //local coordinates, relative to the parent
            this.add(questBtn); //add to the this   
    }      
    
    private void buildTreasureButton() {
            //add a request document
            treasuresBtn = new SuiButton("TREASURES");
            treasuresBtn.pack(); //pack the button based on text and padding
            treasuresBtn.setLocationRelativeTo(this);    
            treasuresBtn.setX(questBtn.getX() - 10 - treasuresBtn.getWidth() ); //local coordinates, relative to the parent    
            this.add(treasuresBtn); //add to the this                 
    }     

    private void buildNextButton() {
            //add a request document
            nextBtn = new SuiButton("NEXT");
            nextBtn.pack(); //pack the button based on text and padding
            nextBtn.setLocationRelativeTo(this);
            nextBtn.setX(treasuresBtn.getX() - 10 - nextBtn.getWidth() ); //local coordinates, relative to the parent
            this.add(nextBtn); //add to the this
    }

    private void buildDialHistWindow() {
        try {
            //create diaglogue history window
            Image diagHisWinImg = new Image("gui/diahistwin.png");
            messageLogWindow = new MessageLogWindow("Message Log", diagHisWinImg, font, 
                                           helloBtn.getX(), this.getY()- DIALOGUE_HIST_HEIGHT,
                                           (int) (showDiaHisWin.getX() + showDiaHisWin.getWidth() - helloBtn.getX()), DIALOGUE_HIST_HEIGHT);
            messageLogWindow.setDefalutMessageLogTheme();
            messageLogWindow.formDefaultComponents();
            messageLogArea = messageLogWindow.getTextArea();
            messageLogWindow.setVisible(false);    
        } catch (SlickException e) {
            // TODO Auto-generated catch block
            Log.error(e);
        }              
    }

    public SuiButton getByeBtn() {
        return byeBtn;
    }

    public SuiButton getHelloBtn() {
        return helloBtn;
    }

    public SuiButton getHelpBtn() {
        return helpBtn;
    }

    public SuiButton getClearBtn() {
        return clearBtn;
    }

    public static int getDIALOGUE_HIST_HEIGHT() {
        return DIALOGUE_HIST_HEIGHT;
    }

    public MessageLogWindow getMessageLogWindow() {
        return messageLogWindow;
    }

    public SuiTextArea getMessageLogArea() {
        return messageLogArea;
    }

    public SuiButton getQuestBtn() {
        return questBtn;
    }

    public SuiButton getShowDiaHisWin() {
        return showDiaHisWin;
    }

    public SuiButton getStopBtn() {
        return stopBtn;
    }

    public SuiButton getTreasuresBtn() {
        return treasuresBtn;
    }        
}
