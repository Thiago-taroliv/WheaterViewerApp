package com.example.weatherviewerapp;

import android.content.Context;
import android.content.SharedPreferences; // Import para salvar dados
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton; // Import do botão correto
import android.widget.ListView;
import android.widget.ProgressBar; // Import da barra de progresso
import com.example.weatherviewerapp.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Weather> weatherList = new ArrayList<>();
    private WeatherArrayAdapter weatherArrayAdapter;
    private ListView weatherListView;

    // Variável para controlar o Loading
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configura a Toolbar com verificação de segurança
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        // Conecta os elementos da tela às variáveis
        weatherListView = findViewById(R.id.weatherListView);
        progressBar = findViewById(R.id.progressBar); // Conecta o Loading do XML

        weatherArrayAdapter = new WeatherArrayAdapter(this, weatherList);
        weatherListView.setAdapter(weatherArrayAdapter);

        // Configura campo de texto e recupera última cidade salva
        EditText locationEditText = findViewById(R.id.locationEditText);
        SharedPreferences prefs = getSharedPreferences("WeatherApp", MODE_PRIVATE);
        String lastCity = prefs.getString("last_city", ""); // Pega a última cidade ou vazio
        locationEditText.setText(lastCity);

        // Configura o Botão
        ImageButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = locationEditText.getText().toString();

                if (city.isEmpty()) {
                    Snackbar.make(findViewById(R.id.main),
                            "Por favor, digite uma cidade", Snackbar.LENGTH_LONG).show();
                    return;
                }

                URL url = createURL(city);

                if (url != null) {
                    dismissKeyboard(locationEditText);

                    // Salva a cidade para a próxima vez
                    SharedPreferences.Editor editor = getSharedPreferences("WeatherApp", MODE_PRIVATE).edit();
                    editor.putString("last_city", city);
                    editor.apply();

                    GetWeatherTask getLocalWeatherTask = new GetWeatherTask();
                    getLocalWeatherTask.execute(url);
                } else {
                    Snackbar.make(findViewById(R.id.main),
                            R.string.invalid_url, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void dismissKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private URL createURL(String city) {
        String apiKey = getString(R.string.api_key);
        String baseUrl = getString(R.string.web_service_url);

        try {
            String urlString = baseUrl + "?city=" + URLEncoder.encode(city, "UTF-8")
                    + "&days=7&APPID=" + apiKey;
            return new URL(urlString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- TAREFA ASSÍNCRONA (AQUI ESTÁ A LÓGICA DO LOADING) ---
    private class GetWeatherTask extends AsyncTask<URL, Void, JSONObject> {

        // 1. Antes de baixar: MOSTRA LOADING, ESCONDE LISTA
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
            if (weatherListView != null) weatherListView.setVisibility(View.GONE);
        }

        // 2. Durante (Baixa os dados)
        @Override
        protected JSONObject doInBackground(URL... params) {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) params[0].openConnection();
                int response = connection.getResponseCode();

                if (response == HttpURLConnection.HTTP_OK) {
                    StringBuilder builder = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    }
                    return new JSONObject(builder.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        // 3. Depois de baixar: ESCONDE LOADING, MOSTRA LISTA
        @Override
        protected void onPostExecute(JSONObject weather) {
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            if (weatherListView != null) weatherListView.setVisibility(View.VISIBLE);

            if (weather != null) {
                convertJSONtoArrayList(weather);
                weatherArrayAdapter.notifyDataSetChanged();
                weatherListView.smoothScrollToPosition(0);
            } else {
                Snackbar.make(findViewById(R.id.main),
                        R.string.connect_error, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void convertJSONtoArrayList(JSONObject forecast) {
        weatherList.clear();
        try {
            JSONArray list = forecast.getJSONArray("days");
            for (int i = 0; i < list.length(); ++i) {
                JSONObject day = list.getJSONObject(i);

                String date = day.getString("date");
                double minTemp = day.getDouble("minTempC");
                double maxTemp = day.getDouble("maxTempC");
                double humidity = day.getDouble("humidity");
                String description = day.getString("description");
                String icon = day.getString("icon");

                weatherList.add(new Weather(
                        date, minTemp, maxTemp, humidity, description, icon));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
