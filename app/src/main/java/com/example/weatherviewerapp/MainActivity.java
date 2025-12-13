package com.example.weatherviewerapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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
    private ProgressBar progressBar;

    private static final String PREFS_NAME = "WeatherApp";
    private static final String KEY_DARK_MODE = "is_dark_mode";
    private static final String KEY_LAST_CITY = "last_city";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupTheme();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        weatherListView = findViewById(R.id.weatherListView);
        progressBar = findViewById(R.id.progressBar);
        weatherArrayAdapter = new WeatherArrayAdapter(this, weatherList);
        weatherListView.setAdapter(weatherArrayAdapter);

        EditText locationEditText = findViewById(R.id.locationEditText);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String lastCity = prefs.getString(KEY_LAST_CITY, "");
        locationEditText.setText(lastCity);

        ImageButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            String city = locationEditText.getText().toString();
            if (city.isEmpty()) {
                Snackbar.make(findViewById(R.id.main), "Por favor, digite uma cidade", Snackbar.LENGTH_LONG).show();
                return;
            }

            URL url = createURL(city);
            if (url != null) {
                dismissKeyboard(locationEditText);

                prefs.edit().putString(KEY_LAST_CITY, city).apply();

                GetWeatherTask getLocalWeatherTask = new GetWeatherTask();
                getLocalWeatherTask.execute(url);
            } else {
                Snackbar.make(findViewById(R.id.main), R.string.invalid_url, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setupTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_theme);
        if (item != null) {
            int currentMode = AppCompatDelegate.getDefaultNightMode();
            if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
                item.setTitle("Modo Claro");
                item.setIcon(R.drawable.ic_sun);
            } else {
                item.setTitle("Modo Escuro");
                item.setIcon(R.drawable.ic_moon);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_theme) {
            toggleTheme();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isCurrentlyDark = (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);

        if (isCurrentlyDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            prefs.edit().putBoolean(KEY_DARK_MODE, false).apply();
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            prefs.edit().putBoolean(KEY_DARK_MODE, true).apply();
        }
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
            return new URL(baseUrl + "?city=" + URLEncoder.encode(city, "UTF-8") + "&days=7&APPID=" + apiKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void convertJSONtoArrayList(JSONObject forecast) {
        weatherList.clear();
        try {
            JSONArray list = forecast.getJSONArray("days");
            for (int i = 0; i < list.length(); ++i) {
                JSONObject day = list.getJSONObject(i);
                weatherList.add(new Weather(
                        day.getString("date"),
                        day.getDouble("minTempC"),
                        day.getDouble("maxTempC"),
                        day.getDouble("humidity"),
                        day.getString("description"),
                        day.getString("icon")
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetWeatherTask extends AsyncTask<URL, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
            if (weatherListView != null) weatherListView.setVisibility(View.GONE);
        }

        @Override
        protected JSONObject doInBackground(URL... params) {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) params[0].openConnection();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    StringBuilder builder = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) builder.append(line);
                    }
                    return new JSONObject(builder.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject weather) {
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            if (weatherListView != null) weatherListView.setVisibility(View.VISIBLE);

            if (weather != null) {
                convertJSONtoArrayList(weather);
                weatherArrayAdapter.notifyDataSetChanged();
                weatherListView.smoothScrollToPosition(0);
            } else {
                Snackbar.make(findViewById(R.id.main), R.string.connect_error, Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
