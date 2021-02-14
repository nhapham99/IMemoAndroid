package com.lnb.imemo.Utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateHelper {
    private static final String TAG = "DateHelper";
    public static String dateConverter(String date) {
        Date dateTime = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            dateTime = df.parse(date.split("\\.")[0]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        df.setTimeZone(TimeZone.getDefault());
        String formatTime = df.format(dateTime);
        Log.d(TAG, "dateConverter: " + formatTime);
        try {
            dateTime = df.parse(formatTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        DateFormat timeFormatter = new SimpleDateFormat("HH:mm");

        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            return "Today at " + timeFormatter.format(dateTime);
        } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
            return "Yesterday at " + timeFormatter.format(dateTime);
        } else {
            return date;
        }
    }


    public static String convertDate(String date) {
        if (date.equals("Monday")) {
            return "T2";
        } else if (date.equals("Tuesday")) {
            return "T3";
        } else if (date.equals("Wednesday")) {
            return "T4";
        } else if (date.equals("Thursday")) {
            return "T5";
        } else if (date.equals("Friday")) {
            return "T6";
        } else if (date.equals("Saturday")) {
            return "T7";
        } else if (date.equals("Sunday")) {
            return "CN";
        }
        return "Error";
    }
}
