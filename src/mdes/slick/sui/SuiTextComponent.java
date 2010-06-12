package mdes.slick.sui;

import org.newdawn.slick.*;
import mdes.slick.sui.event.*;
import  mdes.slick.sui.util.TimerAction;

public abstract class SuiTextComponent extends SuiLabel {
    
    
    /** The maximum number of characters allowed to be input */
    private int maxChars = 100000;
    
    private int caretPos = 0;
    
    protected boolean caretVisible = true;
    protected TimerAction caretTimer = new TimerAction(1000);
    
    protected Color caretColor = Color.black;
    
    protected Caret caret;
    
    /** Used for caret flicker. */
    private boolean drawCaret = true;
    
    protected int rowHeight;
    protected int colWidth;
    
    protected String actionCommand = "";
    
    protected TimerAction moveTimer = new TimerAction(200);
    
    public SuiTextComponent(String t) {
        super(t);
        addKeyListener(new TextInputListener());
        
        rowHeight = getFont().getHeight("W");
        colWidth = getFont().getWidth("m");
        
        caret = createCaret();
        
        moveTimer.setRepeats(true);
        caretTimer.setRepeats(true);
    }
    
    public Caret createCaret() {
        return new Caret() {
            @Override
            public void renderCaretAt(Graphics g, float x, float y) {
                Color oldColor = g.getColor();
                g.setColor(caretColor);
                g.fillRect(x, y, 2, rowHeight);
                g.setColor(oldColor);
            }
        };
    }
    
    /**
     * Sets the caret position. Caret position 0
     * is that start of the text, and
     * position <tt>getText().size()-1</tt> is the
     * end of the text.
     *
     * @param i the new caret position
     */
    public void setCaretPosition(int i) {
        if (caretPos != i)
            resetTimers();
        caretPos = i;
    }
    
    /**
     * Gets the current caret position for this text component.
     *
     * @returns the caret index, starting at 0
     */
    public int getCaretPosition() {
        return caretPos;
    }
    
    /**
     * Sets the action command to be passed to
     * <tt>SuiActionEvent</tt>s when this button
     * is clicked.
     * 
     * @param t the command
     */
    public void setActionCommand(String t) {
        this.actionCommand = t;
    }
  
    /**
     * Gets the action command.
     *
     * @returns the action command
     */
    public String getActionCommand() {
        return actionCommand;
    }
  
    /** 
     * Fires the specified action event to all action listeners
     * in this component.
     * 
     * @param command the action command for the event
     * @see mdes.slick.sui.event.SuiActionEvent
     */
    protected void fireActionPerformed(String command) {
        SuiActionEvent evt = null;
    
        final SuiActionListener[] listeners = listenerList.getListeners(SuiActionListener.class);
        for (int i=0; i<listeners.length; i++) {
            //lazily create it
            if (evt==null) {
                evt = new SuiActionEvent(this, command);
            }
            listeners[i].actionPerformed(evt);
        }
    }
    
    protected void renderCaret(GameContainer c, Graphics g, float x, float y) {
        if (caretVisible&&drawCaret) {
            caret.renderCaretAt(g, x, y);
        }
    }
    
    protected void updateCaret(GameContainer c, int delta) {
        if (caretTimer.update(delta)) {
            drawCaret = !drawCaret;
        }
    }
    
    @Override
    protected void updateComponent(GameContainer c, int delta) {
        super.updateComponent(c, delta);
        Input in = getInput();
        
        if (hasFocus()) {
            if (in.isKeyDown(Input.KEY_LEFT)) {
                if (moveTimer.update(delta)) {
                   if (caretPos > 0) {
                        caretPos--;
                        resetTimers();
                   } 
                }
            }
            else if (in.isKeyDown(Input.KEY_RIGHT)) {
                if (moveTimer.update(delta)) {
                    if (caretPos < getText().length()) {
                        caretPos++;
                        resetTimers();
                    }
                }
            }
            else if (in.isKeyDown(Input.KEY_BACK)) {
                if (moveTimer.update(delta)) {
                    String value = getText();
                    if ((caretPos > 0) && (value.length() > 0)) {
                        if (caretPos < value.length()) {
                            value = value.substring(0, caretPos-1) + value.substring(caretPos);
                        } else {
                            value = value.substring(0, caretPos-1);
                        }
                        caretPos--;
                        resetTimers();
                        setText(value);
                    }
                }
            }
        }
    }
    
    protected void resetTimers() {
        drawCaret = true;
        moveTimer.restart();
        caretTimer.restart();
    }
    
    @Override    
    public void setVisible(boolean b) {
        if (isVisible()!=b)
            resetTimers();
        super.setVisible(b);
    }
    
    //TODO: abstract append and remove
    
    protected class TextInputListener extends SuiKeyAdapter {
        
        @Override
        public void keyPressed(SuiKeyEvent e) {
            int key = e.getKeyChar();
            char c = e.getKeyChar();
            String value = getText();
                        
            if ( (c<127) && (c>31) && (getText().length() < maxChars) ) {
                if (caretPos < value.length()) {
                    value = value.substring(0, caretPos) + c + value.substring(caretPos);
                } else {
                    value = value.substring(0, caretPos) + c;
                }
                caretPos++;
                resetTimers();
                setText(value);
            }
            if (key == Input.KEY_RETURN) {
                releaseFocus();
                fireActionPerformed(getActionCommand());
            }
        }
    }
    
    protected interface Caret {
        public void renderCaretAt(Graphics g, float x, float y);
    }
}