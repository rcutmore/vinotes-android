package com.robcutmore.vinotes.model;

import java.util.HashMap;


public class Winery {

    private Long id;
    private String name;
    private HashMap<Long, Wine> wines = new HashMap<>();

    public Winery(final String name) {
        this.name = name;
        this.id = null;
    }

    public Winery(final long id, final String name) {
        this(name);
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final long id) {
        // Only allow id to be set once.
        this.id = this.id == null ? id : this.id;
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
        boolean isFound = existingWine != null;

        // Make sure wine has an id set.
        boolean hasId = wine.getId() != null;

        if (!isFound && hasId) {
            this.wines.put(wine.getId(), wine);
        }
    }

    @Override
    public String toString() {
        return this.name;
    }

}