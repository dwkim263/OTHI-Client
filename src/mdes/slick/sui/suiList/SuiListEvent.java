package mdes.slick.sui.suiList;

import mdes.slick.sui.SuiContainer;
import mdes.slick.sui.event.SuiEvent;

/**
 * Represents a list selection.
 * @author Alexandre Vieira - afvieira@student.dei.uc.pt
 * @version 0.1
 */
public class SuiListEvent extends SuiEvent {

    private int[] selectedIndexes;

    public SuiListEvent(SuiContainer source, int id, int[] selectedIndexes) {
        super(source);
        this.selectedIndexes = selectedIndexes;
    }

    public int[] getSelectedIndexes() {
        return selectedIndexes;
    }
}