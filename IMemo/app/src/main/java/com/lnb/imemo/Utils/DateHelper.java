package com.lnb.imemo.Utils;

import android.util.Log;

import androidx.annotation.LongDef;

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
        Calendar then = Calendar.getInstance();
        try {
            then.setTime(df.parse(formatTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeAgoInWords(then.getTime());
    }

    public static String timeAgoInWords(Date from) {
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd 'tháng' mm 'năm' yyyy");
        long difference = now.getTime() - from.getTime();
        long distanceInMin = difference / 60000;

        if ( 0 <= distanceInMin && distanceInMin <= 1 ) {
            return "Vừa xong";
        } else if ( 1 <= distanceInMin && distanceInMin <= 45 ) {
            return distanceInMin + " phút trước";
        } else if ( 45 <= distanceInMin && distanceInMin <= 89 ) {
            return "1 giờ trước";
        } else if ( 90 <= distanceInMin && distanceInMin <= 1439 ) {
            return (distanceInMin / 60) + " giờ trước";
        } else if ( 1440 <= distanceInMin && distanceInMin <= 2529 ) {
            return "1 Ngày trước";
        } else if ( 2530 <= distanceInMin && distanceInMin <= 43199 ) {
            return (distanceInMin / 1440) + " ngày trước | " + simpleDateFormat.format(from);
        } else if ( 43200 <= distanceInMin && distanceInMin <= 86399 ) {
            return "1 tháng trước | " + simpleDateFormat.format(from);
        } else if ( 86400 <= distanceInMin && distanceInMin <= 525599 ) {
            return (distanceInMin / 43200) + " tháng trước | " + simpleDateFormat.format(from);
        } else {
            long distanceInYears = distanceInMin / 525600;
            return "Khoảng " + distanceInYears + " năm trước | " + simpleDateFormat.format(from);
        }
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
