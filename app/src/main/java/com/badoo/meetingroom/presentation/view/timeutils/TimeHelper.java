package com.badoo.meetingroom.presentation.view.timeutils;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.widget.LinearLayout.LayoutParams;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by yaozhong on 10/12/2016.
 */

public class TimeHelper {

    public static String getCurrentDateAndWeek(Context context) {
        return DateUtils.formatDateTime(context, getCurrentTimeInMillis(), DateUtils.FORMAT_ABBREV_MONTH| DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY);
    }



    public static long getCurrentTimeInMillis() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static String getCurrentTimeInMillisInText() {
        return formatTime(getCurrentTimeInMillis());
    }

    public static String formatTime(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(millis));
    }


    public static String formatDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
        Date date = new Date(millis);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        date = c.getTime();
        return sdf.format(date);
    }

    public static String formatMillisInMinsAndSecs(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
         return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    public static String formatMillisInHrsAndMins(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
    }

    public static long getCurrentTimeSinceMidNight() {
        Calendar now = Calendar.getInstance();
        Calendar midnight = Calendar.getInstance();

        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);

        return now.getTimeInMillis() - midnight.getTimeInMillis();
    }
    public static long getMidNightTimeOfDay(int i) {

        Calendar date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        date.add(Calendar.DAY_OF_MONTH, i);

        return date.getTimeInMillis();
    }

    public static boolean isMidNight(long milliseconds){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        return calendar.get(Calendar.HOUR_OF_DAY) == 0
            && calendar.get(Calendar.MINUTE) == 0
            && calendar.get(Calendar.SECOND) == 0
            && calendar.get(Calendar.MILLISECOND) == 0;
    }
}
