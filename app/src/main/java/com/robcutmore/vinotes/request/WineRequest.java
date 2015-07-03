package com.robcutmore.vinotes.request;


import com.robcutmore.vinotes.model.Wine;
import com.robcutmore.vinotes.model.Winery;


public class WineRequest {

    public static Wine add(final long wineryId, final String name, final int vintage) {
        String response = sendPOST(wineryId, name, vintage);
        Wine[] wines = parseResponse(response);
        return (wines.length > 0) ? wines[0] : null;
    }

    public static Wine get(final long id) {
        String response = sendGET(id);
        Wine[] wines = parseResponse(response);
        return (wines.length > 0) ? wines[0] : null;
    }

    public static Wine[] getAll() {
        String response = sendGET();
        Wine[] wines = parseResponse(response);
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

    private static Wine[] parseResponse(final String response) {
        // Test stub, replace with code to parse JSON response.
        Winery firstWinery = new Winery(1, "Test Winery 1");
        Winery secondWinery = new Winery(2, "Test Winery 2");
        Wine[] wines = new Wine[] {
            new Wine(1, firstWinery, "Test Wine 1", 2011),
            new Wine(2, firstWinery, "Test Wine 2", 2012),
            new Wine(3, secondWinery, "Test Wine 3", 2014)
        };
        return wines;
    }

}