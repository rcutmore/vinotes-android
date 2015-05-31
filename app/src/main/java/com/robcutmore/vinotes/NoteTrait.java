package com.robcutmore.vinotes;


public class NoteTrait {

    private String url;
    private String name;

    public NoteTrait(final String name) {
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

    @Override
    public String toString() {
        return this.name;
    }
}
