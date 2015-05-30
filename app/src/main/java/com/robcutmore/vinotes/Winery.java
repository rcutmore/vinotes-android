package com.robcutmore.vinotes;


import java.util.ArrayList;

public class Winery {

    private String url;
    private String name;
    private ArrayList<Wine> wines;

    public Winery(final String name) {
        this.name = name;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Wine> getWines() {
        return this.wines;
    }

    public void addWine(final Wine wine) {
        this.wines.add(wine);
    }

    public void setWines(final ArrayList<Wine> wines) {
        this.wines = wines;
    }
}