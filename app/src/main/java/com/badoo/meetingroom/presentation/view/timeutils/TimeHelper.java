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

    public static String formatMillisInMinAndSec(long millis) {
        return String.format(Locale.getDefault(), "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(millis),
            TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

    public static String formatMillisInHrAndMin(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
    }


    public static String formatMillisInMin(long millis) {
        return String.format(Locale.getDefault(), "%02d",
            TimeUnit.MILLISECONDS.toMinutes(millis));
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

    public static long min2Millis(int min) {
        return min * 60 * 1000;
    }

    public static long hr2Millis(int hr) {return hr * min2Millis(60); }

    public static boolean isSameTimeIgnoreSec(long time1, long time2){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(time1);
        cal2.setTimeInMillis(time2);
        return cal1.get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY) &&
            cal1.get(Calendar.MINUTE) == cal2.get(Calendar.MINUTE);
    }

    public static int getHour(long millis) {
        Date date = new Date(millis);   // given date
        Calendar calendar = Calendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMin(long millis) {
        Date date = new Date(millis);   // given date
        Calendar calendar = Calendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date
        return calendar.get(Calendar.MINUTE);
    }

    public static int getSec(long millis) {
        Date date = new Date(millis);   // given date
        Calendar calendar = Calendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date
        return calendar.get(Calendar.SECOND);
    }

    public static long dropMillis(long millis) {
        return 1000 * (millis/ 1000);
    }

    public static long dropSeconds(long millis) {
        return 60 * 1000 * (millis / (60 * 1000));
    }

    public static long sec2Millis(int i) {
        return i * 1000;
    }
}
