package com.robcutmore.vinotes.request;


import com.robcutmore.vinotes.models.Note;
import com.robcutmore.vinotes.models.Wine;
import com.robcutmore.vinotes.models.Winery;
import com.robcutmore.vinotes.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;


public class NoteRequest {

    public static Note add(final Note noteToAdd) {
        //String response = sendPOST(wineId, tasted, rating);
        //ArrayList<Note> notes = parseResponse(response);
        //return (notes.size() > 0) ? notes.get(0) : null;

        // Test stub, remove this and uncomment code above.
        try {
            // Simulate time for network request.
            Thread.sleep(5000);
        } catch (Exception e) {
            // Just for test purposes so handling error doesn't matter.
        }
        noteToAdd.setId(4);
        return noteToAdd;
    }

    public static Note get(final long id) {
        String response = sendGET(id);
        ArrayList<Note> notes = parseResponse(response);
        return (notes.size() > 0) ? notes.get(0) : null;
    }

    public static ArrayList<Note> getAll() {
        String response = sendGET();
        ArrayList<Note> notes = parseResponse(response);
        return notes;
    }

    public static Note update(final Note note) {
        //String response = sendPUT(wine);
        //ArrayList<Note> notes = parseResponse(response);
        //return (notes.size() > 0) ? notes.get(0) : null;

        // Test stub, remove this and uncomment code above.
        try {
            // Simulate time for network request.
            Thread.sleep(5000);
        } catch (Exception e) {
            // Just for test purposes so handling error doesn't matter.
        }
        return note;
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

    private static String sendPUT(final Wine wine) {
        // Test stub, replace with request to API.
        return "";
    }

    private static ArrayList<Note> parseResponse(final String response) {
        // Test stub, replace with code to parse JSON request.
        Winery firstWinery = new Winery(1, "Test 1");
        Winery secondWinery = new Winery(2, "Test 2");
        Wine firstWine = new Wine(1, firstWinery, "Reisling", 2011);
        Wine secondWine = new Wine(2, firstWinery, "Merlot", 2012);
        Wine thirdWine = new Wine(3, secondWinery, "Baco Noir", 2014);
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note(3, thirdWine, DateUtils.parseDateFromString("2012/3/5"), 2));
        notes.add(new Note(2, secondWine, DateUtils.parseDateFromString("2011/10/1"), 3));
        notes.add(new Note(1, firstWine, DateUtils.parseDateFromString("2010/5/13"), 5));
        return notes;
    }

}
