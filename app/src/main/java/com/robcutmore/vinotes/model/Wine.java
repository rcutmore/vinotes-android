package com.robcutmore.vinotes.model;


public class Wine {

    private Long id;
    private Winery winery;
    private String name;
    private int vintage;

    public Wine(final Winery winery, final String name, final int vintage) {
        this.winery = winery;
        this.name = name;
        this.vintage = vintage;
        this.id = null;
    }

    public Wine(final long id, final Winery winery, final String name, final int vintage) {
        this(winery, name, vintage);
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final long id) {
        // Only allow id to be set once.
        this.id = this.id == null ? id : this.id;
    }

    public Winery getWinery() {
        return this.winery;
    }

    public String getName() {
        return this.name;
    }

    public int getVintage() {
        return this.vintage;
    }

    @Override
    public String toString() {
        return String.format("%s %s %d", this.winery.toString(), this.name, this.vintage);
    }

}