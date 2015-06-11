package com.robcutmore.vinotes;

import java.util.Date;


public final class DateUtils {

    public static Long convertDateToTimestamp(Date date) {
        Long timestamp;
        if (date != null) {
            timestamp = date.getTime();
        } else {
            timestamp = null;
        }
        return timestamp;
    }

    public static Date convertTimestampToDate(Long timestamp) {
        Date date;
        if (timestamp != null) {
            date = new Date(timestamp);
        } else {
            date = null;
        }
        return date;
    }

}
