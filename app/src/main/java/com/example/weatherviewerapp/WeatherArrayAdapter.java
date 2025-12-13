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

    private static class ViewHolder {
        TextView conditionIconView;
        TextView dayTextView;
        TextView lowTextView;
        TextView hiTextView;
        TextView humidityTextView;
    }

    public WeatherArrayAdapter(Context context, List<Weather> forecast) {
        super(context, -1, forecast);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Weather day = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);

            viewHolder.conditionIconView = convertView.findViewById(R.id.conditionIconView);
            viewHolder.dayTextView = convertView.findViewById(R.id.dayTextView);
            viewHolder.lowTextView = convertView.findViewById(R.id.lowTextView);
            viewHolder.hiTextView = convertView.findViewById(R.id.hiTextView);
            viewHolder.humidityTextView = convertView.findViewById(R.id.humidityTextView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Context context = getContext();

        if (day != null) {
            viewHolder.conditionIconView.setText(day.icon);

            String tituloCompleto = day.dayOfWeek + ": " + day.description;
            viewHolder.dayTextView.setText(tituloCompleto);

            viewHolder.dayTextView.setTypeface(null, Typeface.BOLD);

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
