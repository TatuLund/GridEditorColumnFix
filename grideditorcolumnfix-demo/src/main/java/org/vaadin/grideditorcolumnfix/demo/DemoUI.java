package org.vaadin.grideditorcolumnfix.demo;

import org.apache.commons.lang3.StringUtils;
import org.vaadin.grideditorcolumnfix.GridEditorColumnFix;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.annotation.WebServlet;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Binder;
import com.vaadin.data.Binder.Binding;
import com.vaadin.data.converter.LocalDateTimeToDateConverter;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.HtmlRenderer;

@Theme("demo")
@Title("GridEditorColumnFix Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

	
    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class)
    public static class Servlet extends VaadinServlet {
    	
    }

    @Override
    protected void init(VaadinRequest request) {

   	
        // Initialize our new UI component
		final Grid<SimplePojo> grid = new Grid<>();
        final GridEditorColumnFix component = new GridEditorColumnFix(grid);

		Random random = new Random(4837291937l);
		List<SimplePojo> data = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			String description = StringUtils.repeat("text", " ", Integer.valueOf(random.nextInt(50)));
			data.add(new SimplePojo(i, description, true, new Date(),
					BigDecimal.valueOf(random.nextDouble() * 100), Integer
							.valueOf(random.nextInt(5))));
		}

		Binder<SimplePojo> binder = grid.getEditor().getBinder();
		
		grid.addComponentColumn(item -> { 
			Panel panel = new Panel();
			panel.setSizeFull();
			Label label = new Label(item.getLD().toString());
			label.setSizeFull();
			DateField date = new DateField();
			date.setSizeFull();
			panel.addClickListener(event -> {
				date.setValue(item.getLD());
				panel.setContent(date);
			});
			date.addValueChangeListener(event -> {
				item.setLD(date.getValue());
				label.setValue(date.getValue().toString());				
				panel.setContent(label);
			});
			date.addFocusListener(event -> {
				
			});
			date.addBlurListener(event -> {
				item.setLD(date.getValue());
				label.setValue(date.getValue().toString());
				panel.setContent(label);
			});
			panel.setContent(label);
			return panel; 
		}).setId("localDate");		
		
		TextField textField = new TextField();
		Binding<SimplePojo, String> descriptionBinding = binder.forField(textField).asRequired("Empty value not accepted").bind(SimplePojo::getDescription,
				SimplePojo::setDescription);
		grid.addColumn(SimplePojo::getDescription)
			.setEditorBinding(descriptionBinding)
			.setHidable(true).setCaption("Description")
			.setStyleGenerator(item -> (item.getDescription().length() > 100 ? "long-text" : null))
			.setDescriptionGenerator(item -> (item.getDescription().length() > 100 ? "use scroller to show more" : null))
			.setWidth(600);			
		descriptionBinding.setReadOnly(true);
		
		TextField starsField = new TextField();
		Binding<SimplePojo, Integer> starsBinding = binder.forField(starsField).withNullRepresentation("")
				.withConverter(new StringToIntegerConverter("Must enter a number")).withValidator(new IntegerRangeValidator("Input integer between 0 and 10",0,10))
				.bind(SimplePojo::getStars, SimplePojo::setStars);
		grid.addColumn(SimplePojo::getStars).setEditorBinding(starsBinding).setHidable(true).setCaption("Rating");

		CheckBox checkBox = new CheckBox();
		Binding<SimplePojo, Boolean> truthBinding = binder.forField(checkBox).bind(SimplePojo::isTruth,
				SimplePojo::setTruth);
		grid.addColumn(simplePojo -> (simplePojo.isTruth() ? VaadinIcons.CHECK_SQUARE_O : VaadinIcons.THIN_SQUARE).getHtml()
				, new HtmlRenderer()).setEditorBinding(truthBinding).setHidable(true).setCaption("In stock").setStyleGenerator(style -> "v-align-center");

		DateTimeField dateField = new DateTimeField();
		OffsetDateTime odt = OffsetDateTime.now(ZoneId.systemDefault());
		ZoneOffset zoneOffset = odt.getOffset();
		SimpleDateFormat dateTimeFormat = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.SHORT, UI.getCurrent().getLocale());
		Binding<SimplePojo, Date> dateBinding = binder.forField(dateField)
				.withConverter(new LocalDateTimeToDateConverter(zoneOffset))
				.bind(SimplePojo::getDate, SimplePojo::setDate);
		grid.addColumn(SimplePojo::getDate).setEditorBinding(dateBinding).setHidable(true).setCaption("Date & Time");

		TextField numberField = new TextField();
		Binding<SimplePojo, BigDecimal> numberBinding = binder.forField(numberField).withNullRepresentation("")
				.withConverter(new StringToBigDecimalConverter("Must enter a number"))
				.bind(SimplePojo::getNumber, SimplePojo::setNumber);
		grid.addColumn(SimplePojo::getNumber).setEditorBinding(numberBinding).setHidable(true).setCaption("Value");
//		grid.setFrozenColumnCount(1);
		
		grid.setItems(data);
		grid.setSizeFull();
		grid.addSelectionListener(event -> {			
			System.out.println("Selection");
			grid.getSelectionModel().getFirstSelectedItem().ifPresent(item -> System.out.println(item.toString()));			
		});		
//		grid.getEditor().addOpenListener(event -> {
//			grid.select(event.getBean());
//		});
		grid.setSelectionMode(SelectionMode.MULTI);
		
		// Filtering example
		ListDataProvider<SimplePojo> dp = (ListDataProvider) grid.getDataProvider();
		TextField filterField = new TextField();
		filterField.setPlaceholder("filter");
		filterField.setWidth("100%");
		filterField.addValueChangeListener(event -> {
			dp.setFilter(bean -> bean.getLD().toString(),value -> value.contains(event.getValue()));
		});		
		grid.getHeaderRow(0).getCell("localDate").setComponent(filterField);
		grid.setHeaderRowHeight(42);
		
//		grid.setDetailsGenerator(item -> new Label(item.getDescription()));
//		grid.addItemClickListener(event -> {
//			if (grid.isDetailsVisible(event.getItem())) grid.setDetailsVisible(event.getItem(), false);
//			else grid.setDetailsVisible(event.getItem(), true);
//		});
        grid.getEditor().setEnabled(true);
        grid.getEditor().setBuffered(false);
//        grid.setFrozenColumnCount(2);
//        grid.setHeightMode(HeightMode.ROW);
//        grid.setHeightByRows(grid.getDataCommunicator().getDataProviderSize());
//        Panel panel = new Panel();
//        panel.setSizeFull();
//        panel.setContent(grid);
        
        Chart chart = new Chart();
        DataSeries ds = new DataSeries();
        ds.setData(10,20,30,20,5,40,10);
        chart.getConfiguration().setSeries(ds);
        chart.setSizeFull();
        
        // Show it in the middle of the screen
        final VerticalLayout layout = new VerticalLayout();
        layout.setStyleName("demoContentLayout");
        layout.addComponents(grid,chart);
        layout.setExpandRatio(grid, 3);
        layout.setExpandRatio(chart, 1);
        layout.setSizeFull();
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);
    }
}
