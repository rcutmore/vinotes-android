package com.robcutmore.vinotes.model;


import com.robcutmore.vinotes.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;


/**
 * Represents a wine tasting note.
 */
public class Note {

    private Long id;
    private Wine wine;
    private Date tasted;
    private ArrayList<Trait> colorTraits = new ArrayList<>();
    private ArrayList<Trait> noseTraits = new ArrayList<>();
    private ArrayList<Trait> tasteTraits = new ArrayList<>();
    private ArrayList<Trait> finishTraits = new ArrayList<>();
    private Integer rating;

    /**
     * Constructor.
     *
     * @param wine  wine of note
     */
    public Note(final Wine wine) {
        this.wine = wine;
        this.id = null;
        this.tasted = null;
        this.rating = null;
    }

    /**
     * Constructor.
     *
     * @param id  ID of note
     * @param wine  wine of note
     */
    public Note(final long id, final Wine wine) {
        this(wine);
        this.id = id;
    }

    /**
     * Constructor.
     *
     * @param id  ID of note
     * @param wine  wine of note
     * @param tasted  tasting date of note
     * @param rating  rating of wine
     */
    public Note(final long id, final Wine wine, final Date tasted, final Integer rating) {
        this(id, wine);
        this.tasted = tasted;
        this.rating = rating;
    }

    /**
     * Gets ID of tasting note.
     *
     * @return ID of note (null if not set)
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Sets ID of tasting note.
     * ID can only be set once so subsequent attempts to set ID will default to first ID set.
     *
     * @param id  ID to set for note
     */
    public void setId(final long id) {
        // Only allow id to be set once.
        this.id = this.id == null ? id : this.id;
    }

    /**
     * Gets wine of tasting note.
     *
     * @return wine
     */
    public Wine getWine() {
        return this.wine;
    }

    /**
     * Gets date of tasting note.
     *
     * @return tasting date
     */
    public Date getTasted() {
        return this.tasted;
    }

    /**
     * Sets tasting date.
     *
     * @param tasted  tasting date to set
     */
    public void setTasted(final Date tasted) {
        this.tasted = tasted;
    }

    /**
     * Gets list of color traits of wine.
     *
     * @return list of color traits
     */
    public ArrayList<Trait> getColorTraits() {
        return this.colorTraits;
    }

    /**
     * Sets color traits of wine.
     *
     * @param colorTraits  list of color traits to set
     */
    public void setColorTraits(final ArrayList<Trait> colorTraits) {
        this.colorTraits = colorTraits;
    }

    /**
     * Gets list of nose traits of wine.
     *
     * @return list of nose traits
     */
    public ArrayList<Trait> getNoseTraits() {
        return this.noseTraits;
    }

    /**
     * Sets nose traits of wine.
     *
     * @param noseTraits  list of note traits to set
     */
    public void setNoseTraits(final ArrayList<Trait> noseTraits) {
        this.noseTraits = noseTraits;
    }

    /**
     * Gets list of taste traits of wine.
     *
     * @return list of taste traits
     */
    public ArrayList<Trait> getTasteTraits() {
        return this.tasteTraits;
    }

    /**
     * Sets taste traits of wine.
     *
     * @param tasteTraits  list of taste traits to set
     */
    public void setTasteTraits(final ArrayList<Trait> tasteTraits) {
        this.tasteTraits = tasteTraits;
    }

    /**
     * Gets list of finish traits of wine.
     *
     * @return list of finish traits
     */
    public ArrayList<Trait> getFinishTraits() {
        return this.finishTraits;
    }

    /**
     * Sets finish traits of wine.
     *
     * @param finishTraits  list of finish traits to set
     */
    public void setFinishTraits(final ArrayList<Trait> finishTraits) {
        this.finishTraits = finishTraits;
    }

    /**
     * Gets rating of wine.
     *
     * @return rating of wine
     */
    public Integer getRating() {
        return this.rating;
    }

    /**
     * Sets rating of wine.
     * Rating set to min (1) or max (5) if input is outside this range.
     *
     * @param rating  rating to set
     */
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

    /**
     * Gets string representation of tasting note.
     *
     * @return string representation of note
     */
    @Override
    public String toString() {
        String tastedDate = DateUtils.convertDateToString(this.tasted);
        return String.format("%s\n%d stars on %s", this.wine.toString(), this.rating, tastedDate);
    }

}
