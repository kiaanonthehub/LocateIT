package com.locateitteam.locateit.Util;

public class Global {

    public static String metricSelection;
    public static String filteredLocation;

    public static double calcMiles(double value) {

        return 0;
    }

    public static double calcKilometers(double value) {

        return 0;
    }

    // method used to extract the prefic of the users email address before the @
    public static String getUsername(String email) {

        // get substring of @ and use as username for user
        String[] split = email.replace(".", "").split("@");

        // return the first part of the email before the @
        return split[0];
    }

}
