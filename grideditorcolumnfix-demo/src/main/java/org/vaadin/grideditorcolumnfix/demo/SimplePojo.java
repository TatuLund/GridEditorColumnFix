package org.vaadin.grideditorcolumnfix.demo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public class SimplePojo implements Serializable {
    private long id;
    private String description;
    private boolean yes;
    private boolean truth;
    private Date date;
    private BigDecimal number;
    private Integer stars;
    private LocalDate ld;
    
    public SimplePojo() {
    }

    public SimplePojo(long id, String description, boolean yes, Date date, BigDecimal number, Integer stars) {
        this.id = id;
        this.description = description;
        this.yes = yes;
        this.date = date;
        this.number = number;
		this.stars = stars;
		this.ld = LocalDate.now().minusDays(stars);
    }

	public Integer getStars() {
		return stars;
	}
	
	public void setStars(Integer stars) {
		this.stars = stars;
	}
	
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getNumber() {
        return number;
    }

    public void setNumber(BigDecimal number) {
        this.number = number;
    }

    public LocalDate getLD() {
    	return this.ld;
    }
    
    public void setLD(LocalDate ld) {
    	this.ld = ld;
    }
    
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isYes() {
        return yes;
    }

    public void setYes(boolean yes) {
        this.yes = yes;
    }

    public boolean isTruth() {
        return truth;
    }

    public void setTruth(boolean truth) {
        this.truth = truth;
    }

}
