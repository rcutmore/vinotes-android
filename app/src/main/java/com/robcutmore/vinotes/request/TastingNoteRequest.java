package com.robcutmore.vinotes.request;


import com.robcutmore.vinotes.model.TastingNote;
import com.robcutmore.vinotes.model.Wine;
import com.robcutmore.vinotes.model.Winery;

import java.util.Date;


public class TastingNoteRequest {

    public static TastingNote add(final long wineId, final Date tasted, final Integer rating) {
        String response = sendPOST(wineId, tasted, rating);
        TastingNote[] notes = parseResponse(response);
        return (notes.length > 0) ? notes[0] : null;
    }

    public static TastingNote get(final long id) {
        String response = sendGET(id);
        TastingNote[] notes = parseResponse(response);
        return (notes.length > 0) ? notes[0] : null;
    }

    public static TastingNote[] getAll() {
        String response = sendGET();
        TastingNote[] notes = parseResponse(response);
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

    private static TastingNote[] parseResponse(final String response) {
        // Test stub, replace with code to parse JSON request.
        Winery winery = new Winery(1, "Test Winery");
        Wine firstWine = new Wine(1, winery, "Test 1", 2012);
        Wine secondWine = new Wine(2, winery, "Test 2", 2013);
        TastingNote[] notes = {
            new TastingNote(1, firstWine),
            new TastingNote(2, secondWine)
        };
        return notes;
    }

}