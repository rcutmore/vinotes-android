package com.robcutmore.vinotes.model;

import java.util.Date;
import java.util.HashMap;


public class TastingNote {

    private Long id = null;
    private Wine wine;
    private Date tasted = null;
    private HashMap<Long, NoteTrait> colorTraits = new HashMap<>();
    private HashMap<Long, NoteTrait> noseTraits = new HashMap<>();
    private HashMap<Long, NoteTrait> tasteTraits = new HashMap<>();
    private HashMap<Long, NoteTrait> finishTraits = new HashMap<>();
    private Integer rating = null;

    public TastingNote(final Wine wine) {
        this.wine = wine;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final long id) {
        // Only allow id to be set once.
        this.id = this.id == null ? id : this.id;
    }

    public Wine getWine() {
        return this.wine;
    }

    public Date getTasted() {
        return this.tasted;
    }

    public void setTasted(final Date tasted) {
        this.tasted = tasted;
    }

    public HashMap<Long, NoteTrait> getColorTraits() {
        return this.colorTraits;
    }

    public void setColorTraits(final HashMap<Long, NoteTrait> colorTraits) {
        this.colorTraits = colorTraits;
    }

    public HashMap<Long, NoteTrait> getNoseTraits() {
        return this.noseTraits;
    }

    public void setNoseTraits(final HashMap<Long, NoteTrait> noseTraits) {
        this.noseTraits = noseTraits;
    }

    public HashMap<Long, NoteTrait> getTasteTraits() {
        return this.tasteTraits;
    }

    public void setTasteTraits(final HashMap<Long, NoteTrait> tasteTraits) {
        this.tasteTraits = tasteTraits;
    }

    public HashMap<Long, NoteTrait> getFinishTraits() {
        return this.finishTraits;
    }

    public void setFinishTraits(final HashMap<Long, NoteTrait> finishTraits) {
        this.finishTraits = finishTraits;
    }

    public int getRating() {
        return this.rating;
    }

    public void setRating(final int rating) {
        final int minRating = 1;
        final int maxRating = 5;

        if (rating < minRating) {
            this.rating = minRating;
        } else if (rating > maxRating) {
            this.rating = maxRating;
        } else {
            this.rating = rating;
        }
    }

    @Override
    public String toString() {
        return String.format("%s\nRating: %d", this.wine.toString(), this.rating);
    }

}