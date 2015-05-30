package com.robcutmore.vinotes;


public class Wine {

    private String url;
    private Winery winery;
    private String name;
    private int vintage;

    public Wine(final Winery winery, final String name, final int vintage) {
        this.winery = winery;
        this.name = name;
        this.vintage = vintage;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(final String url) {
        this.url = url;
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
}