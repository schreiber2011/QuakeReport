// * Copyright (C) 2016 The Android Open Source Project
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
package com.example.android.quakereport;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EarthquakeActivity extends AppCompatActivity {

    private static final String LOCATION_SEPARATOR = " of ";

    private final String LOG_TAG = EarthquakeActivity.class.getName();
    private ArrayList<Earthquake> earthquakesList;
    private EarthquakeAdapter adapter;
    private ProgressBar progressBar;
    /**
     * URL for earthquake data from the USGS dataset
     */
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2018-01-01&endtime=2018-01-17&minmagnitude=1";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        //Creates reference to progress bar
        progressBar = findViewById(R.id.progressBar);

        //Set list for earthquakes
        earthquakesList = new ArrayList<>();
        // Create a new {@link ArrayAdapter} of earthquakes
        adapter = new EarthquakeAdapter(this, earthquakesList);

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = findViewById(R.id.list);
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);

        //Execute background task to get earthquake data
        fetchEarthquakeData();

        earthquakeListView.setOnItemClickListener((adapterView, view, position, l) -> {
            // Find the earthquake that was clicked on
            Earthquake currentEarthquake = adapter.getItem(position);

            // Convert URL String in an object URI (to pass into the Intent constructor)
            assert currentEarthquake != null;
            Uri earthquakeUri = Uri.parse(currentEarthquake.getmUrl());

            // Creates a new intent to view the earthquake URI
            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

            // Send the intent to launch a new activity
            startActivity(websiteIntent);
        });
    }

    private void fetchEarthquakeData() {
        Toast.makeText(this, "JSON Data is downloading", Toast.LENGTH_LONG).show();
        
        executorService.execute(() -> {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall();
            List<Earthquake> fetchedEarthquakes = new ArrayList<>();

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray earthquakes = jsonObj.getJSONArray("features");

                    for (int i = 0; i < earthquakes.length(); i++) {
                        JSONObject eq = earthquakes.getJSONObject(i);
                        JSONObject eqProp = eq.getJSONObject("properties");
                        double mag = eqProp.getDouble("mag");
                        String place = eqProp.getString("place");
                        String direction;
                        if (place.contains(LOCATION_SEPARATOR)) {
                            direction = place.substring(0, place.indexOf(LOCATION_SEPARATOR) + 4);
                            place = place.substring(place.indexOf(LOCATION_SEPARATOR) + 4);
                        } else {
                            direction = getString(R.string.default_toptext);
                        }
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                        String date = dateFormatter.format(new Date(eqProp.getLong("time")));
                        dateFormatter = new SimpleDateFormat("h:mm a", Locale.US);
                        String hour = dateFormatter.format(new Date(eqProp.getLong("time")));
                        String urlEq = eqProp.getString("url");
                        fetchedEarthquakes.add(new Earthquake(mag, place, direction, date, hour, urlEq));
                        
                        final int progress = (int) ((i / (float) earthquakes.length()) * 100) + 1;
                        if ((i % Math.max(1, earthquakes.length() / 50)) == 0) {
                            runOnUiThread(() -> updateProgress(progress));
                        }
                    }
                } catch (final JSONException e) {
                    Log.e(LOG_TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                            "Json parsing error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show());
                }
            } else {
                Log.e(LOG_TAG, "Couldn't get json from server.");
                runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                        "Couldn't get json from server. Check LogCat for possible errors!",
                        Toast.LENGTH_LONG).show());
            }

            runOnUiThread(() -> {
                earthquakesList.clear();
                earthquakesList.addAll(fetchedEarthquakes);
                adapter.notifyDataSetChanged();
                Toast.makeText(EarthquakeActivity.this, "Found " + adapter.getCount() + " items", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            });
        });
    }

    private void updateProgress(int progress) {
        if (progressBar.isIndeterminate()) {
            progressBar.setIndeterminate(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBar.setProgress(progress, true);
        } else {
            progressBar.setProgress(progress);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    /**
     * Buffer JSON into string
     */
    class HttpHandler {

        private final String TAG = HttpHandler.class.getSimpleName();

        HttpHandler() {
        }

        String makeServiceCall() {
            String response = null;
            try {
                URL url = new URL(EarthquakeActivity.USGS_REQUEST_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                try (InputStream in = new BufferedInputStream(conn.getInputStream())) {
                    response = convertStreamToString(in);
                }
            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());
            } catch (ProtocolException e) {
                Log.e(TAG, "ProtocolException: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
            return response;
        }

        private String convertStreamToString(InputStream is) {
            StringBuilder sb = new StringBuilder();
            runOnUiThread(() -> progressBar.setIndeterminate(true));

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            } catch (IOException e) {
                Log.e(TAG, "Error reading stream", e);
            }

            return sb.toString();
        }
    }
}
