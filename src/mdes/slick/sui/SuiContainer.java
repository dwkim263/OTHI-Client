package mdes.slick.sui;

import org.newdawn.slick.*;
import java.util.*;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.*;
import org.newdawn.slick.geom.Rectangle;
import javax.swing.event.EventListenerList;
import mdes.slick.sui.event.*;

/**
 * SuiContainer is the base class for any Sui component. It doesn't
 * contain any information about fonts, colors, or images. It simply
 * holds local and absolute positions, sizes, listeners, children, etc.
 * <p>
 * The listener system has been changed in b.0.2 of SUI. The new listener
 * system mimics Swing, using a Listener interface (and an Adapter if needed)
 * where each method takes a subclass of SuiEvent.
 * <p>
 * For a SuiContainer to receive keyboard, controller and mouse wheel
 * events, it must be focusable and have the focus (ie: it must be
 * the focus owner).
 * <p>
 * Before a SuiContainer is created you should always call Sui.init(GameContainer).
 *
 * @author davedes
 * @since b.0.1
 */
public class SuiContainer {

  /** The internal listener for handling Slick input. */
  private final SuiContainerListener LISTENER = new SuiContainerListener();

  /** A list of children in this container. */
  private List<SuiContainer> children;

  /** The Slick Input used in this SuiContainer. */
  private Input input;

  /** Whether this component is focusable, initially true. */
  private boolean focusable = true;

  /** Whether this component is visible. */
  private boolean visible = true;

  /** Whether we've already added the input for this top-level container. */
  boolean inputAdded = false;

  /** Whether we are accepting input on the component. */
  protected boolean acceptingInput = false;

  /** A type-safe list which holds the different listeners. */
  protected EventListenerList listenerList = new EventListenerList();

  /** A fully transparent color used internally. */
  private final Color TRANSPARENT_COLOR = new Color(0f, 0f, 0f, 0f);

  protected Color border = Sui.getTheme().getSecondary3();

  /** The parent of this container, used internally. */
  SuiContainer parent = null;

  /** The x position of this container. */
  private float x = 0.0f;

  /** The y position of this container. */
  private float y = 0.0f;

  /** The width this container. */
  private int width = 0;

  /** The height this container. */
  private int height = 0;

  /**
   * Whether this component is ignoring events
   * and letting them pass through to underlying
   * components.
   */
  private boolean glassPane = false;

  //TODO: fix static default font implementation
  //(incase font is changed after construction)

  /** Constructs an empty SuiContainer. */
  public SuiContainer() {
    super();

    input = Sui.getInput();
    children = new ArrayList<SuiContainer>();
  }

  /**
   * Adds the specified listener to the list.
   *
   * @param s the listener to receive events
   */
  public synchronized void addKeyListener(SuiKeyListener s) {
    listenerList.add(SuiKeyListener.class, s);
  }

  /**
   * Removes the specified listener from the list.
   *
   * @param s the listener to remove
   */
  public synchronized void removeKeyListener(SuiKeyListener s) {
    listenerList.remove(SuiKeyListener.class, s);
  }

  /**
   * Adds the specified listener to the list.
   *
   * @param s the listener to receive events
   */
  public synchronized void addMouseListener(SuiMouseListener s) {
    listenerList.add(SuiMouseListener.class, s);
  }

  /**
   * Removes the specified listener from the list.
   *
   * @param s the listener to remove
   */
  public synchronized void removeMouseListener(SuiMouseListener s) {
    listenerList.remove(SuiMouseListener.class, s);
  }

  /**
   * Adds the specified listener to the list.
   *
   * @param s the listener to receive events
   */
  public synchronized void addMouseWheelListener(SuiMouseWheelListener s) {
    listenerList.add(SuiMouseWheelListener.class, s);
  }

  /**
   * Removes the specified listener from the list.
   *
   * @param s the listener to remove
   */
  public synchronized void removeMouseWheelListener(SuiMouseWheelListener s) {
    listenerList.remove(SuiMouseWheelListener.class, s);
  }

  /**
   * Adds the specified listener to the list.
   *
   * @param s the listener to receive events
   */
  public synchronized void addControllerListener(SuiControllerListener s) {
    listenerList.add(SuiControllerListener.class, s);
  }

  /**
   * Removes the specified listener from the list.
   *
   * @param s the listener to remove
   */
  public synchronized void removeControllerListener(SuiControllerListener s) {
    listenerList.remove(SuiControllerListener.class, s);
  }

  /**
   * Adds the specified listener to the list.
   *
   * @param s the listener to receive events
   */
  public synchronized void addActionListener(SuiActionListener s) {
    listenerList.add(SuiActionListener.class, s);
  }

  /**
   * Removes the specified listener from the list.
   *
   * @param s the listener to remove
   */
  public synchronized void removeActionListener(SuiActionListener s) {
    listenerList.remove(SuiActionListener.class, s);
  }

  /**
   * Sets the visibility for this component and all of its
   * children. Invisible components are not rendered.
   *
   * @param b <tt>true</tt> if it should be renderable
   */
  public void setVisible(boolean b) {
    this.visible = b;
    if (!b&&hasFocus())
      releaseFocus();
    for (int i=0; i<getChildCount(); i++) {
      SuiContainer c = getChild(i);
      c.setVisible(b);
    }
  }

  /**
   * Whether this component is currently visible.
   *
   * @returns <tt>true</tt> if this component is visible
   */
  public boolean isVisible() {
    return visible;
  }

  /**
   * Returns an array of this SuiContainer's children.
   *
   * @returns an array of SuiContainer children
   */
  public SuiContainer[] getChildren() {
    SuiContainer[] c = new SuiContainer[getChildCount()];
    return children.toArray(c);
  }

  /**
   * Adds a child to this SuiContainer.
   *
   * @param child the child container to add
   * @returns the child which was passed
   */
  public SuiContainer add(SuiContainer child) {
    child.parent = this;
    children.add(child);
    return child;
  }

  /**
   * Inserts a child to this SuiContainer at the specified index.
   *
   * @param child the child container to add
   * @index the index to insert it to
   */
  public void add(SuiContainer child, int index) {
    child.parent = this;
    children.add(index, child);
  }

  /**
   * Removes the child from this SuiContainer if it exists.
   *
   * @param child the child container to remove
   * @returns <tt>true</tt> if the child was removed
   */
  public boolean remove(SuiContainer child) {
    boolean contained = children.remove(child);
    if (contained) {
      child.parent = null;
    }
    return contained;
  }

  /**
   * Gets the child at the specified index.
   *
   * @param index the index of the child
   * @returns the child
   * @throws IndexOutOfBoundsException if the index is out of range
   */
  public SuiContainer getChild(int index) {
    return children.get(index);
  }

  /**
   * Gets the number of components in this container.
   *
   * @returns the number of components in this container
   */
  public int getChildCount() {
    return children.size();
  }

  /**
   * Whether this container contains the specified SuiContainer.
   *
   * @param c the container to check against
   * @returns <tt>true</tt> if this container contains the specified
   *			SuiContainer
   */
  public boolean containsChild(SuiContainer c) {
    return children.contains(c);
  }

  /**
   * Removes all children from this SuiContainer.
   */
  public void removeAll() {
    for (int i=0; i<getChildCount(); i++) {
      SuiContainer c = getChild(i);
      c.parent = null;
    }
    children.clear();
  }

  /**
   * Gets the parent of this container.
   *
   * @returns the parent container, or <tt>null</tt>
   *			if this is a top-level component
   */
  public SuiContainer getParent() {
    return parent;
  }

  /**
   * Called to render this component.
   *
   * @param container the GameContainer we are rendering to
   * @param g the Graphics context we are rendering with
   */
  protected void renderComponent(GameContainer container, Graphics g) {
  }

  /**
   * Called to update this component.
   *
   * @param container the GameContainer we are rendering to
   * @param delta the delta time (in ms)
   */
  protected void updateComponent(GameContainer container, int delta) {
  }

  /**
   * Called to recursively render all children of this container.
   *
   * @param container the GameContainer we are rendering to
   * @param g the Graphics context we are rendering with
   */
  protected void renderChildren(GameContainer container, Graphics g) {
    for (int i=0; i<getChildCount(); i++) {
      SuiContainer child = getChild(i);
      g.setClip((int)child.getAbsoluteX(), (int)child.getAbsoluteY()-1,
      			child.getWidth()+1, child.getHeight()+1);
      //shrinkClip(g, child);
      child.render(container, g, false);
    }
  }

  /**
   * Called after rendering the component and children to
   * render the border.
   *
   * @param container the GameContainer we are rendering to
   * @param g the Graphics context we are rendering with
   */
  protected void renderBorder(GameContainer container, Graphics g) {
  }

  /**
   * Called after updating the component and children to
   * update the border.
   *
   * @param container the GameContainer we are rendering to
   * @param delta the delta time (in ms)
   */
  protected void updateBorder(GameContainer container, int delta) {
  }

  /**
   * Called to recursively update all children of this container.
   *
   * @param container the GameContainer we are rendering to
   * @param delta the delta time (in ms)
   */
  protected void updateChildren(GameContainer container, int delta) {
    for (int i=0; i<getChildCount(); i++)
      getChild(i).update(container, delta);
  }

  /**
   * Renders this container and its children to the screen.
   * <p>
   * The order of rendering is as follows:<ol>
   * <li>Render this component through renderComponent</li>
   * <li>Render this component's children through renderChildren</li>
   * <li>Render this component's border through renderBorder</li>
   * </ol>
   * <p>
   * For custom rendering of the component, override renderComponent.<br>
   * For custom rendering of the children, override renderChildren.<br>
   * For cusotm rendering of the border, override renderBorder.<br>
   *
   * @param container The container displaying this component
   * @param g The graphics context used to render to the display
   */
  public final void render(GameContainer container, Graphics g) {
    render(container, g, true);
  }

  /**
   * Used internally for top-level components.
   *
   * @param container The container displaying this component
   * @param g The graphics context used to render to the display
   * @param topLevel <tt>true</tt> if this is a top-level component
   */
  protected void render(GameContainer container, Graphics g, boolean topLevel) {
    if (isVisible()) {
      Color c = g.getColor();
      Font f = g.getFont();
      acceptingInput = true;

      //if it's a top level component
      if (topLevel) {
      	if (!inputAdded) {
      		input.addPrimaryListener(LISTENER);
      		inputAdded = true;
      	}
        //shrinkClip(g, this);
      }
      //if it's no longer a top level component and we have to remove the input
      else if (inputAdded) {
      	input.removeListener(LISTENER);
      	inputAdded = false;
      }

      Rectangle r = g.getClip();
      if (r==null) {
      	r = new Rectangle(getAbsoluteX(), getAbsoluteY()-1,
      			getWidth()+1, getHeight()+1);
      	g.setClip(r);
      }

      renderComponent(container, g);
      renderChildren(container, g);

      g.setClip(r);

      renderBorder(container, g);
      g.setColor(c);
      g.setFont(f);
    }

    if (topLevel) {
    	//TODO: shrink clipping
      g.clearClip();
    }
  }

  /**
   * Updates this container and its children to the screen.
   * <p>
   * The order of updating is as follows:<ol>
   * <li>Update this component through updateComponent</li>
   * <li>Update this component's children through updateChildren</li>
   * <li>Update this component's border through updateBorder</li>
   * </ol>
   * <p>
   * For custom updating of the component, override renderComponent.<br>
   * For custom updating of the children, override renderChildren.<br>
   * For cusotm updating of the border, override renderBorder.<br>
   *
   * @param container The container displaying this component
   * @param delta The delta to update by
   */
  public void update(GameContainer container, int delta) {
    //TODO: should I update only while visible?
    //TODO: should update affect acceptingInput?
    //acceptingInput = true;
    updateComponent(container, delta);
    updateChildren(container, delta);
    updateBorder(container, delta);
  }

  /** Work in Progress algorithm. */
  void shrinkClip(Graphics g, SuiContainer child) {
    Rectangle clip = g.getClip();

    if (clip==null) {
      g.setClip((int)child.getAbsoluteX(), (int)child.getAbsoluteY()-1, child.getWidth()+1, child.getHeight()+1);
      return;
    }

    //if child location is negative
      //no change
    //if child location is positive
      //increase location by child's absolute x/y
      //UNLESS it is out of the clip bounds (in which case we use the bounds)

      //and some other stuff
    float dx = Math.max(0, Math.min(Math.max(0,child.getAbsoluteX()), clip.getWidth()));
    float dy = Math.max(0, Math.min(Math.max(0,child.getAbsoluteY()), clip.getHeight()));

    clip.setX(clip.getX() + dx);
    clip.setX(clip.getY() + dy);
//    clip.x += dx;
//    clip.y += dy;

    //shrink
    clip.setWidth(Math.max(0, (child.getWidth() - dx)));
    clip.setHeight(Math.max(0, (child.getHeight() - dy)));

    g.setClip((int)clip.getX(), (int)clip.getY()-1, (int)clip.getWidth()+1, (int)clip.getHeight()+1);
  }

  /**
   * Centers this container relative to the specified
   * container. If the passed container is <tt>null</tt>,
   * this container is centered based on the GameContainer.
   *
   * @param c the container to center with
   */
  public void setLocationRelativeTo(SuiContainer c) {
    int pw = (c!=null)
                ? c.getWidth()
                : Sui.getContainer().getWidth();
    int ph = (c!=null)
                ? c.getHeight()
                : Sui.getContainer().getHeight();


    float cx = (pw/2.0f - getWidth()/2.0f);
    float cy = (ph/2.0f - getHeight()/2.0f);
    setLocation((int)cx, (int)cy);
  }

  /**
   * Sets the x and y positions of this SuiContainer, relative
   * to its parent. If no parent exists, the location is absolute
   * to the GameContainer.
   *
   * @param x the x position of this component
   * @param y the y position of this component
   * @see mdes.slick.sui.SuiContainer#setBounds(int, int, int, int)
   */
  public void setLocation(float x, float y) {
    setX(x);
    setY(y);
  }

  /**
   * Sets the x position of this SuiContainer, relative
   * to its parent.
   *
   * @param x the x position of this component
   * @see mdes.slick.sui.SuiContainer#setLocation()
   */
  public void setX(float x) {
    this.x = x;
  }

  /**
   * Sets the y position of this SuiContainer, relative
   * to its parent.
   *
   * @param y the y position of this component
   * @see mdes.slick.sui.SuiContainer#setLocation()
   */
  public void setY(float y) {
    this.y = y;
  }

  /**
   * Gets the x position of this SuiContainer, relative
   * to its parent.
   *
   * @returns the x position of this component
   * @see mdes.slick.sui.SuiContainer#setLocation()
   */
  public float getX() {
    return x;
  }

  /**
   * Gets the y position of this SuiContainer, relative
   * to its parent.
   *
   * @returns the y position of this component
   * @see mdes.slick.sui.SuiContainer#setLocation()
   */
  public float getY() {
    return y;
  }

  /**
   * Translates the location of this container.
   *
   * @param x the x amount to translate by
   * @param y the y amount to translate by
   */
  public void translate(float x, float y) {
    float dx = getX()+ x;
    float dy = getY()+ y;
    setLocation(dx, dy);
  }

  /**
   * Gets the absolute x position of this
   * component. This is <i>not</i> relative to the
   * parent's position.
   *
   * @returns the x position in the GameContainer
   */
  public float getAbsoluteX() {
    return (parent==null) ? x : x+parent.getAbsoluteX();
  }

  /**
   * Gets the absolute y position of this
   * component. This is <i>not</i> relative to the
   * parent's position.
   *
   * @returns the y position in the GameContainer
   */
  public float getAbsoluteY() {
    return (parent==null) ? y : y+parent.getAbsoluteY();
  }

  /**
   * Sets the bounds of this SuiContainer.
   * <p>
   * The x and y positions are relative to this
   * component's parent. However, if no parent exists,
   * the x and y positions are equivalent to the
   * absolute x and y positions.
   *
   * @param x the x position of this component
   * @param y the y position of this component
   * @param width the width of this component
   * @param height the height of this component
   */
  public void setBounds(float x, float y, int width, int height) {
    setLocation(x, y);
    setSize(width, height);
  }

  /**
   * Sets the size of this SuiContainer.
   *
   * @param width the width of this component
   * @param height the height of this component
   */
  public void setSize(int width, int height) {
    setWidth(width);
    setHeight(height);
  }

  /**
   * Sets the height of this SuiContainer.
   *
   * @param height the height of this component
   */
  public void setHeight(int height) {
    this.height = height;
  }

  /**
   * Sets the width of this SuiContainer.
   *
   * @param width the width of this component
   */
  public void setWidth(int width) {
    this.width = width;
  }

  /**
   * Gets the width of this SuiContainer.
   *
   * @returns the width of this component
   */
  public int getWidth() {
    return width;
  }

  /**
   * Gets the height of this SuiContainer.
   *
   * @returns the height of this component
   */
  public int getHeight() {
    return height;
  }

  /**
   * Whether this container is focusable and has the input focus.
   *
   * @returns <tt>true</tt> if this focusable container has the input focus
   */
  public boolean hasFocus() {
  	return (focusable && Sui.getFocusOwner()== this);
  }

  /**
   * Sets the focus ability of this component. Focusable components can
   * receive key, controller or mouse wheel events if they have the focus.
   * Non-focusable components will never receive key, controller or mouse
   * wheel events.
   *
   * @param b <tt>true</tt> if this component should receive key, controller
   *				or mouse wheel events when it has the focus
   */
  public void setFocusable(boolean b) {
  	focusable = b;
  }

  /**
   * Whether the component is focusable.
   *
   * @returns whether this component can receive focus
   */
  public boolean isFocusable() {
  	return focusable;
  }

  //TODO: skip input for overlapping buttons
  	//EG: click button on a content pane
  	//	  but content pane receives event

  /**
   * Grabs the focus for this container. If this
   * container is not focusable no change will be made.
   * <p>
   * If the top-level parent of this component is an
   * instance of SuiWindow, it will be activated.
   */
  public void grabFocus() {
  	if (!focusable)
  		return;

  	Sui.setFocusOwner(this);

  	//finds the top level container
  	SuiContainer top = this;
    while (top.parent!=null) {
    	top = top.parent;
    }

    if (top instanceof SuiWindow) {
    	((SuiWindow)top).active = true;
    }
  }

  /**
   * Releases the focus on this container and all of its
   * SuiWindow parents. If this container is not focusable no change
   * will be made.
   * <p>
   * If the top-level parent of this component is an
   * instance of SuiWindow, it will be deactivated.
   */
  public void releaseFocus() {
  	if (!focusable)
  		return;

    if (Sui.getFocusOwner()==this)
    	Sui.setFocusOwner(null);

  	//finds the top level container
  	SuiContainer top = this;
    while (top.parent!=null) {
    	top = top.parent;
    }

    if (top instanceof SuiWindow) {
    	((SuiWindow)top).active = true;
    }

    //TODO: better support for window focus (isActive) ???
  }

  /**
   * Whether the absolute (relative to the GameContainer) x
   * and y positions are contained within this component.
   *
   * @param x the x position
   * @param y the y position
   * @returns <tt>true</tt> if this component contains the specified
   *			point
   */
  public boolean contains(int x, int y) {
  	return x>=getAbsoluteX() && y>=getAbsoluteY()
  				&& x<=getAbsoluteX()+getWidth() && y<=getAbsoluteY()+getHeight();
  }

  //TODO: package-protected setParent() which sets parent x/y
  //TODO: local space ints
  //TODO: check for isVisible()

  /**
   * Whether this SuiContainer is accepting input.
   *
   * @returns <tt>true</tt> if this is accepting input
   * @see org.newdawn.slick.InputListener#isAcceptingInput()
   */
  public boolean isAcceptingInput() {
    return acceptingInput;
  }

  /**
   * Indicate that this component has consumed the last reported event
   */
  protected void consumeEvent() {
    input.consumeEvent();
  }

  /**
   * Changes the Input for this Container.
   *
   * @param input the new input
   * @see org.newdawn.slick.InputListener#setInput(org.newdawn.slick.Input)
   */
  public void setInput(Input input) {
    if (this.input!=input) {
    	//TODO: fix
      this.input.removeListener(LISTENER);
      input.addPrimaryListener(LISTENER);
    }
    this.input = input;
  }

  /**
   * Gets the input we're using.
   *
   * @returns the input
   */
  public Input getInput() {
    return input;
  }

  /**
   * Glass pane components will ignore events and
   * the underlying components (ie: the parent panel) will
   * pick them up instead.
   * <p>
   * Still testing this.
   *
   * @param b whether this component should be glass pane
   */
  public void setGlassPane(boolean b) {
    glassPane = b;
  }

  /**
   * Whether this component is a glass pane component,
   * ignoring events and letting them pass through
   * to underlying components.
   *
   * @returns whether this is a glass pane component
   */
  public boolean isGlassPane() {
    return glassPane;
  }

  public Color getBorderColor() {
      return border;
  }

  public void setBorderColor(Color border) {
      this.border = border;
  }


  /**
   * Fills a rectangle using GL to draw a gradient. Sui uses gradients
   * for button and window rendering, this is mainly meant for internal use.
   *
   * @param start the color the gradient will start with (null for transparent)
   * @param end the color the gradient will end with (null for transparent)
   * @param x1 the x position of the rectangle
   * @param y1 the y position of the rectangle
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   */
  protected void fillGradientRect(Color start, Color end, float x1, float y1, int width, int height) {
    TextureImpl.bindNone();
    if (start!=null)
      start.bind();
    else
      TRANSPARENT_COLOR.bind();

    GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x1,y1);
        GL11.glVertex2f(x1+width,y1);
        if (end!=null)
          end.bind();
        else
          TRANSPARENT_COLOR.bind();
        GL11.glVertex2f(x1+width,y1+height);
        GL11.glVertex2f(x1,y1+height);
    GL11.glEnd();
  }

  /**
   * Returns a String representation of this container.
   *
   * @returns a String representation of this container
   */
  public String toString() {
    return super.toString() + " [Children: "+children.toString()+"]";
  }

  //TODO: EventListenerList support for earlier VM's without generics

  /**
   * Fires the specified key event to all key listeners
   * in this component.
   *
   * @param id the SuiKeyEvent id constant
   * @param key the Input constant
   * @param chr the character of the key
   * @see mdes.slick.sui.event.SuiKeyEvent
   */
  protected void fireKeyEvent(int id, int key, char chr) {
    SuiKeyEvent evt = null;

    final SuiKeyListener[] listeners = listenerList.getListeners(SuiKeyListener.class);
    for (int i=0; i<listeners.length; i++) {
      //lazily create it
      if (evt==null) {
        evt = new SuiKeyEvent(this, id, key, chr);
      }
      switch (id) {
        case SuiKeyEvent.KEY_PRESSED:
          listeners[i].keyPressed(evt);
          break;
        case SuiKeyEvent.KEY_RELEASED:
          listeners[i].keyReleased(evt);
          break;
      }
    }
  }

  /**
   * Fires the specified mouse event to all mouse listeners
   * in this component.
   *
   * @param id the SuiMouseEvent id constant
   * @param button the index of the button (starting at 0)
   * @param x the local new x position
   * @param y the local new y position
   * @param ox the local old x position
   * @param oy the local old y position
   * @param absx the absolute x position
   * @param absy the absolute y position
   * @see mdes.slick.sui.event.SuiMouseEvent
   */
  protected void fireMouseEvent(int id, int button, int x, int y, int ox, int oy, int absx, int absy) {
    SuiMouseEvent evt = null;

    final SuiMouseListener[] listeners = listenerList.getListeners(SuiMouseListener.class);
    for (int i=0; i<listeners.length; i++) {
      //lazily create it
      if (evt==null) {
        evt = new SuiMouseEvent(this, id, button, x, y, ox, oy, absx, absy);
      }
      switch (id) {
        case SuiMouseEvent.MOUSE_MOVED:
          listeners[i].mouseMoved(evt);
          break;
        case SuiMouseEvent.MOUSE_PRESSED:
          listeners[i].mousePressed(evt);
          break;
        case SuiMouseEvent.MOUSE_RELEASED:
          listeners[i].mouseReleased(evt);
          break;
        case SuiMouseEvent.MOUSE_DRAGGED:
          listeners[i].mouseDragged(evt);
          break;
        case SuiMouseEvent.MOUSE_ENTERED:
          listeners[i].mouseEntered(evt);
          break;
        case SuiMouseEvent.MOUSE_EXITED:
          listeners[i].mouseExited(evt);
          break;
      }
    }
  }

  /**
   * Fires the specified mouse event to all mouse listeners
   * in this component.
   *
   * @param id the SuiMouseEvent id constant
   * @param button the index of the button (starting at 0)
   * @param x the local x position
   * @param y the local y position
   * @param absx the absolute x position
   * @param absy the absolute y position
   * @see mdes.slick.sui.event.SuiMouseEvent
   */
  protected void fireMouseEvent(int id, int button, int x, int y, int absx, int absy) {
    fireMouseEvent(id, button, x, y, x, y, absx, absy);
  }

  /**
   * Fires the specified mouse event to all mouse listeners
   * in this component.
   *
   * @param id the SuiMouseEvent id constant
   * @param x the local x position
   * @param y the local y position
   * @param ox the local old x position
   * @param oy the local old y position
   * @param absx the absolute x position
   * @param absy the absolute y position
   * @see mdes.slick.sui.event.SuiMouseEvent
   */
  protected void fireMouseEvent(int id, int x, int y, int ox, int oy, int absx, int absy) {
    fireMouseEvent(id, SuiMouseEvent.NOBUTTON, x, y, ox, oy, absx, absy);
  }

  /**
   * Fires the specified mouse wheel event to all mouse wheel
   * listeners in this component.
   *
   * @param change the amount the mouse wheel has changed
   * @see mdes.slick.sui.event.SuiMouseWheelEvent
   */
  protected void fireMouseWheelEvent(int change) {
    SuiMouseWheelEvent evt = null;

    final SuiMouseWheelListener[] listeners = listenerList.getListeners(SuiMouseWheelListener.class);
    for (int i=0; i<listeners.length; i++) {
      //lazily create it
      if (evt==null) {
        evt = new SuiMouseWheelEvent(this, change);
      }
      listeners[i].mouseWheelMoved(evt);
    }
  }

  /**
   * Fires the specified controller event to all controller
   * listeners in this component.
   *
   * @param id the SuiControllerEvent id
   * @param controller the controller being used
   * @param button the button that was pressed/released
   * @see mdes.slick.sui.event.SuiControllerEvent
   */
  protected void fireControllerEvent(int id, int controller, int button) {
    SuiControllerEvent evt = null;

    final SuiControllerListener[] listeners = listenerList.getListeners(SuiControllerListener.class);
    for (int i=0; i<listeners.length; i++) {
      //lazily create it
      if (evt==null) {
        evt = new SuiControllerEvent(this, id, controller, button);
      }
      switch (id) {
        case SuiControllerEvent.BUTTON_PRESSED:
          listeners[i].controllerButtonPressed(evt);
          break;
        case SuiControllerEvent.BUTTON_RELEASED:
          listeners[i].controllerButtonReleased(evt);
          break;
      }
    }
  }

  /**
   * Handles the Slick Input and sends calls to this
   * SuiContainer's listeners.
   */
  private class SuiContainerListener implements InputListener {

    //the button for dragging
    private int dragb = -1;

    //whether the button is down
    private boolean bdown = false;

    //whether we are inside
    private boolean inside = false;


    public void mouseClicked(int button, int x, int y, int clickCount){

    }

    public void mousePressed(int button, int x, int y) {
   	  //input is made from parents
      if (!isVisible()) {
        return;
      }

      for (int i=0; i<getChildCount(); i++) {
  	  	if (!getChild(i).isGlassPane()&&getChild(i).contains(x, y)) {
  	  		getChild(i).LISTENER.mousePressed(button, x, y);
  	  		return;
  	  	}
  	  	else {
  	  		releaseFocus();
  	  	}
      }

      if (isGlassPane())
        return;

      //gets relative positions
      int nx = (int)(x-getAbsoluteX());
      int ny = (int)(y-getAbsoluteY());

      //call using local coordinates
      if (inBounds(nx, ny)) {
      	//TODO: ignore focus for dragging
      	grabFocus();

       	dragb = button;
        bdown = true;
        fireMouseEvent(SuiMouseEvent.MOUSE_PRESSED, button, nx, ny, x, y);
      }
      else {
      	releaseFocus();
      }
    }

    public void mouseReleased(int button, int x, int y) {
      int nx = (int)(x-getAbsoluteX());
      int ny = (int)(y-getAbsoluteY());

      for (int i=0; i<getChildCount(); i++) {
  	  	if (!getChild(i).isGlassPane()&&getChild(i).contains(x, y)) {
  	  		getChild(i).LISTENER.mouseReleased(button, x, y);
  	  		return;
  	  	}
      }

      //TODO: fix dragging w/ multiple buttons
      dragb = -1;

      boolean bounds = inBounds(nx, ny);
      if (bounds)
      	grabFocus();

      //call using local coordinates
      if (bounds||bdown) {
        if (!isGlassPane())
          fireMouseEvent(SuiMouseEvent.MOUSE_RELEASED, button, nx, ny, x, y);
      }
      else {
      	//TODO: touch up focus
        releaseFocus();
      }
      bdown=false;
    }

    public void mouseWheelMoved(int change) {
    }

    public void inputEnded() {
   	  for (int i=0; i<getChildCount(); i++) {
  	  	getChild(i).LISTENER.inputEnded();
      }
      SuiContainer.this.acceptingInput = false;
    }

    public void mouseMoved(int oldX, int oldY, int newX, int newY) {
      if (!isVisible()) {
        return;
      }

      for (int i=0; i<getChildCount(); i++) {
  	  	getChild(i).LISTENER.mouseMoved(oldX, oldY, newX, newY);
      }

      int ox = (int)(oldX-getAbsoluteX());
      int oy = (int)(oldY-getAbsoluteY());

      int nx = (int)(newX-getAbsoluteX());
      int ny = (int)(newY-getAbsoluteY());

      boolean bounds = inBounds(nx, ny);

      //if we are moving and not dragging
      if (dragb==-1) {
        //if we've moved in bounds
        if (bounds) {
          fireMouseEvent(SuiMouseEvent.MOUSE_MOVED, nx, ny, ox, oy, newX, newY);
          if (!inside) {
            fireMouseEvent(SuiMouseEvent.MOUSE_ENTERED, nx, ny, ox, oy, newX, newY);
          }
          inside = true;
        }
        //if we've moved out of bounds and we were originally inside
        else if (inside) {
          inside=false;
          fireMouseEvent(SuiMouseEvent.MOUSE_EXITED, nx, ny, ox, oy, newX, newY);
        }
      }
      //if we are dragging. this can be called even if the x/y coords
      //are out of the local container's coordinate bounds.
      else {
        //in bounds and we need to call enter
        if (bounds) {
          if (!inside) {
            //TODO: ok for focus, see button
            fireMouseEvent(SuiMouseEvent.MOUSE_ENTERED, nx, ny, ox, oy, newX, newY);
          }
          inside = true;
        }
        //out of bounds but we need to call exit
        else if (inside) {
          inside = false;
          fireMouseEvent(SuiMouseEvent.MOUSE_EXITED, nx, ny, ox, oy, newX, newY);
        }

        //call drag event
        if (!isGlassPane())
          fireMouseEvent(SuiMouseEvent.MOUSE_DRAGGED, dragb, nx, ny, ox, oy, newX, newY);
      } //end else for drag check
    }

    public void keyPressed(int key, char c) {
    }

    public void keyReleased(int key, char c) {
    }

    public void setInput(Input in) {
      for (int i=0; i<getChildCount(); i++) {
  	  	getChild(i).LISTENER.setInput(in);
      }

      SuiContainer.this.setInput(in);
    }

    public boolean isAcceptingInput() {
      return SuiContainer.this.isAcceptingInput();
    }

    /**
     * @see org.newdawn.slick.InputListener#controllerButtonPressed(int, int)
     */
    public void controllerButtonPressed(int controller, int button) {
    }

    /**
     * @see org.newdawn.slick.InputListener#controllerButtonReleased(int, int)
     */
    public void controllerButtonReleased(int controller, int button) {
      if (!isVisible())
        return;

      for (int i=0; i<getChildCount(); i++) {
  	  	getChild(i).LISTENER.controllerButtonReleased(controller, button);
      }

      fireControllerEvent(SuiControllerEvent.BUTTON_RELEASED, controller, button);
    }

    /**
     * @see org.newdawn.slick.InputListener#controllerDownPressed(int)
     */
    public void controllerDownPressed(int controller) {
      if (!isVisible())
        return;

      for (int i=0; i<getChildCount(); i++) {
  	  	getChild(i).LISTENER.controllerDownPressed(controller);
      }

      fireControllerEvent(SuiControllerEvent.BUTTON_PRESSED, controller,
                          SuiControllerEvent.DOWN_BUTTON);
    }

    /**
     * @see org.newdawn.slick.InputListener#controllerDownReleased(int)
     */
    public void controllerDownReleased(int controller) {
      if (!isVisible())
        return;

      for (int i=0; i<getChildCount(); i++) {
  	  	getChild(i).LISTENER.controllerDownReleased(controller);
      }

      fireControllerEvent(SuiControllerEvent.BUTTON_RELEASED, controller,
                          SuiControllerEvent.DOWN_BUTTON);
    }

    /**
     * @see org.newdawn.slick.InputListener#controllerLeftPressed(int)
     */
    public void controllerLeftPressed(int controller) {
      if (!isVisible())
        return;

      for (int i=0; i<getChildCount(); i++) {
  	  	getChild(i).LISTENER.controllerLeftPressed(controller);
      }

      fireControllerEvent(SuiControllerEvent.BUTTON_PRESSED, controller,
                          SuiControllerEvent.LEFT_BUTTON);
    }

    /**
     * @see org.newdawn.slick.InputListener#controllerLeftReleased(int)
     */
    public void controllerLeftReleased(int controller) {
      if (!isVisible())
        return;

      for (int i=0; i<getChildCount(); i++) {
  	  	getChild(i).LISTENER.controllerLeftReleased(controller);
      }

      fireControllerEvent(SuiControllerEvent.BUTTON_RELEASED, controller,
                          SuiControllerEvent.LEFT_BUTTON);
    }

    /**
     * @see org.newdawn.slick.InputListener#controllerRightPressed(int)
     */
    public void controllerRightPressed(int controller) {
      if (!isVisible())
        return;

      for (int i=0; i<getChildCount(); i++) {
  	  	getChild(i).LISTENER.controllerRightPressed(controller);
      }

      fireControllerEvent(SuiControllerEvent.BUTTON_PRESSED, controller,
                          SuiControllerEvent.RIGHT_BUTTON);
    }

    /**
     * @see org.newdawn.slick.InputListener#controllerRightReleased(int)
     */
    public void controllerRightReleased(int controller) {
      if (!isVisible())
        return;

      for (int i=0; i<getChildCount(); i++) {
  	  	getChild(i).LISTENER.controllerRightReleased(controller);
      }

      fireControllerEvent(SuiControllerEvent.BUTTON_RELEASED, controller,
                          SuiControllerEvent.RIGHT_BUTTON);
    }

    /**
     * @see org.newdawn.slick.InputListener#controllerUpPressed(int)
     */
    public void controllerUpPressed(int controller) {
      if (!isVisible())
        return;

      for (int i=0; i<getChildCount(); i++) {
  	  	getChild(i).LISTENER.controllerUpPressed(controller);
      }

      fireControllerEvent(SuiControllerEvent.BUTTON_PRESSED, controller,
                          SuiControllerEvent.UP_BUTTON);
    }

    /**
     * @see org.newdawn.slick.InputListener#controllerUpReleased(int)
     */
    public void controllerUpReleased(int controller) {
      if (!isVisible())
        return;

      for (int i=0; i<getChildCount(); i++) {
  	  	getChild(i).LISTENER.controllerUpReleased(controller);
      }

      fireControllerEvent(SuiControllerEvent.BUTTON_RELEASED, controller,
                          SuiControllerEvent.UP_BUTTON);
    }

    public boolean inBounds(int nx, int ny) {
      return nx>=0 && ny>=0 && nx<=getWidth() && ny<=getHeight();
    }

    public boolean inContainerBounds(int nx, int ny) {
      return nx>=0 && ny>=0 && nx<=Sui.getContainer().getWidth() && ny<=Sui.getContainer().getHeight();
    }
  }
}