package com.robcutmore.vinotes.utils;

import java.util.Date;


public final class DateUtils {

    public static Long convertDateToTimestamp(Date date) {
        return (date != null) ? date.getTime() : null;
    }

    public static Date convertTimestampToDate(Long timestamp) {
        return (timestamp != null) ? new Date(timestamp) : null;
    }

}