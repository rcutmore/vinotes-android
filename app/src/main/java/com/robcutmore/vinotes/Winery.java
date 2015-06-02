package com.robcutmore.vinotes;

import java.util.HashMap;


public class Winery {

    private long id;
    private String name;
    private HashMap<Long, Wine> wines;

    public Winery(final long id, final String name) {
        this.id = id;
        this.name = name;
        this.wines = new HashMap<>();
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public HashMap<Long, Wine> getWines() {
        return this.wines;
    }

    public void setWines(final HashMap<Long, Wine> wines) {
        this.wines = wines;
    }

    public void addWine(final Wine wine) {
        // Add new wine only if it's not already in wines.
        Wine existingWine = this.wines.get(wine.getId());
        if (existingWine == null) {
            this.wines.put(wine.getId(), wine);
        }
    }

    @Override
    public String toString() {
        return this.name;
    }
}