package com.example.wheaterviewerapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

    // Lista de objetos Weather (agora adaptados para o trabalho)
    private List<Weather> weatherList = new ArrayList<>();

    // O adaptador que liga os dados à ListView
    private WeatherArrayAdapter weatherArrayAdapter;

    // A ListView da tela
    private ListView weatherListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configura a Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configura a ListView
        weatherListView = findViewById(R.id.weatherListView);
        weatherArrayAdapter = new WeatherArrayAdapter(this, weatherList);
        weatherListView.setAdapter(weatherArrayAdapter);

        // Configura o Botão Flutuante (FAB)
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pega o texto digitado (Cidade)
                EditText locationEditText = findViewById(R.id.locationEditText);
                String city = locationEditText.getText().toString();

                if (city.isEmpty()) {
                     Snackbar.make(findViewById(R.id.main), 
                         "Por favor, digite uma cidade", Snackbar.LENGTH_LONG).show();
                     return;
                }

                // Cria a URL no formato exigido pelo Professor
                URL url = createURL(city);

                // Se a URL for válida, esconde teclado e inicia o download
                if (url != null) {
                    dismissKeyboard(locationEditText);
                    GetWeatherTask getLocalWeatherTask = new GetWeatherTask();
                    getLocalWeatherTask.execute(url);
                } else {
                    Snackbar.make(findViewById(R.id.main),
                            R.string.invalid_url, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    // Esconde o teclado virtual
    private void dismissKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // -------------------------------------------------------------------------
    // MUDANÇA CRÍTICA 1: Montagem da URL conforme o Trabalho
    // Padrão: .../api/weather?city=Passos,MG,BR&days=7&APPID=...
    // -------------------------------------------------------------------------
    private URL createURL(String city) {
        String apiKey = getString(R.string.api_key);
        String baseUrl = getString(R.string.web_service_url);

        try {
            // Codifica a cidade para URL (ex: "São Paulo" vira "S%C3%A3o+Paulo")
            String urlString = baseUrl + "?city=" + URLEncoder.encode(city, "UTF-8")
                    + "&days=7" // Valor fixo conforme permitido nas orientações
                    + "&APPID=" + apiKey;

            return new URL(urlString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // URL inválida
    }

    // -------------------------------------------------------------------------
    // AsyncTask: Realiza a conexão em segundo plano (fundo)
    // -------------------------------------------------------------------------
    private class GetWeatherTask extends AsyncTask<URL, Void, JSONObject> {

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
                } else {
                    // Erro no servidor (ex: 404, 500)
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace(); // Erro de conexão
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject weather) {
            if (weather != null) {
                convertJSONtoArrayList(weather); // Processa os dados
                weatherArrayAdapter.notifyDataSetChanged(); // Atualiza a lista
                weatherListView.smoothScrollToPosition(0); // Rola para o topo
            } else {
                Snackbar.make(findViewById(R.id.main),
                        R.string.connect_error, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    // -------------------------------------------------------------------------
    // MUDANÇA CRÍTICA 2: Processamento do JSON do Professor
    // Estrutura esperada:
    // {
    //   "city": "...",
    //   "days": [ { "date": "...", "minTempC": 20, ... }, ... ]
    // }
    // -------------------------------------------------------------------------
    private void convertJSONtoArrayList(JSONObject forecast) {
        weatherList.clear(); // Limpa dados antigos

        try {
            // A lista agora chama "days" (no OpenWeatherMap chamava "list")
            JSONArray list = forecast.getJSONArray("days");

            // Loop para cada dia da previsão
            for (int i = 0; i < list.length(); ++i) {
                JSONObject day = list.getJSONObject(i);

                // Extraindo dados conforme os nomes exatos do JSON do professor
                String date = day.getString("date");
                double minTemp = day.getDouble("minTempC");
                double maxTemp = day.getDouble("maxTempC");
                double humidity = day.getDouble("humidity");
                String description = day.getString("description");
                String icon = day.getString("icon"); // É um Emoji

                // Adiciona na lista usando nosso construtor adaptado
                weatherList.add(new Weather(
                        date, minTemp, maxTemp, humidity, description, icon));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            // Opcional: Mostrar erro de parse
        }
    }
}