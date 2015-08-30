package com.robcutmore.vinotes.model;


import java.util.Date;
import java.util.HashMap;


public class Note {

    private Long id;
    private Wine wine;
    private Date tasted;
    private HashMap<Long, Trait> colorTraits = new HashMap<>();
    private HashMap<Long, Trait> noseTraits = new HashMap<>();
    private HashMap<Long, Trait> tasteTraits = new HashMap<>();
    private HashMap<Long, Trait> finishTraits = new HashMap<>();
    private Integer rating;

    public Note(final Wine wine) {
        this.wine = wine;
        this.id = null;
        this.tasted = null;
        this.rating = null;
    }

    public Note(final long id, final Wine wine) {
        this(wine);
        this.id = id;
    }

    public Note(final long id, final Wine wine, final Date tasted, final Integer rating) {
        this(id, wine);
        this.tasted = tasted;
        this.rating = rating;
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

    public HashMap<Long, Trait> getColorTraits() {
        return this.colorTraits;
    }

    public void setColorTraits(final HashMap<Long, Trait> colorTraits) {
        this.colorTraits = colorTraits;
    }

    public HashMap<Long, Trait> getNoseTraits() {
        return this.noseTraits;
    }

    public void setNoseTraits(final HashMap<Long, Trait> noseTraits) {
        this.noseTraits = noseTraits;
    }

    public HashMap<Long, Trait> getTasteTraits() {
        return this.tasteTraits;
    }

    public void setTasteTraits(final HashMap<Long, Trait> tasteTraits) {
        this.tasteTraits = tasteTraits;
    }

    public HashMap<Long, Trait> getFinishTraits() {
        return this.finishTraits;
    }

    public void setFinishTraits(final HashMap<Long, Trait> finishTraits) {
        this.finishTraits = finishTraits;
    }

    public Integer getRating() {
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
