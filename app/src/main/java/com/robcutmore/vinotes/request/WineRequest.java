package com.robcutmore.vinotes.request;


import com.robcutmore.vinotes.model.Wine;
import com.robcutmore.vinotes.model.Winery;

import java.util.ArrayList;


public class WineRequest {

    public static Wine add(final Wine wineToAdd) {
        //String response = sendPOST(wineryId, name, vintage);
        //ArrayList<Wine> wines = parseResponse(response);
        //return (wines.size() > 0) ? wines.get(0) : null;

        // Test stub, remove this and uncomment code above.
        wineToAdd.setId(4);
        return wineToAdd;
    }

    public static Wine get(final long id) {
        String response = sendGET(id);
        ArrayList<Wine> wines = parseResponse(response);
        return (wines.size() > 0) ? wines.get(0) : null;
    }

    public static ArrayList<Wine> getAll() {
        String response = sendGET();
        ArrayList<Wine> wines = parseResponse(response);
        return wines;
    }

    private static String sendGET(final Long id) {
        // Test stub, replace with request to API.
        return "";
    }

    private static String sendGET() {
        return sendGET(null);
    }

    private static String sendPOST(final long wineryId, final String name, final int vintage) {
        // Test stub, replace with request to API.
        return "";
    }

    private static ArrayList<Wine> parseResponse(final String response) {
        // Test stub, replace with code to parse JSON response.
        Winery firstWinery = new Winery(1, "Test 1");
        Winery secondWinery = new Winery(2, "Test 2");
        ArrayList<Wine> wines = new ArrayList<>();
        wines.add(new Wine(1, firstWinery, "Reisling", 2011));
        wines.add(new Wine(2, firstWinery, "Merlot", 2012));
        wines.add(new Wine(3, secondWinery, "Baco Noir", 2014));
        return wines;
    }

}
