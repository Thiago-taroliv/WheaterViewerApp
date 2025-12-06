package com.example.wheaterviewerapp;

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

        // Chama o método atualizado para formatar a data
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

            // Força o local para Brasil (para garantir "Sábado" e não "Saturday")
            Locale brazil = new Locale("pt", "BR");

            // MUDANÇA AQUI: "EEEE" = Dia da semana, "dd/MM/yyyy" = Data
            // Exemplo: "Sábado, 06/12/2025"
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy", brazil);

            String resultado = outputFormat.format(date);

            // Capitaliza a primeira letra (ex: "sábado" vira "Sábado")
            return resultado.substring(0, 1).toUpperCase() + resultado.substring(1);

        } catch (Exception e) {
            e.printStackTrace();
            return dateString;
        }
    }
}
