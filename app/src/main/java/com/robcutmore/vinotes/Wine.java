package com.robcutmore.vinotes;


public class Wine {

    private long id;
    private Winery winery;
    private String name;
    private int vintage;

    public Wine(final long id, final Winery winery, final String name, final int vintage) {
        this.id = id;
        this.winery = winery;
        this.name = name;
        this.vintage = vintage;

        this.winery.addWine(this);
    }

    public long getId() {
        return this.id;
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