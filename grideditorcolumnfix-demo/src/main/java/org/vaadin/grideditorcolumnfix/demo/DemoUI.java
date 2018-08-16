package org.vaadin.grideditorcolumnfix.demo;

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

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Binder;
import com.vaadin.data.Binder.Binding;
import com.vaadin.data.converter.LocalDateTimeToDateConverter;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

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
			data.add(new SimplePojo(i, "Bean", true, new Date(),
					BigDecimal.valueOf(random.nextDouble() * 100), Integer
							.valueOf(random.nextInt(5))));
		}

		Binder<SimplePojo> binder = grid.getEditor().getBinder();
		
		TextField textField = new TextField();
		Binding<SimplePojo, String> descriptionBinding = binder.forField(textField).asRequired("Empty value not accepted").bind(SimplePojo::getDescription,
				SimplePojo::setDescription);
		grid.addColumn(SimplePojo::getDescription).setEditorBinding(descriptionBinding).setHidable(true).setCaption("Description");			

		TextField starsField = new TextField();
		Binding<SimplePojo, Integer> starsBinding = binder.forField(starsField).withNullRepresentation("")
				.withConverter(new StringToIntegerConverter("Must enter a number")).withValidator(new IntegerRangeValidator("Input integer between 0 and 10",0,10))
				.bind(SimplePojo::getStars, SimplePojo::setStars);
		grid.addColumn(SimplePojo::getStars).setEditorBinding(starsBinding).setHidable(true).setCaption("Rating");

		CheckBox checkBox = new CheckBox();
		Binding<SimplePojo, Boolean> truthBinding = binder.forField(checkBox).bind(SimplePojo::isTruth,
				SimplePojo::setTruth);
		grid.addColumn(SimplePojo::isTruth).setEditorBinding(truthBinding).setHidable(true).setCaption("In stock");

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
		
		grid.setItems(data);
		grid.setSizeFull();
        grid.getEditor().setEnabled(true);
        grid.getEditor().setBuffered(true);
        
        // Show it in the middle of the screen
        final VerticalLayout layout = new VerticalLayout();
        layout.setStyleName("demoContentLayout");
        layout.addComponent(grid);
        layout.setSizeFull();
        layout.setMargin(false);
        layout.setSpacing(false);
        setContent(layout);
    }
}
