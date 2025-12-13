package com.example.weatherviewerapp;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Weather {
    public final String dayOfWeek;
    public final String minTemp;
    public final String maxTemp;
    public final String humidity;
    public final String description;
    public final String icon;

    public Weather(String dateString, double minTemp, double maxTemp,
                   double humidity, String description, String icon) {

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);

        this.dayOfWeek = convertDateToDay(dateString);

        this.minTemp = numberFormat.format(minTemp) + "\u00B0C";
        this.maxTemp = numberFormat.format(maxTemp) + "\u00B0C";
        this.humidity = numberFormat.format(humidity * 100) + "%";
        this.description = description;
        this.icon = icon;
    }

    private static String convertDateToDay(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(dateString);

            Locale brazil = new Locale("pt", "BR");

            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy", brazil);

            String resultado = outputFormat.format(date);

            return resultado.substring(0, 1).toUpperCase() + resultado.substring(1);

        } catch (Exception e) {
            e.printStackTrace();
            return dateString;
        }
    }
}
