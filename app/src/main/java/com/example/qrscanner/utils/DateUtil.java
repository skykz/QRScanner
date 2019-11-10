package com.example.qrscanner.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static String format(String date, String format) {
        Date dt = DateUtil.stringToDateTime(date);

        return DateUtil.dateToString(dt, format);
    }

    public static String format(String date) {
        Date dt = DateUtil.stringToDateTime(date);

        return DateUtil.dateToString(dt, "dd/MM/yy HH:mm");
    }

    /**
     * Convert string to date.
     *
     * @param date String to convert with format yyyy-MM-dd
     *
     * @return Date
     */
    public static Date stringToDate(String date) {
        if (date == null || date.equals("")) return null;

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date today	  = null;

        try {
            today = df.parse(date);
        } catch (ParseException e) {}

        return today;
    }

    /**
     * Convert string to date time.
     *
     * @param date String to convert with format yyyy-MM-dd HH:mm
     *
     * @return Date time
     */
    public static Date stringToDateTime(String date) {
        if (date == null || date.equals("")) return null;

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date today	  = null;

        try {
            today = df.parse(date);
        } catch (ParseException e) {}

        return today;
    }

    /**
     * Convert date to string.
     *
     * @param date Date to convert
     * @param format Date format
     *
     * @return String of date
     */
    public static String dateToString(Date date, String format) {
        DateFormat df = new SimpleDateFormat(format);

        return df.format(date);
    }

    /**
     * Convert date to string with default format yyyy-MM-dd
     *
     * @param date Date to convert
     *
     * @return String of date.
     */
    public static String dateToString(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        return df.format(date);
    }

    /*
     * Convert string time to milliseconds log
     *
     * @param time String to convert with format yyyy-MM-dd HH:mm:ss
     *
     * @return milliseconds
     */
    public static long timeStringToMilis(String time) {
        long milis = 0;

        try {
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date 	= sd.parse(time);
            milis 		= date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return milis;
    }

    /**
     * Convert time in milliseconds to string with format yyyy-MM-dd HH:mm:ss.
     *
     * @param milis Milliseconds
     *
     * @return String of time
     */
    public static String timeMilisToString(long milis) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar   = Calendar.getInstance();

        calendar.setTimeInMillis(milis);

        return sd.format(calendar.getTime());
    }

    /**
     * Convert time in milliseconds to string with format yyyy-MM-dd HH:mm:ss.
     *
     * @param milis Milliseconds
     *
     * @return String of time
     */
    public static String timeMilisToString(long milis, String format) {
        SimpleDateFormat sd = new SimpleDateFormat(format);
        Calendar calendar   = Calendar.getInstance();

        calendar.setTimeInMillis(milis);

        return sd.format(calendar.getTime());
    }

    public static String getMonth(int month) {
        String str = "Январь";

        switch (month) {
            case 1:
                str = "Январь";
                break;
            case 2:
                str = "Февраль";
                break;
            case 3:
                str = "Март";
                break;
            case 4:
                str = "Апрель";
                break;
            case 5:
                str = "Май";
                break;
            case 6:
                str = "Июнь";
                break;
            case 7:
                str = "Июль";
                break;
            case 8:
                str = "Август";
                break;
            case 9:
                str = "Сентябрь";
                break;
            case 10:
                str = "Октябрь";
                break;
            case 11:
                str = "Ноябрь";
                break;
            case 12:
                str = "Декабрь";
                break;
        }

        return str;
    }

    public static String getAdvDate(String date) {
        //current
        Calendar c 	= Calendar.getInstance();

        int year 	= c.get(Calendar.YEAR);
        int month 	= c.get(Calendar.MONTH) + 1;
        int day 	= c.get(Calendar.DAY_OF_MONTH);

        //defined
        long milis	= timeStringToMilis(date);

        Calendar calendar   = Calendar.getInstance();

        calendar.setTimeInMillis(milis);

        if (day == calendar.get(Calendar.DAY_OF_MONTH) && month == (calendar.get(Calendar.MONTH)+1)
                && year == calendar.get(Calendar.YEAR)) {
            return "Сегодня , " + DateUtil.timeMilisToString(milis, "MMM dd");
        } else if (year == calendar.get(Calendar.YEAR)) {
            return DateUtil.timeMilisToString(milis, "MMM dd");
        } else {
            return DateUtil.timeMilisToString(milis, "MMM dd, yyyy");
        }
    }
}