package com.robcutmore.vinotes;

import java.util.ArrayList;
import java.util.Date;


public class TastingNote {

    private String url;
    private Date tasted;
    private Wine wine;
    private ArrayList<NoteTrait> colorTraits;
    private ArrayList<NoteTrait> noseTraits;
    private ArrayList<NoteTrait> tasteTraits;
    private ArrayList<NoteTrait> finishTraits;
    private Integer rating;

    public TastingNote(final Wine wine) {
        this.url = null;
        this.tasted = null;
        this.wine = wine;
        this.colorTraits = new ArrayList<>();
        this.noseTraits = new ArrayList<>();
        this.tasteTraits = new ArrayList<>();
        this.finishTraits = new ArrayList<>();
        this.rating = null;
    }

    public String getUrl() {
        return this.url;
    }

    public Date getTasted() {
        return this.tasted;
    }

    public void setTasted(final Date tasted) {
        this.tasted = tasted;
    }

    public Wine getWine() {
        return this.wine;
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
}