package com.robcutmore.vinotes;


import java.util.ArrayList;

public class Winery {

    private long id;
    private String name;
    private ArrayList<Wine> wines;

    public Winery(final long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return this.id;
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

    @Override
    public String toString() {
        return this.name;
    }
}