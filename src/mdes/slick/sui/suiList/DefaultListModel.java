package mdes.slick.sui.suiList;

import java.util.ArrayList;

/**
 * This is a basic implementation of a <code>SuiListModel</code>. It uses an <code>ArrayList</code> to hold the data.
 *
 * @author Alexandre Vieira - afvieira@student.dei.uc.pt
 * @version 0.1
 */
public class DefaultListModel implements SuiListModel {
    
    /** The list actually holding the data. */
    private ArrayList<? extends Object> list;

    /** Creates an empty list model. */
    public DefaultListModel() {
        this(new ArrayList<Object>());
    }

    /**
     * Creates a list model with the specified data.
     *
     * @param list The data.
     */
    public DefaultListModel(ArrayList<? extends Object> list) {
        this.list = list;
    }

    /**
     * Returns an element.
     *
     * @param index The index of the element.
     * @return The value at the specified index.
     */
    @Override
    public Object getElementAt(int index) {
        return list.get(index);
    }

    /**
     * Returns the size of the data.
     *
     * @return The size of the list.
     */
    @Override
    public int size() {
        return list.size();
    }

    public ArrayList<? extends Object> getList() {
        return list;
    }
}