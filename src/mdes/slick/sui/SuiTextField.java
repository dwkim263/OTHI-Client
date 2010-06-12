package mdes.slick.sui;

import org.newdawn.slick.*;
import mdes.slick.sui.event.*;

import org.lwjgl.Sys;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;

/**
 * A single text field supporting text entry
 *
 * @author kevin
 * @modifier Dong Won Kim July 05, 2009
 */
public class SuiTextField extends SuiLabel {
	/** The key repeat interval */
	private static final int INITIAL_KEY_REPEAT_INTERVAL = 400;
	/** The key repeat interval */
	private static final int KEY_REPEAT_INTERVAL = 50;

    private String initialText = null;


	/** The maximum number of characters allowed to be input */
	private int maxCharacter = 10000;

	/** The current cursor position */
	private int cursorPos;

	/** True if the cursor should be visible */
	private boolean visibleCursor = true;

	/** The last key pressed */
	private int lastKey = -1;

	/** The last character pressed */
	private char lastChar = 0;

	/** The time since last key repeat */
	private long repeatTimer;

	/** The text before the paste in */
	private String oldText;

	/** The cursor position before the paste */
	private int oldCursorPos;

	/** True if events should be consumed by the field */
	private boolean consume = true;

    /** The action command for this text field. */
    private String actionCommand = "";    

  /**
   * Creates a text field with the specified number of columns.
   * Column width will be equal to <tt>font.getHeight("W")</tt>,
   * using the font from {@link mdes.slick.sui.Sui#getDefaultFont()}.
   *
   * @param cols the number of columns to start off with
   */

    public SuiTextField(Font font, int x, int y, int width,  int height) {
        super(Sui.getTheme().getEditFont());

        this.setLocation(x, y);
        this.setSize(width, height);

        addKeyListener(new FieldListener());
        setText("");
        setFocusable(true);
    }

    public SuiTextField(Font font, int width,  int height) {
        super(Sui.getTheme().getEditFont());

        this.setSize(width, height);

        addKeyListener(new FieldListener());
        setText("");
        setFocusable(true);
    }

    public String getInitialText() {
        return initialText;
    }

    public void setInitialText(String initText) {
        this.initialText = initText;
        if (initText != null) setText(initText); 
    }

    /**
	 * Indicate if the input events should be consumed by this field
	 *
	 * @param consume True if events should be consumed by this field
	 */
	public void setConsumeEvents(boolean consume) {
		this.consume = consume;
	}

	/**
	 * Deactivate the key input handling for this field
	 */
	public void deactivate() {
		setFocus(false);
	}

	/**
	 * @see org.newdawn.slick.gui.AbstractComponent#render(org.newdawn.slick.gui.GUIContext,
	 *      org.newdawn.slick.Graphics)
	 */
    @Override
	public void renderComponent(GameContainer container, Graphics g) {
        if (isOpaque()) {
          Color oldColor = g.getColor();
          g.setColor(Sui.getTheme().getSecondary2());
          g.fillRect(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight());
          g.setColor(oldColor);
        }
        if (getImage()!=null) {
          g.drawImage(image, getAbsoluteX(), getAbsoluteY());
        }
        
		if (lastKey != -1) {
			if (this.getInput().isKeyDown(lastKey)) {
				if (repeatTimer < System.currentTimeMillis()) {
					repeatTimer = System.currentTimeMillis() + KEY_REPEAT_INTERVAL;
                    fireKeyEvent(SuiKeyEvent.KEY_PRESSED, lastKey, lastChar);
				}
			} else {
				lastKey = -1;
			}
		}
		Rectangle oldClip = g.getClip();
		g.setWorldClip(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight());

		// Someone could have set a color for me to blend...
		Color clr = g.getColor();

		if (getBackgroundColor() != null) {
			g.setColor(getBackgroundColor().multiply(clr));
			g.fillRect(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight());
		}
		g.setColor(getForegroundColor().multiply(clr));
		Font temp = g.getFont();

		int cpos = getFont().getWidth(text.substring(0, cursorPos));
		int tx = 0;
		if (cpos > getWidth()) {
			tx = getWidth() - cpos - getFont().getWidth("_");
		}

		g.translate(tx + 2, 0);
		g.setFont(getFont());
		g.drawString(text, getAbsoluteX() + 1, getAbsoluteY() + 1);

		if (hasFocus() && visibleCursor) {
            if (getInitialText() != null) {
                setInitialText(null);
                setText("");
            }
			g.drawString("_", getAbsoluteX() + 1 + cpos + 2, getAbsoluteY() + 1);
		}

		g.translate(-tx - 2, 0);

		if (getBorderColor() != null) {
			g.setColor(getBorderColor().multiply(clr));
			g.drawRect(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight());
		}
		g.setColor(clr);
		g.setFont(temp);
		g.clearWorldClip();
		g.setClip(oldClip);
	}

    @Override
    public String getText(){
        if (getInitialText() == null) {
            return super.getText();
        } else {
            return "";
        }
    }

	/**
	 * Set the value to be displayed in the text field
	 *
	 * @param value
	 *            The value to be displayed in the text field
	 */
    @Override
	public void setText(String value) {
		this.text = value;
		if (cursorPos > value.length()) {
			cursorPos = value.length();
		}
	}

	/**
	 * Set the position of the cursor
	 *
	 * @param pos
	 *            The new position of the cursor
	 */
	public void setCursorPos(int pos) {
		cursorPos = pos;
		if (cursorPos > text.length()) {
			cursorPos = text.length();
		}
	}

	/**
	 * Indicate whether the mouse cursor should be visible or not
	 *
	 * @param visibleCursor
	 *            True if the mouse cursor should be visible
	 */
	public void setCursorVisible(boolean visibleCursor) {
		this.visibleCursor = visibleCursor;
	}

	/**
	 * Set the length of the allowed input
	 *
	 * @param length
	 *            The length of the allowed input
	 */
	public void setMaxLength(int length) {
		maxCharacter = length;
		if (text.length() > maxCharacter) {
			text = text.substring(0, maxCharacter);
		}
	}

	/**
	 * Do the paste into the field, overrideable for custom behaviour
	 *
	 * @param text The text to be pasted in
	 */
	protected void doPaste(String text) {
		recordOldPosition();

		for (int i=0;i<text.length();i++) {
            fireKeyEvent(SuiKeyEvent.KEY_PRESSED, -1, text.charAt(i));            
		}
	}

	/**
	 * Record the old position and content
	 */
	protected void recordOldPosition() {
		oldText = getText();
		oldCursorPos = cursorPos;
	}

	/**
	 * Do the undo of the paste, overrideable for custom behaviour
	 *
	 * @param oldCursorPos before the paste
	 * @param oldText The text before the last paste
	 */
	protected void doUndo(int oldCursorPos, String oldText) {
		if (oldText != null) {
			setText(oldText);
			setCursorPos(oldCursorPos);
		}
	}


	/**
	 * @see org.newdawn.slick.gui.AbstractComponent#setFocus(boolean)
	 */
	public void setFocus(boolean focus) {
		lastKey = -1;

		super.setFocusable(focus);
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

  private class FieldListener extends SuiKeyAdapter {

	/**
	 * @see org.newdawn.slick.gui.AbstractComponent#keyPressed(int, char)
	 */
    @Override
	public void keyPressed(SuiKeyEvent e) {
        int key = e.getKeyCode();
        char c = e.getKeyChar();

        Input input = getInput();

		if (hasFocus()) {
			if (key != -1)
			{
				if ((key == Input.KEY_V) &&
				   ((Sui.getInput().isKeyDown(Input.KEY_LCONTROL)) || (Sui.getInput().isKeyDown(Input.KEY_RCONTROL)))) {
					String text = Sys.getClipboard();
					if (text != null) {
						doPaste(text);
					}
					return;
				}
				if ((key == Input.KEY_Z) &&
				   ((Sui.getInput().isKeyDown(Input.KEY_LCONTROL)) || (Sui.getInput().isKeyDown(Input.KEY_RCONTROL)))) {
					if (oldText != null) {
						doUndo(oldCursorPos, oldText);
					}
					return;
				}

				// alt and control keys don't come through here
				if (Sui.getInput().isKeyDown(Input.KEY_LCONTROL) || Sui.getInput().isKeyDown(Input.KEY_RCONTROL)) {
					return;
				}
				if (Sui.getInput().isKeyDown(Input.KEY_LALT) || Sui.getInput().isKeyDown(Input.KEY_RALT)) {
					return;
				}
			}

			if (lastKey != key) {
				lastKey = key;
				repeatTimer = System.currentTimeMillis() + INITIAL_KEY_REPEAT_INTERVAL;
			} else {
				repeatTimer = System.currentTimeMillis() + KEY_REPEAT_INTERVAL;
			}
			lastChar = c;

			if (key == Input.KEY_LEFT) {
				if (cursorPos > 0) {
					cursorPos--;
				}
				// Nobody more will be notified
				if (consume) {
					input.consumeEvent();
				}
			} else if (key == Input.KEY_RIGHT) {
				if (cursorPos < text.length()) {
					cursorPos++;
				}
				// Nobody more will be notified
				if (consume) {
					input.consumeEvent();
				}
			} else if (key == Input.KEY_BACK) {
				if ((cursorPos > 0) && (text.length() > 0)) {
					if (cursorPos < text.length()) {
						text = text.substring(0, cursorPos - 1)
								+ text.substring(cursorPos);
					} else {
						text = text.substring(0, cursorPos - 1);
					}
					cursorPos--;
				}
				// Nobody more will be notified
				if (consume) {
					Sui.getContainer().getInput().consumeEvent();
				}
			} else if (key == Input.KEY_DELETE) {
				if (text.length() > cursorPos) {
					text = text.substring(0,cursorPos) + text.substring(cursorPos+1);
				}
				// Nobody more will be notified
				if (consume) {
					Sui.getContainer().getInput().consumeEvent();
				}
			} else if ((c < 127) && (c > 31) && (text.length() < maxCharacter)) {
				if (cursorPos < text.length()) {
					text = text.substring(0, cursorPos) + c
							+ text.substring(cursorPos);
				} else {
					text = text.substring(0, cursorPos) + c;
				}
				cursorPos++;
				// Nobody more will be notified
				if (consume) {
					Sui.getContainer().getInput().consumeEvent();
				}
			} else if (key == Input.KEY_RETURN) {
            	releaseFocus();
                fireActionPerformed(getActionCommand());
				// Nobody more will be notified
				if (consume) {
					Sui.getContainer().getInput().consumeEvent();
				}
			}

		}
	}
  }
}
