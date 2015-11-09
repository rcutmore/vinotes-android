package com.robcutmore.vinotes.fragments;


import android.app.Fragment;
import android.os.Bundle;

import com.robcutmore.vinotes.model.Wine;
import com.robcutmore.vinotes.model.Winery;

import java.util.Date;


/**
 * RetainedNoteFragment is used to persist tasting note data.
 * It can store the tasting date, selected winery, selected wine, and rating for a tasting note.
 * It can be used by an activity to store and recover tasting note data as needed, such as when the
 * activity is destroyed and recreated due to the normal activity lifecycle.
 */
public class RetainedNoteFragment extends Fragment {

    // Note data to retain.
    private Date tastingDate;
    private Winery winery;
    private Wine wine;
    private Integer rating;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setTastingDate(final Date tastingDate) {
        this.tastingDate = tastingDate;
    }

    public Date getTastingDate() {
        return this.tastingDate;
    }

    public void setWinery(final Winery winery) {
        this.winery = winery;
    }

    public Winery getWinery() {
        return this.winery;
    }

    public void setWine(final Wine wine) {
        this.wine = wine;
    }

    public Wine getWine() {
        return this.wine;
    }

    public void setRating(final Integer rating) {
        this.rating = rating;
    }

    public Integer getRating() {
        return this.rating;
    }

}
