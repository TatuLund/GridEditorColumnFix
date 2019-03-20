package org.vaadin.grideditorcolumnfix;

import org.vaadin.grideditorcolumnfix.client.GridEditorColumnFixClientRpc;
import org.vaadin.grideditorcolumnfix.client.GridEditorColumnFixState;

import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.Grid;

// This is the server-side UI component that provides public API 
// for GridEditorColumnFix
public class GridEditorColumnFix extends AbstractExtension {

    public GridEditorColumnFix(Grid grid) {
		this.extend(grid);
		grid.getEditor().addOpenListener(event -> {
			getRpc().applyFix();
		});
    }

    // We must override getState() to cast the state to GridEditorColumnFixState
    @Override
    protected GridEditorColumnFixState getState() {
        return (GridEditorColumnFixState) super.getState();
    }
 
	private GridEditorColumnFixClientRpc getRpc() {
		return getRpcProxy(GridEditorColumnFixClientRpc.class);
	}    
}
