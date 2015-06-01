package com.robcutmore.vinotes;


public class NoteTrait {

    private long id;
    private String name;

    public NoteTrait(final long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}