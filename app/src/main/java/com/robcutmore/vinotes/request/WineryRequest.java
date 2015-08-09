package com.robcutmore.vinotes.request;


import com.robcutmore.vinotes.model.Winery;

import java.util.ArrayList;


public final class WineryRequest {

    public static Winery add(final String name) {
        String response = sendPOST(name);
        ArrayList<Winery> wineries = parseResponse(response);
        return (wineries.size() > 0) ? wineries.get(0) : null;
    }

    public static Winery get(final long id) {
        String response = sendGET(id);
        ArrayList<Winery> wineries = parseResponse(response);
        return (wineries.size() > 0) ? wineries.get(0) : null;
    }

    public static ArrayList<Winery> getAll() {
        String response = sendGET();
        ArrayList<Winery> wineries = parseResponse(response);
        return wineries;
    }

    private static String sendGET(final Long id) {
        // Test stub, replace with request to API.
        return "";
    }

    private static String sendGET() {
        return sendGET(null);
    }

    private static String sendPOST(final String name) {
        return "";
    }

    private static ArrayList<Winery> parseResponse(final String response) {
        // Test stub, replace with code to parse JSON response.
        ArrayList<Winery> wineries = new ArrayList<>();
        wineries.add(new Winery(1, "Test 1"));
        wineries.add(new Winery(2, "Test 2"));
        wineries.add(new Winery(3, "Test 3"));
        wineries.add(new Winery(4, "Test 4"));
        wineries.add(new Winery(5, "Test 5"));
        wineries.add(new Winery(6, "Test 6"));
        wineries.add(new Winery(7, "Test 7"));
        wineries.add(new Winery(8, "Test 8"));
        wineries.add(new Winery(9, "Test 9"));
        wineries.add(new Winery(10, "Test 10"));
        wineries.add(new Winery(11, "Test 11"));
        wineries.add(new Winery(12, "Test 12"));
        wineries.add(new Winery(13, "Test 13"));
        return wineries;
    }

}
