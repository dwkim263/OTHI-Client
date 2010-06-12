package mdes.slick.sui.suiList;

import mdes.slick.sui.SuiContainer;

/**
 * This is used to delegate the rendering of items of a <code>SuiList</code>.
 * @author Alexandre Vieira - afvieira@student.dei.uc.pt
 * @version 0.1
 */
public interface SuiListCellRenderer {

    SuiContainer getContainer(Object value);

    void containerStatusChanged(SuiContainer cell, boolean isSelected);
}