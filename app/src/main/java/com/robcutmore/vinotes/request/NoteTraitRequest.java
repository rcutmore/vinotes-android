package com.robcutmore.vinotes.request;


import com.robcutmore.vinotes.model.NoteTrait;

import java.util.ArrayList;


public final class NoteTraitRequest {

    public static NoteTrait add(final String name) {
        String response = sendPOST(name);
        ArrayList<NoteTrait> traits = parseResponse(response);
        return (traits.size() > 0) ? traits.get(0) : null;
    }

    public static NoteTrait get(final long id) {
        String response = sendGET(id);
        ArrayList<NoteTrait> traits = parseResponse(response);
        return (traits.size() > 0) ? traits.get(0) : null;
    }

    public static ArrayList<NoteTrait> getAll() {
        String response = sendGET();
        return parseResponse(response);
    }

    private static String sendGET(final Long id) {
        // Test stub, replace with request to API.
        return "";
    }

    private static String sendGET() {
        return sendGET(null);
    }

    private static String sendPOST(final String name) {
        // Test stub, replace with request to API.
        return "";
    }

    private static ArrayList<NoteTrait> parseResponse(final String response) {
        // Test stub, replace with code to parse JSON response.
        ArrayList<NoteTrait> traits = new ArrayList<>();
        traits.add(new NoteTrait(1, "Trait 1"));
        traits.add(new NoteTrait(2, "Trait 2"));
        return traits;
    }

}
