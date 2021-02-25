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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy | HH:mm");
        return simpleDateFormat.format(dateTime);
    }


    public static String convertDate(String date) {
        Log.d(TAG, "convertDate: " + date);
        if (date.equals("Monday") || date.equals("Thứ Hai")) {
            return "T2";
        } else if (date.equals("Tuesday") || date.equals("Thứ Ba")) {
            return "T3";
        } else if (date.equals("Wednesday") || date.equals("Thứ Tư")) {
            return "T4";
        } else if (date.equals("Thursday") || date.equals("Thứ Năm")) {
            return "T5";
        } else if (date.equals("Friday") || date.equals("Thứ Sáu")) {
            return "T6";
        } else if (date.equals("Saturday") || date.equals("Thứ Bảy")) {
            return "T7";
        } else if (date.equals("Sunday") || date.equals("Chủ Nhật")) {
            return "CN";
        }
        return "Error";
    }
}
