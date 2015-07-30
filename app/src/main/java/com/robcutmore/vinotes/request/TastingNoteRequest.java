package com.robcutmore.vinotes.request;


import com.robcutmore.vinotes.model.TastingNote;
import com.robcutmore.vinotes.model.Wine;
import com.robcutmore.vinotes.model.Winery;

import java.util.ArrayList;
import java.util.Date;


public class TastingNoteRequest {

    public static TastingNote add(final long wineId, final Date tasted, final Integer rating) {
        String response = sendPOST(wineId, tasted, rating);
        ArrayList<TastingNote> notes = parseResponse(response);
        return (notes.size() > 0) ? notes.get(0) : null;
    }

    public static TastingNote get(final long id) {
        String response = sendGET(id);
        ArrayList<TastingNote> notes = parseResponse(response);
        return (notes.size() > 0) ? notes.get(0) : null;
    }

    public static ArrayList<TastingNote> getAll() {
        String response = sendGET();
        ArrayList<TastingNote> notes = parseResponse(response);
        return notes;
    }

    private static String sendGET(final Long id) {
        // Test stub, replace with request to API.
        return "";
    }

    private static String sendGET() {
        return sendGET(null);
    }

    private static String sendPOST(final long wineId, final Date tasted, final Integer rating) {
        // Test stub, replace with request to API.
        return "";
    }

    private static ArrayList<TastingNote> parseResponse(final String response) {
        // Test stub, replace with code to parse JSON request.
        Winery winery = new Winery(1, "Test Winery");
        Wine firstWine = new Wine(1, winery, "Test 1", 2012);
        Wine secondWine = new Wine(2, winery, "Test 2", 2013);
        ArrayList<TastingNote> notes = new ArrayList<>();
        notes.add(new TastingNote(1, firstWine));
        notes.add(new TastingNote(2, secondWine));
        return notes;
    }

}
