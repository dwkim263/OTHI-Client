package mdes.slick.sui.suiList;

import mdes.slick.sui.event.SuiListener;

/**
 * @author Alexandre Vieira - afvieira@student.dei.uc.pt
 * @version 0.1
 */
public interface SuiListListener extends SuiListener {

    void selectionChanged(SuiListEvent sle);
}