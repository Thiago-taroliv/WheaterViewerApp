package com.example.weatherviewerapp;

import android.content.Context;
import android.graphics.Typeface; // Import necessário para o negrito
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class WeatherArrayAdapter extends ArrayAdapter<Weather> {

    // Classe interna ViewHolder
    private static class ViewHolder {
        TextView conditionIconView;
        TextView dayTextView;
        TextView lowTextView;
        TextView hiTextView;
        TextView humidityTextView;
    }

    // Construtor
    public WeatherArrayAdapter(Context context, List<Weather> forecast) {
        super(context, -1, forecast);
    }

    // Método getView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Pega o objeto Weather da posição atual
        Weather day = getItem(position);

        ViewHolder viewHolder;

        // 2. Verifica se existe uma view reutilizável
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);

            // Conecta os IDs do XML (Certifique-se que no list_item.xml os IDs são exatamente estes)
            viewHolder.conditionIconView = convertView.findViewById(R.id.conditionIconView);
            viewHolder.dayTextView = convertView.findViewById(R.id.dayTextView);
            viewHolder.lowTextView = convertView.findViewById(R.id.lowTextView);
            viewHolder.hiTextView = convertView.findViewById(R.id.hiTextView);
            viewHolder.humidityTextView = convertView.findViewById(R.id.humidityTextView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 3. Preenche os dados nos textos
        Context context = getContext();

        if (day != null) {
            // Define o Emoji
            viewHolder.conditionIconView.setText(day.icon);

            // --- MELHORIA DO TÍTULO ---
            // Coloca o Dia/Data em destaque e concatena com a descrição
            // Ex: "Segunda-feira, 06/12: Sol com nuvens"
            String tituloCompleto = day.dayOfWeek + ": " + day.description;
            viewHolder.dayTextView.setText(tituloCompleto);

            // Aplica negrito no título para destacar
            viewHolder.dayTextView.setTypeface(null, Typeface.BOLD);

            // Temperaturas e Umidade
            viewHolder.lowTextView.setText(
                    context.getString(R.string.low_temp, day.minTemp));

            viewHolder.hiTextView.setText(
                    context.getString(R.string.high_temp, day.maxTemp));

            viewHolder.humidityTextView.setText(
                    context.getString(R.string.humidity, day.humidity));
        }

        return convertView;
    }
}
