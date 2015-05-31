package com.robcutmore.vinotes;

import java.util.ArrayList;
import java.util.Date;


public class TastingNote {

    private long id;
    private Wine wine;
    private Date tasted;
    private ArrayList<NoteTrait> colorTraits;
    private ArrayList<NoteTrait> noseTraits;
    private ArrayList<NoteTrait> tasteTraits;
    private ArrayList<NoteTrait> finishTraits;
    private Integer rating;

    public TastingNote(final long id, final Wine wine) {
        this.id = id;
        this.wine = wine;
        this.tasted = null;
        this.colorTraits = new ArrayList<>();
        this.noseTraits = new ArrayList<>();
        this.tasteTraits = new ArrayList<>();
        this.finishTraits = new ArrayList<>();
        this.rating = null;
    }

    public long getId() {
        return this.id;
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

    public ArrayList<NoteTrait> getColorTraits() {
        return this.colorTraits;
    }

    public void setColorTraits(final ArrayList<NoteTrait> colorTraits) {
        this.colorTraits = colorTraits;
    }

    public ArrayList<NoteTrait> getNoseTraits() {
        return this.noseTraits;
    }

    public void setNoseTraits(final ArrayList<NoteTrait> noseTraits) {
        this.noseTraits = noseTraits;
    }

    public ArrayList<NoteTrait> getTasteTraits() {
        return this.tasteTraits;
    }

    public void setTasteTraits(final ArrayList<NoteTrait> tasteTraits) {
        this.tasteTraits = tasteTraits;
    }

    public ArrayList<NoteTrait> getFinishTraits() {
        return this.finishTraits;
    }

    public void setFinishTraits(final ArrayList<NoteTrait> finishTraits) {
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