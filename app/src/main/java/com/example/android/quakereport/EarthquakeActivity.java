/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EarthquakeActivity extends AppCompatActivity {

    private static final String LOCATION_SEPARATOR = " of ";

    private final String LOG_TAG = EarthquakeActivity.class.getName();
    private ArrayList<Earthquake> earthquakesList;
    private EarthquakeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Create a fake list of earthquake locations.
        //ArrayList<Earthquake> earthquakes = new ArrayList<>();
        //earthquakes.add(new Earthquake("4.6", "San Francisco", "Jan 4, 2018"));
        //earthquakes.add(new Earthquake("2.4","London", "Jan 3, 2018"));
        //earthquakes.add(new Earthquake("5.1","Tokyo", "Jan 5, 2018"));
        //earthquakes.add(new Earthquake("3.3","Mexico City", "Jan 4, 2018"));
        //earthquakes.add(new Earthquake("3.2","Moscow", "Jan 5, 2018"));
        //earthquakes.add(new Earthquake("1.9","Rio de Janeiro", "Jan 2, 2018"));
        //earthquakes.add(new Earthquake("3.7","Paris", "Jan 3, 2018"));

        // Find a reference to the {@link ListView} in the layout
        earthquakesList = new ArrayList<>();

        new GetEarthquakeData().execute();

        // Create a new {@link ArrayAdapter} of earthquakes
        adapter = new EarthquakeAdapter(this, earthquakesList);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        ListView earthquakeListView = findViewById(R.id.list);
        earthquakeListView.setAdapter(adapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Achar o terremoto atual que foi clicado
                Earthquake currentEarthquake = adapter.getItem(position);

                // Converte o URL String em um objeto URI (para passar no construtor de Intent)
                assert currentEarthquake != null;
                Uri earthquakeUri = Uri.parse(currentEarthquake.getmUrl());

                // Cria um novo intent para visualizar a URI do earthquake
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Envia o intent para lançar uma nova activity
                startActivity(websiteIntent);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class GetEarthquakeData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(EarthquakeActivity.this, "JSON Data is downloading",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(),
                    "Found " + adapter.getCount() + " itens", Toast.LENGTH_LONG).show();
            adapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2017-01-01&endtime=2018-01-15&minmagnitude=5";//"https://earthquake.usgs.gov/fdsnws/event/1/query?starttime=2018-01-10&endtime=2018-01-11&format=geojson&minmagnitude=4.5";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(LOG_TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    //Getting JSON Array node
                    JSONArray earthquakes = jsonObj.getJSONArray("features");

                    //Looping through All earthquakes
                    int i;
                    for (i = 0; i < earthquakes.length(); i++) {
                        JSONObject eq = earthquakes.getJSONObject(i);
                        JSONObject eqprop = eq.getJSONObject("properties");
                        Double mag = eqprop.getDouble("mag");
                        String place = eqprop.getString("place");
                        String direction;
                        if (place.contains(" of ")) {
                            direction = place.substring(0, place.indexOf(" of ") + 3);
                            place = place.substring(place.indexOf(LOCATION_SEPARATOR) + 4, place.length());
                        } else {
                            direction = getString(R.string.default_toptext);
                        }
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                        String date = dateFormatter.format(new Date(eqprop.getLong("time")));
                        dateFormatter = new SimpleDateFormat("h:mm a", Locale.US);
                        String hour = dateFormatter.format(new Date(eqprop.getLong("time")));
                        String urlEq = eqprop.getString("url");
                        earthquakesList.add(new Earthquake(mag, place, direction, date, hour, urlEq));
                    }
                    //Toast.makeText(getApplicationContext(),
                    //        "Found " + i + "itens", Toast.LENGTH_LONG).show();
                }
                catch (final JSONException e) {
                    Log.e(LOG_TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }
            } else {
                Log.e(LOG_TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }
    }

}