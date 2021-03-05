package org.vaadin.grideditorcolumnfix.client;

import org.vaadin.grideditorcolumnfix.GridEditorColumnFix;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.user.client.Window;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.VConsole;
import com.vaadin.client.data.DataChangeHandler;
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

    public static native final DivElement getEditorOverlay(Grid<?> grid) /*-{
		var editor = grid.@com.vaadin.client.widgets.Grid::getEditor()();
		return editor.@com.vaadin.client.widgets.Grid.Editor::editorOverlay;
	}-*/;

    public static native final DivElement getFrozenCellWrapper(Grid<?> grid) /*-{
		var editor = grid.@com.vaadin.client.widgets.Grid::getEditor()();
		return editor.@com.vaadin.client.widgets.Grid.Editor::frozenCellWrapper;
	}-*/;

    
	// Return -1.0 if Grid has no vertical scroll bar otherwise its width
	private double getVerticalScrollBarWidth() {
		for (Element e : getGridParts("div")) {
			if (e.getClassName().contains("v-grid-scroller-vertical")) {
				if (BrowserInfo.get().isIE11() || BrowserInfo.get().isEdge()) { 
					return e.getClientWidth();
				} else {
					return e.getOffsetWidth();					
				}
			}
		}
		return -1.0;
	}
	
	// Get elements in Grid by tag name
	private Element[] getGridParts(String elem) {
		NodeList<Element> elems = grid.getElement().getElementsByTagName(elem);
		Element[] ary = new Element[elems.getLength()];
		for (int i = 0; i < ary.length; ++i) {
			ary[i] = elems.getItem(i);
		}
		return ary;
	}

	private void doEditorScrollOffsetFix() {
		try {
			DivElement cellWrapper = getEditorCellWrapper(grid);
			if (cellWrapper == null) return; 
			DivElement frozenCellWrapper = getFrozenCellWrapper(grid);
    		int offset = 0;
    		double selectionWidth = -1.0;
    		if (grid.getSelectionColumn().isPresent()) {
    			offset = 1;
    			selectionWidth = grid.getColumns().get(0).getWidthActual();
    		}
			double scrollLeft = grid.getScrollLeft();
			int row = grid.getEditor().getRow();
			TableRowElement rowElement = grid.getEscalator().getBody().getRowElement(row);
			int rowLeft = Math.abs(rowElement.getAbsoluteLeft());
			int editorLeft = Math.abs(cellWrapper.getAbsoluteLeft());
			int frozenColumns = grid.getFrozenColumnCount();
        	int frozenWidth = 0;
        	for (int i=0;i<frozenColumns;i++) {
        		Element element = (Element) frozenCellWrapper.getChild(i);
        		if (element == null) break;
        		double width = grid.getVisibleColumns().get(i+offset).getWidthActual();
        		frozenWidth += width;
        	}
			if (scrollLeft > 0 && frozenColumns > 0) {
				cellWrapper.getStyle().setLeft((frozenWidth - scrollLeft),
						Style.Unit.PX);				
			} else {
				if (selectionWidth > 0) { 
					cellWrapper.getStyle().setLeft((selectionWidth - scrollLeft),
							Style.Unit.PX);				
				} else if (editorLeft != rowLeft) {
					cellWrapper.getStyle().setLeft(editorLeft - (scrollLeft + rowLeft),
						Style.Unit.PX);
				}
			}
        } catch (IllegalStateException e) {
            // IllegalStateException may occur if user has scrolled Grid so
            // that Escalator has updated, and row under Editor is no longer
            // there
		} catch (IndexOutOfBoundsException e) {
		}        
	}
	
    @Override
	protected void extend(ServerConnector target) {
    	grid = (Grid<Object>) ((ComponentConnector) target).getWidget();
        AnimationCallback editorColumnAndWidthFix = new AnimationCallback() {
            @Override
            public void execute(double timestamp) {
        		int cols = grid.getVisibleColumns().size();
        		int offset = 0;
        		if (grid.getSelectionColumn().isPresent()) offset = 1;
        		DivElement editorOverlay = getEditorOverlay(grid);
        		if (editorOverlay == null) return;
        		DivElement frozenCellWrapper = getFrozenCellWrapper(grid);
        		Double scrollerWidth = getVerticalScrollBarWidth();
        		Double gridWidth = (double) grid.getOffsetWidth();
        		if (scrollerWidth > 0.0) gridWidth = gridWidth - scrollerWidth; 
        		editorOverlay.getStyle().setWidth(gridWidth, Style.Unit.PX);
        		DivElement cellWrapper = getEditorCellWrapper(grid);
        		int frozenColumns = grid.getFrozenColumnCount();
            	for (int i=0;i<cols;i++) {
            		Element element = (Element) cellWrapper.getChild(i);
            		if (element == null) break;
            		double width = grid.getVisibleColumns().get(i+offset+frozenColumns).getWidthActual();
            		element.getStyle().setWidth(width, Style.Unit.PX);
            	}
            	int frozenWidth = 0;
            	for (int i=0;i<frozenColumns;i++) {
            		Element element = (Element) frozenCellWrapper.getChild(i);
            		if (element == null) break;
            		double width = grid.getVisibleColumns().get(i+offset).getWidthActual();
            		frozenWidth += width;
            		element.getStyle().setWidth(width, Style.Unit.PX);
            	}
            	if (frozenColumns > 0) {
    				cellWrapper.getStyle().setLeft((frozenWidth - grid.getScrollLeft()),
    						Style.Unit.PX);				
            	}
            }
        };
        AnimationCallback editorColumnWidthFix = new AnimationCallback() {
            @Override
            public void execute(double timestamp) {
        		DivElement cellWrapper = getEditorCellWrapper(grid);
        		if (cellWrapper == null) return;
        		DivElement frozenCellWrapper = getFrozenCellWrapper(grid);
        		int cols = grid.getVisibleColumns().size();
        		int offset = 0;
        		if (grid.getSelectionColumn().isPresent()) offset = 1;
        		int frozenColumns = grid.getFrozenColumnCount();
            	for (int i=0;i<cols;i++) {
            		Element element = (Element) cellWrapper.getChild(i);
            		if (element == null) break;
            		double width = grid.getVisibleColumns().get(i+offset+frozenColumns).getWidthActual();
            		element.getStyle().setWidth(width, Style.Unit.PX);
            	}
            	int frozenWidth = 0;
            	for (int i=0;i<frozenColumns;i++) {
            		Element element = (Element) frozenCellWrapper.getChild(i);
            		if (element == null) break;
            		double width = grid.getVisibleColumns().get(i+offset).getWidthActual();
            		frozenWidth += width;
            		element.getStyle().setWidth(width, Style.Unit.PX);
            	}
            	if (frozenColumns > 0) {
    				cellWrapper.getStyle().setLeft((frozenWidth - grid.getScrollLeft()),
    						Style.Unit.PX);				
            	}
            }
        };
        AnimationCallback editorScrollOffsetFix = new AnimationCallback() {
            @Override
            public void execute(double timestamp) {
        		doEditorScrollOffsetFix();
            }

        };
        grid.addScrollHandler(event -> {
        	if (grid.isEditorActive()) {
        		Scheduler.get().scheduleFinally(() -> {
            		doEditorScrollOffsetFix();        			
        		});
        	}        					        	
        });        
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
		Window.addResizeHandler(event -> {
        	if (grid.isEditorActive()) {
        		AnimationScheduler.get().requestAnimationFrame(editorColumnAndWidthFix);
        	}
		});

    	registerRpc(GridEditorColumnFixClientRpc.class, new GridEditorColumnFixClientRpc() {
			@Override
			public void applyFix() {
	        	if (grid.isEditorActive()) {
	        		AnimationScheduler.get().requestAnimationFrame(editorScrollOffsetFix);
	        	}        					
			}    		
    	});    	
	}
}
