package mdes.slick.sui;

import mdes.slick.sui.event.SuiMouseEvent;
import mdes.slick.sui.event.SuiMouseListener;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.Color;

/**
 * @author Alexandre Vieira
 */
public class SuiScrollPane extends SuiContainer {

    /* The <code>SuiContainer</code> which contents is to be displayed.
Scrollbars add themselves automatically to the pane. */
    private SuiContainer view;
    /* The horizontal scrollbar... */
    private SuiScrollBar horizontalBar;
    /* The vertical scrollbar... */
    private SuiScrollBar verticalBar;

    /**
     * Creates a <code>SuiScrollPane</code> that displays the contents of
     * the specified <code>SuiContainer</code>. Both horizontal and vertical
     * scrollbars appear whenever the specified <code>SuiContainer</code>
     * contents are larger than the view.
     *
     * @param view The <code>SuiContainer</code> to display.
     */
    public SuiScrollPane(SuiContainer view) {
        super();
        this.view = view;
        add(view);
        view.setLocation(0, 0);
        horizontalBar = new SuiScrollBar(SuiScrollBar.HORIZONTAL);
        verticalBar = new SuiScrollBar(SuiScrollBar.VERTICAL);
    }

    /**
     * This method is overridden to correct the behavior of the scrollbars.
     *
     * @param visible The visibility of this component.
     */
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        horizontalBar.setVisible(false);
        verticalBar.setVisible(false);
    }

    /**
     * this <code>SuiScrollPane</code>.
     *
     * @param container The container holding the game.
     * @param g         The graphics context to render onto.
     */
    @Override
    protected void renderBorder(GameContainer container, Graphics g) {
        Color oldColor = g.getColor();
        g.setColor(getBorderColor());
        g.drawRect(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight()-1);
        g.setColor(oldColor);
    }

    /**
     * Updates this component. If an horizontal and/or vertical bar is needed
     * it's set visible, otherwise it's set invisible.
     *
     * @param container The container holding the game.
     * @param delta     The time elapsed since the last update.
     */
    protected void updateComponent(GameContainer container, int delta) {
        super.updateComponent(container, delta);
        if (isHorizontalBarNeeded()) {
            if (!horizontalBar.isVisible()) {
                horizontalBar.setVisible(true);
                horizontalBar.adjustHorizontally();
            }
        } else {
            horizontalBar.setVisible(false);
            verticalBar.adjustVertically();
        }
        if (isVerticalBarNeeded()) {
            if (!verticalBar.isVisible()) {
                verticalBar.setVisible(true);
                verticalBar.adjustVertically();
            }
        } else {
            verticalBar.setVisible(false);
            horizontalBar.adjustHorizontally();
        }
    }

    /**
     * Returns true if view's width is greater than this component's width.
     *
     * @return TRUE if an horizontal is needed.
     */
    private boolean isHorizontalBarNeeded() {
        return view.getWidth() > getWidth();
    }

    /**
     * Returns true if view's height is greater than this component's height.
     *
     * @return TRUE if an vertical is needed.
     */
    private boolean isVerticalBarNeeded() {
        return view.getHeight() > getHeight();
    }

    /**
     * TODO Prevent the orientation to be changed at any time.
     * The orientation shouldn't be changed after construction, or else,
     * incorrect behavior may arise.
     */
    private class SuiScrollBar extends SuiContainer implements SuiMouseListener {

        /* Indicates an horizontal orientation. */
        public final static int HORIZONTAL = 1;
        /* Indicates a vertical orientation. */
        public final static int VERTICAL = 2;
        /* The width or height according to the orientation. */
        final static int SIZE = 16;
        /* The minimum size of the thumb. */
        private final static int MIN_THUMB_SIZE = 8;

        /* The button used to increment. */
        SuiButton incButton;
        /* The button used to decrement. */
        SuiButton decButton;
        /* The draggable button to adjust the view. */
        SuiButton thumb;
        int unitsFactor;
        /* The orientation. This affects the visual aspect and scrolling direction. */
        int orientation;
        int maxThumbSize;

        /**
         * Creates a <code>SuiScrollBar</code> with the specified orientation.
         *
         * @param orientation The orientation.
         */
        SuiScrollBar(int orientation) {
            super();
            setOrientation(orientation);
            SuiScrollPane.this.add(this);
            if (orientation == HORIZONTAL)
                initHorizontally();
            else
                initVertically();
            add(incButton);
            add(decButton);
            //add(thumb);
            incButton.addMouseListener(this);
            decButton.addMouseListener(this);
        }

        /**
         * @param container The container holding the game.
         * @param g         The graphics context to render onto.
         */
        @Override
        protected void renderComponent(GameContainer container, Graphics g) {
            super.renderComponent(container, g);
            Color oldColor = g.getColor();
            g.setColor(Sui.getTheme().getPrimary2());
            g.fillRect(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight());
            g.setColor(oldColor);
        }

        public void mouseMoved(SuiMouseEvent sme) {
        }

        public void mouseDragged(SuiMouseEvent sme) {
        }

        public void mousePressed(SuiMouseEvent sme) {
            SuiContainer src = sme.getSource();
            if (src.equals(incButton))
                onIncrement();
            else if (src.equals(decButton))
                onDecrement();
        }

        public void mouseReleased(SuiMouseEvent sme) {
        }

        public void mouseEntered(SuiMouseEvent sme) {
        }

        public void mouseExited(SuiMouseEvent sme) {
        }

        void setOrientation(int orientation) {
            if (orientation != HORIZONTAL && orientation != VERTICAL)
                throw new IllegalArgumentException("Orientation must either HORIZONTAL or VERTICAL.");
            this.orientation = orientation;
        }

        void onIncrement() {
            if (orientation == HORIZONTAL) {
                SuiScrollPane.this.view.setX(SuiScrollPane.this.view.getX() * (-unitsFactor));
            } else {
                SuiScrollPane.this.view.setY(SuiScrollPane.this.view.getY() * unitsFactor);
            }
        }

        void onDecrement() {
            if (orientation == HORIZONTAL) {
                SuiScrollPane.this.view.setX(SuiScrollPane.this.view.getX() * unitsFactor);
            } else {
                SuiScrollPane.this.view.setY(SuiScrollPane.this.view.getY() * (-unitsFactor));
            }
        }

        /**
         * Initializes this <code>SuiScrollBar</code> horizontally.
         */
        void initHorizontally() {
            setHeight(SIZE);
            incButton = new ArrowButton(ArrowButton.FACE_RIGHT);
            decButton = new ArrowButton(ArrowButton.FACE_LEFT);
            thumb = new SuiButton();
            incButton.setSize(SIZE, SIZE);
            decButton.setSize(SIZE, SIZE);
            thumb.setHeight(SIZE);
            decButton.setLocation(0, 0);
            thumb.setY(0);
        }

        /**
         * Initializes this <code>SuiScrollBar</code> verctically.
         */
        void initVertically() {
            setWidth(SIZE);
            incButton = new ArrowButton(ArrowButton.FACE_UP);
            decButton = new ArrowButton(ArrowButton.FACE_DOWN);
            thumb = new SuiButton();
            incButton.setSize(SIZE, SIZE);
            decButton.setSize(SIZE, SIZE);
            thumb.setWidth(SIZE);
            incButton.setLocation(0, 0);
            thumb.setX(0);
        }

        /**
         * Adjust the horizontal locations of this <code>SuiScrollBar</code> and its
         * increment button, accordingly to the view.
         */
        void adjustHorizontally() {
            if (orientation != HORIZONTAL)
                throw new IllegalStateException("The orientation should be HORIZONTAL.");
            if (SuiScrollPane.this.isVerticalBarNeeded())
                setWidth(SuiScrollPane.this.getWidth() - SIZE);
            else
                setWidth(SuiScrollPane.this.getWidth());
            incButton.setX(getX() + getWidth() - incButton.getWidth());
            setLocation(0, SuiScrollPane.this.getHeight() - getHeight());
            maxThumbSize = getWidth() - incButton.getWidth() - decButton.getWidth();
            thumb.setWidth((int) (maxThumbSize *
                    ((float) SuiScrollPane.this.getWidth() / SuiScrollPane.this.view.getWidth())));
            unitsFactor = SuiScrollPane.this.view.getWidth() / maxThumbSize;
        }

        /**
         * Adjust the vertical locations of this <code>SuiScrollBar</code> and its
         * increment button, accordingly to the view.
         */
        void adjustVertically() {
            if (orientation != VERTICAL)
                throw new IllegalStateException("The orientation should be VERTICAL.");
            if (SuiScrollPane.this.isHorizontalBarNeeded())
                setHeight(SuiScrollPane.this.getHeight() - SIZE);
            else
                setHeight(SuiScrollPane.this.getHeight());
            decButton.setY(getY() + getHeight() - decButton.getHeight());
            setLocation(SuiScrollPane.this.getWidth() - getWidth(), 0);
            maxThumbSize = getHeight() - incButton.getHeight() - decButton.getHeight();
            thumb.setHeight((int) (maxThumbSize *
                    ((float) SuiScrollPane.this.getHeight() / SuiScrollPane.this.view.getHeight())));
            unitsFactor = SuiScrollPane.this.view.getHeight() / maxThumbSize;
        }
    }

    /**
     * // TODO Fix the arrow rendering bug.
     * Represents a button with an arrow rendered on top of it.
     */
    private static class ArrowButton extends SuiButton {

        public final static float FACE_UP = 0.0f;
        public final static float FACE_RIGHT = (float) (Math.PI / 2);
        public final static float FACE_DOWN = (float) Math.PI;
        public final static float FACE_LEFT = -(float) (Math.PI / 2);

        /* The arrow... */
        Shape arrow;

        /**
         * Creates an <code>ArrowButton</code> which arrow faces the specified angle.
         *
         * @param angle The angle the arrow faces.
         */
        ArrowButton(float angle) {
            super();
            arrow = new Polygon(new float[]{0.0f, 4.0f, 4.0f, 0.0f, 8.0f, 4.0f});
            arrow = arrow.transform(Transform.
                    createRotateTransform(angle, arrow.getCenterX(), arrow.getCenterY()));
        }

        /**
         * Renders an adicional arrow on top of this button.
         *
         * @param container The container holding the game.
         * @param g         The graphics context to render onto.
         */
        @Override
        protected void renderComponent(GameContainer container, Graphics g) {
            super.renderComponent(container, g);
            Color oldColor = g.getColor();
            g.setColor(Sui.getTheme().getForeground());
            g.translate(getAbsoluteX() + ((getWidth() - arrow.getBoundingCircleRadius()) / 2),
                    getAbsoluteY() + ((getHeight() - arrow.getBoundingCircleRadius()) / 2));
            g.fill(arrow);
            g.resetTransform();
            g.setColor(oldColor);
        }
    }
}
