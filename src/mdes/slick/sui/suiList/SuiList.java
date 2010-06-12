package mdes.slick.sui.suiList;

import mdes.slick.sui.Sui;
import mdes.slick.sui.SuiContainer;
import mdes.slick.sui.event.SuiKeyEvent;
import mdes.slick.sui.event.SuiKeyListener;
import mdes.slick.sui.event.SuiMouseEvent;
import mdes.slick.sui.event.SuiMouseListener;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Color;
import org.newdawn.slick.Input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a basic implementation of a list.</p>At the moment, two modes of selection are avaliable: the
 * SINGLE_SELECTION mode permits a single selected item at most, the MULTIPLE_SELECTION mode allows several selected
 * items at a time. When using this last mode, the user may select several contiguous items or intervaled ones by
 * pressing the corresponding key. The default key for contiguous selection is <code>Input.KEY_LSHIFT</code> and for the
 * intervaled selection is <code>Input.KEY_LCONTROL</code>.
 *
 * @author Alexandre Vieira - afvieira@student.dei.uc.pt
 * @version 0.1
 */
public class SuiList extends SuiContainer implements SuiKeyListener, SuiMouseListener {

    public final static int SINGLE_SELECTION = 1;
    public final static int MULTIPLE_SELECTION = 2;

    /** The model holding the real list data. */
    private SuiListModel model;
    /** The renderer used to delegate UI rendering. */
    private SuiListCellRenderer cellRenderer;
    /** Provides unique ids in a safe way. Not sure if it's needed... */
    private AtomicInteger listEventIdFactory;
    /** A list containing the indexes of the selected items. */
    private ArrayList<Integer> selectedIndexes;
    /** The index of the item on which the mouse is released. */
    private int releasedIndex;
    /** The index of the item acting as an anchor when making a contiguous slection. */
    private int anchorIndex;
    /** The slection mode in usage. */
    private int selectionMode;
    /** The key used to trigger a contiguous selection. */
    private int contiguousKey;
    /** The key used to trigger an intervaled selection. */
    private int intervaledKey;
    private boolean anchoring;
    private boolean usingContiguousSelection;
    private boolean usingIntervaledSelection;
    /** If true, rendering components will adjust their width to match the list. */
    private boolean itemsFitToList;

    /** Creates a list with a default model. */
    public SuiList() {
        this(new DefaultListModel());
    }

    /**
     * Creates a list with the specified model.
     *
     * @param model The model holding the data.
     */
    public SuiList(SuiListModel model) {
        this(model, true);
    }

    /**
     * Creates a list with the specified model and adjusts the cells' width to match the list.
     *
     * @param model          The model holding the data.
     * @param itemsFitToList <code>true</code> if hte cells should be adjusted.
     */
    public SuiList(SuiListModel model, boolean itemsFitToList) {
        super();
        this.cellRenderer = new DefaultListRenderer();
        this.model = model;
        this.listEventIdFactory = new AtomicInteger();
        this.selectedIndexes = new ArrayList<Integer>();
        this.anchoring = true;
        this.usingContiguousSelection = false;
        this.usingIntervaledSelection = false;
        setSelectionMode(MULTIPLE_SELECTION);
        setContiguousKey(Input.KEY_LSHIFT);
        setIntervaledKey(Input.KEY_LCONTROL);
        setItemsFitToList(itemsFitToList);
        addKeyListener(this);
        addMouseListener(this);
    }
        
    @Override
    protected void renderBorder(GameContainer container, Graphics g) {
        Color oldColor =  g.getColor();
        g.setColor(getBorderColor());
        g.drawRect(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight()-1);
        g.setColor(oldColor);    
    }

    /**
     * Sets the width of this list. NOTE: Rebuilds the list if necessary.
     *
     * @param width The width of the list.
     */
    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        if (itemsFitToList)
            buildToFit();
        else build();
    }

    @Override
    public void keyPressed(SuiKeyEvent ske) {
        if (selectionMode == MULTIPLE_SELECTION)
            if (ske.getKeyCode() == intervaledKey && !usingIntervaledSelection)
                usingIntervaledSelection = true;
            else if (ske.getKeyCode() == contiguousKey && !usingContiguousSelection)
                usingContiguousSelection = true;
    }

    @Override
    public void keyReleased(SuiKeyEvent ske) {
        if (selectionMode == MULTIPLE_SELECTION)
            if (ske.getKeyCode() == intervaledKey)
                usingIntervaledSelection = false;
            else if (ske.getKeyCode() == contiguousKey)
                usingContiguousSelection = false;
    }

    @Override
    public void mouseMoved(SuiMouseEvent sme) {
    }

    @Override
    public boolean hasFocus() {
        boolean focus = super.hasFocus();
        int i = 0;
        while (!focus && (i < getChildCount())){
            focus = getChild(i).hasFocus();
            if (focus) return focus;
            ++i;
        }
        return focus;         
    }
    
    @Override
    public void mouseDragged(SuiMouseEvent sme) {
    }

    @Override
    public void mousePressed(SuiMouseEvent sme) {
    }

    // TODO This method is too confusing!
    // TODO Fix the whole selecting mechanism.
    @Override
    public void mouseReleased(SuiMouseEvent sme) {
        if (sme.getSource().equals(this))
            clearSelection();
        else {
            releasedIndex = getChildIndexFor(sme);
            if (selectionMode == SINGLE_SELECTION) {
                clearSelection();
                addIndex(releasedIndex, false);
            } else {
                if (usingIntervaledSelection) {
                    addIndex(releasedIndex, true);
                } else if (usingContiguousSelection) {
                    if (anchoring) {
                        anchorIndex = releasedIndex;
                        addIndex(anchorIndex, false);
                        anchoring = false;
                    } else {
                        anchoring = true;
                        ArrayList<Integer> indexes = makeSelection(anchorIndex, releasedIndex);
                        for (int index : indexes)
                            addIndex(index, false);
                    }
                } else {
                    clearSelection();
                    addIndex(releasedIndex, false);
                }
            }
            fireListEvent();
        }
    }

    /**
     * We want to the list to have focus to handle selections.
     *
     * @param sme The generated event.
     */
    @Override
    public void mouseEntered(SuiMouseEvent sme) {
        grabFocus();
    }

    /**
     * We want to release the focus ONLY when the source is NOT the list.
     *
     * @param sme The generated event.
     */
    @Override
    public void mouseExited(SuiMouseEvent sme) {
        if (sme.getSource().equals(this)) {
            usingIntervaledSelection = false;
            usingContiguousSelection = false;
            releaseFocus();
        }
    }

    /**
     * Sets the model. Note: Rebuilds the list.
     *
     * @param model The model holding the data.
     */
    public void setModel(SuiListModel model) {
        this.model = model;
        if (itemsFitToList)
            buildToFit();
        else build();
    }

    /**
     * Sets the cell renderer. Note: Rebuilds the list.
     *
     * @param cellRenderer The renderer to delegate UI rendering.
     */
    public void setCellRenderer(SuiListCellRenderer cellRenderer) {
        this.cellRenderer = cellRenderer;
        if (itemsFitToList)
            buildToFit();
        else build();
    }

    /**
     * Sets the key used for contiguous selections.
     *
     * @param key The code of the key.
     */
    public void setContiguousKey(int key) {
        this.contiguousKey = key;
    }

    /**
     * Sets the key used for intervaled selections.
     *
     * @param key The code of the key.
     */
    public void setIntervaledKey(int key) {
        this.intervaledKey = key;
    }

    /**
     * Sets the selection mode. SINGLE_SELECTION and MULTIPLE_SELECTION are allowed.
     *
     * @param selectionMode The selection mode.
     */
    public void setSelectionMode(int selectionMode) {
        if (selectionMode != SINGLE_SELECTION && selectionMode != MULTIPLE_SELECTION)
            throw new IllegalArgumentException("Illegal selection mode: " + selectionMode);
        this.selectionMode = selectionMode;
    }

    /**
     * If set to <code>true</code>, cells' width will be adjusted to match the list.
     *
     * @param itemsFitToList <code>true</code> if hte cells should be adjusted.
     */
    public void setItemsFitToList(boolean itemsFitToList) {
        this.itemsFitToList = itemsFitToList;
        if (itemsFitToList)
            buildToFit();
        else build();
    }

    public SuiListModel getModel() {
        return model;
    }

    public SuiListCellRenderer getCellRenderer() {
        return cellRenderer;
    }

    public ArrayList<Integer> getSelectedIndexes() {
        return selectedIndexes;
    }

    /**
     * Returns the first index if one or more items are selected, <code>-1</code> otherwise.
     *
     * @return The selected index.
     */
    public int getSelectedIndex() {
        return selectedIndexes.size() > 0 ? selectedIndexes.get(0) : -1;
    }

    public int getContiguousKey() {
        return contiguousKey;
    }

    public int getIntervaledKey() {
        return intervaledKey;
    }

    public int getSelectionMode() {
        return selectionMode;
    }

    public boolean isItemsFitToList() {
        return itemsFitToList;
    }

    /**
     * Adds a listener interested in selections event.
     *
     * @param listener The interested listener.
     */
    public void addListListener(SuiListListener listener) {
        listenerList.add(SuiListListener.class, listener);
    }

    /**
     * Removes a listener interested in selections event.
     *
     * @param listener The interested listener.
     */
    public void removeListListener(SuiListListener listener) {
        listenerList.remove(SuiListListener.class, listener);
    }

    /** Clears any selected items. */
    public void clearSelection() {
        for (int index : selectedIndexes)
            cellRenderer.containerStatusChanged(getChild(index), false);
        selectedIndexes.clear();
    }

    /**
     * Returns a list containing the indexes between <var>from</var> and <var>to</to>, inclusive.
     *
     * @param from The starting index.
     * @param to   The ending index.
     * @return The selected indexes.
     */
    protected ArrayList<Integer> makeSelection(int from, int to) {
        int dif = from - to;
        int signum = dif < 0 ? 1 : -1;
        int length = Math.abs(dif) + 1;
        ArrayList<Integer> theSelectedIndexes = new ArrayList<Integer>(length);
        for (int index = 0, plus = 0; index < length; index++, plus += signum)
            theSelectedIndexes.add(from + plus);
        return theSelectedIndexes;
    }

    protected void fireListEvent() {
        SuiListListener[] listListeners = listenerList.getListeners(SuiListListener.class);
        int[] indexes = new int[selectedIndexes.size()];
        for (int index = 0; index < indexes.length; index++)
            indexes[index] = selectedIndexes.get(index);
        Arrays.sort(indexes);
        SuiListEvent event = new SuiListEvent(this, listEventIdFactory.getAndIncrement(), indexes);
        for (int index = 0; index < listListeners.length; index++)
            listListeners[index].selectionChanged(event);
    }

    protected void fireListEvent(int id, int[] selectedIndexes) {
        SuiListListener[] listListeners = listenerList.getListeners(SuiListListener.class);
        SuiListEvent event = new SuiListEvent(this, id, selectedIndexes);
        for (int index = 0; index < listListeners.length; index++)
            listListeners[index].selectionChanged(event);
    }

    /**
     * Returns the index of the child which generated the specified event.
     *
     * @param sme The generated event.
     * @return The index of the child.
     */
    private int getChildIndexFor(SuiMouseEvent sme) {
        SuiContainer[] children = getChildren();
        for (int index = 0; index < children.length; index++)
            if (children[index].equals(sme.getSource()))
                return index;
        return -1;
    }

    /**
     * Adds an index to the list.
     *
     * @param index The selected index.
     * @param clear If <code>true</code>, adding an existent index will reset it.
     */
    private void addIndex(int index, boolean clear) {
        if (selectedIndexes.contains(index)) {
            if (clear) {
                selectedIndexes.remove(new Integer(index));
                cellRenderer.containerStatusChanged(getChild(index), false);
            }
        } else {
            selectedIndexes.add(index);
            cellRenderer.containerStatusChanged(getChild(index), true);
        }
    }

    /** Sets up the components to display the list. */
    private void build() {
        if (model != null) {
            removeAll();
            SuiContainer suiContainer;
            int y = 0;
            for (int index = 0; index < model.size(); index++) {
                suiContainer = cellRenderer.getContainer(model.getElementAt(index));
                cellRenderer.containerStatusChanged(suiContainer, false);
                suiContainer.setGlassPane(false);
                suiContainer.setLocation(0, y);
                suiContainer.addKeyListener(this);
                suiContainer.addMouseListener(this);
                y += suiContainer.getHeight();
                add(suiContainer, index);
            }
        }
    }

    /** Sets up the components to display the list. The cells' with are adjusted to match this list's. */
    private void buildToFit() {
        removeAll();
        SuiContainer suiContainer;
        int y = 0;
        for (int index = 0; index < model.size(); index++) {
            suiContainer = cellRenderer.getContainer(model.getElementAt(index));
            cellRenderer.containerStatusChanged(suiContainer, false);
            suiContainer.setGlassPane(false);
            suiContainer.setWidth(getWidth());
            suiContainer.setLocation(0, y);
            suiContainer.addKeyListener(this);
            suiContainer.addMouseListener(this);
            y += suiContainer.getHeight();
            add(suiContainer, index);
        }
    }
}