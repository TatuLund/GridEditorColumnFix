package org.vaadin.grideditorcolumnfix.client;

import org.vaadin.grideditorcolumnfix.GridEditorColumnFix;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.widgets.Grid;
import com.vaadin.shared.ui.Connect;

// Connector binds client-side widget class to server-side component class
// Connector lives in the client and the @Connect annotation specifies the
// corresponding server-side component
@Connect(GridEditorColumnFix.class)
public class GridEditorColumnFixConnector extends AbstractExtensionConnector {

	private Grid<?> grid;
	

    // We must implement getState() to cast to correct type
    @Override
    public GridEditorColumnFixState getState() {
        return (GridEditorColumnFixState) super.getState();
    }

    private static native final void redrawEditor(Grid<?> grid) /*-{
		var editor = grid.@com.vaadin.client.widgets.Grid::getEditor()();
		editor.@com.vaadin.client.widgets.Grid.Editor::showOverlay()();
	}-*/;

    private static native final DivElement getEditorCellWrapper(Grid<?> grid) /*-{
		var editor = grid.@com.vaadin.client.widgets.Grid::getEditor()();
		return editor.@com.vaadin.client.widgets.Grid.Editor::cellWrapper;
	}-*/;

	@Override
	protected void extend(ServerConnector target) {
        grid = (Grid<Object>) ((ComponentConnector) target).getWidget();
        AnimationCallback editorColumnWidthFix = new AnimationCallback() {
            @Override
            public void execute(double timestamp) {
        		int cols = grid.getVisibleColumns().size();
        		DivElement cellWrapper = getEditorCellWrapper(grid);
            	for (int i=0;i<cols;i++) {
            		Element element = (Element) cellWrapper.getChild(i);
            		double width = grid.getVisibleColumns().get(i).getWidthActual();
            		element.getStyle().setWidth(width, Style.Unit.PX);
            	}
            }
        };
        grid.addColumnVisibilityChangeHandler(event -> {
        	if (grid.isEditorActive()) {
        		redrawEditor(grid);
        		AnimationScheduler.get().requestAnimationFrame(editorColumnWidthFix);
        	}
        });
        grid.addColumnResizeHandler(event -> {
        	if (grid.isEditorActive()) {
        		AnimationScheduler.get().requestAnimationFrame(editorColumnWidthFix);
        	}        	
        });

	}
}
