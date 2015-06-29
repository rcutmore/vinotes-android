package com.robcutmore.vinotes.request;


import com.robcutmore.vinotes.model.NoteTrait;


public final class NoteTraitRequest {

    public static NoteTrait get(final long id) {
        String response = sendGET(id);
        NoteTrait[] traits = parseResponse(response);
        return (traits.length > 0) ? traits[0] : null;
    }

    public static NoteTrait[] getAll() {
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

    private static NoteTrait[] parseResponse(final String response) {
        // Test stub, replace with code to parse JSON response.
        NoteTrait[] traits = {
            new NoteTrait(1, "Trait 1"),
            new NoteTrait(2, "Trait 2")
        };
        return traits;
    }

}