package com.amit.groupsprojectmvc;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class MathFucnClass {


    public static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {

        double theta = Math.abs(lon1 - lon2);
        double dist = (Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta)));
        dist = Math.acos(dist);
        dist = rad2deg(dist);


        dist = (dist * 60 * 1.1515);
        if (unit == 'K') {
            dist = (dist * 1.609344);
        } else if (unit == 'N') {
            dist = (dist * 0.8684);
        } else if (unit == 'm') {
            dist = (dist * 1609.344);
        }
        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return deg * Math.PI / 180.0;
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return rad * 180.0 / Math.PI;
    }

    public static String fixTime(int hour, int minute) {

        String time = ((hour + 1) < 10 ? "0" : "") + (hour + 1) + ":" + ((minute + 1) < 10 ? "0" : "") + minute;
        return time;
    }
    public static String fixDate(int year, int month, int day) {

        String date = ((day + 1) < 10 ? "0" : "") + day + "/" + ((month + 1) < 10 ? "0" : "") + (month + 1) + "/" + year;
        return date;
    }


    /**
     * @param timeInMilis
     *
     * "May 7, 09:00"
     *
     * @return cool date format
     */
    public static String dateCoolFormat(long timeInMilis) {
        Calendar c = Calendar.getInstance();
        //Set time in milliseconds
        c.setTimeInMillis(timeInMilis);
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int hr = c.get(Calendar.HOUR);
        int min = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);

        String monthString = new DateFormatSymbols().getMonths()[mMonth];


        return monthString.substring(0,3)+" "+(mDay)+", "+fixTime(hr-1,min);
    }
}
