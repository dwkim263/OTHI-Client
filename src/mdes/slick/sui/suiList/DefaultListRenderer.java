package mdes.slick.sui.suiList;

import mdes.slick.sui.Sui;
import mdes.slick.sui.SuiContainer;
import mdes.slick.sui.SuiLabel;

/**
 * A simple <code>SuiListCellRenderer</code>.
 *
 * @author Alexandre Vieira - afvieira@student.dei.uc.pt
 * @version 0.1
 */
public class DefaultListRenderer implements SuiListCellRenderer {

    /**
     * Creates a <code>SuiLabel</code> with the text returned by the <code>value.toString()</code>, packed, a height of
     * 16 pixels and returns it.
     *
     * @param value The value to represent.
     * @return A label representing the specified value.
     */
    @Override
    public SuiContainer getContainer(Object value) {
        SuiLabel label = new SuiLabel(value.toString());
        label.setHorizontalAlignment(SuiLabel.LEFT_ALIGNMENT);
        label.pack();
        label.setHeight(16);
        return label;
    }

    /**
     * This method is called whenever an item is selected or deselected.
     *
     * @param cell       The component used to render the selected item.
     * @param isSelected The selected state of the item.
     */
    @Override
    public void containerStatusChanged(SuiContainer cell, boolean isSelected) {
        ((SuiLabel) cell).setForegroundColor(isSelected ? Sui.getTheme().getPrimary1()
                : Sui.getTheme().getForeground());
    }
}