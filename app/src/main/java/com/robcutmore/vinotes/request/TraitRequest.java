package com.robcutmore.vinotes.request;


import com.robcutmore.vinotes.model.Trait;

import java.util.ArrayList;


public final class TraitRequest {

    public static Trait add(final String name) {
        String response = sendPOST(name);
        ArrayList<Trait> traits = parseResponse(response);
        return (traits.size() > 0) ? traits.get(0) : null;
    }

    public static Trait get(final long id) {
        String response = sendGET(id);
        ArrayList<Trait> traits = parseResponse(response);
        return (traits.size() > 0) ? traits.get(0) : null;
    }

    public static ArrayList<Trait> getAll() {
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

    private static ArrayList<Trait> parseResponse(final String response) {
        // Test stub, replace with code to parse JSON response.
        ArrayList<Trait> traits = new ArrayList<>();
        traits.add(new Trait(1, "Oaky"));
        traits.add(new Trait(2, "Fruity"));
        traits.add(new Trait(3, "Petrol"));
        traits.add(new Trait(4, "Plum"));
        traits.add(new Trait(5, "Cherry"));
        traits.add(new Trait(6, "Peach"));
        traits.add(new Trait(7, "Pineapple"));
        traits.add(new Trait(8, "Garnet"));
        traits.add(new Trait(9, "Ruby"));
        traits.add(new Trait(10, "Pale"));
        return traits;
    }

}
