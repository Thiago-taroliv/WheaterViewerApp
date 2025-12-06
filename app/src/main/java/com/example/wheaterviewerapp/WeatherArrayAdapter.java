package com.example.wheaterviewerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class WeatherArrayAdapter extends ArrayAdapter<Weather> {

    // Classe interna ViewHolder: guarda as referências dos elementos da tela
    // para não precisarmos buscar (findViewById) toda vez que rolar a lista.
    private static class ViewHolder {
        TextView conditionIconView; // MUDANÇA: Agora é TextView para o Emoji
        TextView dayTextView;
        TextView lowTextView;
        TextView hiTextView;
        TextView humidityTextView;
    }

    // Construtor
    public WeatherArrayAdapter(Context context, List<Weather> forecast) {
        super(context, -1, forecast);
    }

    // O método getView monta o visual de CADA linha da lista
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        // 1. Pega o objeto Weather da posição atual
        Weather day = getItem(position);

        ViewHolder viewHolder;

        // 2. Verifica se existe uma view reutilizável (padrão ViewHolder)
        if (convertView == null) {
            // Se não existe, "infla" (cria) o layout a partir do XML
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);

            // Conecta os IDs do XML
            viewHolder.conditionIconView = convertView.findViewById(R.id.conditionIconView);
            viewHolder.dayTextView = convertView.findViewById(R.id.dayTextView);
            viewHolder.lowTextView = convertView.findViewById(R.id.lowTextView);
            viewHolder.hiTextView = convertView.findViewById(R.id.hiTextView);
            viewHolder.humidityTextView = convertView.findViewById(R.id.humidityTextView);

            // Guarda o viewHolder dentro da view para usar depois
            convertView.setTag(viewHolder);
        } else {
            // Se já existe, apenas recupera o que estava guardado
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 3. Preenche os dados nos textos
        // Como temos acesso ao Context, podemos usar getString para formatar
        Context context = getContext();

        // Ícone (AQUI ESTÁ A GRANDE MUDANÇA: Apenas setamos o texto do Emoji)
        if (day != null) { // Verificação de segurança
            viewHolder.conditionIconView.setText(day.icon);

            // Dia e Descrição
            viewHolder.dayTextView.setText(context.getString(
                    R.string.day_description, day.dayOfWeek, day.description));

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