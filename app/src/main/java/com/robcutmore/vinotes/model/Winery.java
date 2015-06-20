package com.robcutmore.vinotes.model;


public class Winery {

    private Long id;
    private String name;

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

    @Override
    public String toString() {
        return this.name;
    }

}