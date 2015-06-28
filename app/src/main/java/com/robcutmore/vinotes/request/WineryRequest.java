package com.robcutmore.vinotes.request;


import com.robcutmore.vinotes.model.Winery;


public final class WineryRequest {

    public static Winery get(final long id) {
        String response = sendGET(id);
        Winery[] wineries = parseResponse(response);
        return (wineries.length > 0) ? wineries[0] : null;
    }

    public static Winery[] getAll() {
        String response = sendGET();
        Winery[] wineries = parseResponse(response);
        return wineries;
    }

    private static String sendGET(final Long id) {
        // Test stub, replace with request to API.
        return "";
    }

    private static String sendGET() {
        return sendGET(null);
    }

    private static Winery[] parseResponse(final String response) {
        // Test stub, replace with code to parse JSON response.
        Winery[] wineries = {
            new Winery(1, "Test 1"),
            new Winery(2, "Test 2")
        };
        return wineries;
    }

}