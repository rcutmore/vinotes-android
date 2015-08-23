package com.robcutmore.vinotes.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * DateUtils contains miscellaneous date utility functions.
 */
public final class DateUtils {

    /**
     * Converts the given date to a timestamp.
     *
     * @param date  date to convert to a timestamp
     * @return the long representation of the timestamp for given date
     */
    public static Long convertDateToTimestamp(Date date) {
        return (date != null) ? date.getTime() : null;
    }

    /**
     * Converts given timestamp to a date.
     *
     * @param timestamp  long to convert to date
     * @return the date for given timestamp
     */
    public static Date convertTimestampToDate(Long timestamp) {
        return (timestamp != null) ? new Date(timestamp) : null;
    }

    /**
     * Converts given date to a string.
     *
     * @param date  date to convert to string
     * @return the string representation of given date or empty string if date is null
     */
    public static String convertDateToString(final Date date) {
        String dateText;
        if (date != null) {
            // Use calendar object to get year, month, and day from date.
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            dateText = String.format("%d/%d/%d", year, month, day);
        } else {
            dateText = "";
        }
        return dateText;
    }

    /**
     * Parses date from given string.
     *
     * @param dateString  string to parse
     * @return the date parsed from given string, or null if invalid
     */
    public static Date parseDateFromString(final String dateString) {
        Date date;
        try {
            date = new SimpleDateFormat("yyyy/M/d").parse(dateString);
        } catch (ParseException e) {
            date = null;
        }
        return date;
    }

}