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
    public final String icon; // Agora é uma String (Emoji), não URL

    // Construtor adaptado para a API do Professor
    public Weather(String dateString, double minTemp, double maxTemp,
                   double humidity, String description, String icon) {

        // Formatador de números (arredonda para inteiro)
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);

        // 1. Data: A API retorna "2025-11-26", precisamos converter para "Quarta-feira"
        this.dayOfWeek = convertDateToDay(dateString);

        // 2. Temperatura: Já vem em Celsius, só formatamos
        this.minTemp = numberFormat.format(minTemp) + "\u00B0C";
        this.maxTemp = numberFormat.format(maxTemp) + "\u00B0C";

        // 3. Umidade: A API retorna 0.75 (float), precisamos mostrar 75%
        this.humidity = numberFormat.format(humidity * 100) + "%";

        // 4. Descrição e Ícone (Emoji)
        this.description = description;
        this.icon = icon;
    }

    // Método auxiliar modificado para ler String "yyyy-MM-dd"
    private static String convertDateToDay(String dateString) {
        try {
            // Formato que vem da API
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(dateString);

            // Formato que queremos mostrar (Dia da semana)
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return dateString; // Se der erro, retorna a data original
        }
    }
}