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
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.Locale;

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
    //"https://earthquake.usgs.gov/fdsnws/event/1/query?starttime=2018-01-10&endtime=2018-01-11&format=geojson&minmagnitude=4.5";


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

        //Execute background async task to get earthquake data
        new GetEarthquakeData().execute(USGS_REQUEST_URL);

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
    private class GetEarthquakeData extends AsyncTask<String, Integer, Void> {

        /**
         * Warn that data is loading
         */
        @Override
        protected void onPreExecute() {
            Toast.makeText(EarthquakeActivity.this, "JSON Data is downloading",
                    Toast.LENGTH_LONG).show();
        }

        /**
         * Display Toast to inform total number of itens, update adapter
         * and clear progress bar
         *
         * @param aVoid no data passed
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(EarthquakeActivity.this, //getApplicationContext(),
                    "Found " + adapter.getCount() + " itens", Toast.LENGTH_LONG).show();
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);//progressBar.getLayoutParams().height = 0;
        }

        /**
         * Update the progress bar
         *
         * @param values number to control progress bar
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            if (progressBar.isIndeterminate()) {
                progressBar.setIndeterminate(false);
            }
            //adapter.notifyDataSetChanged();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progressBar.setProgress(values[0], true);
            } else {
                progressBar.setProgress(values[0]);
            }
        }

        /** Fetch JSON data into array list of earthquakes displayed in adapter
         * @param urls no input data
         * @return null
         */
        @Override
        protected Void doInBackground(String... urls) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            String jsonStr = sh.makeServiceCall(urls[0]);

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
                        if ((i % (earthquakes.length() / 50)) == 0) {
                            publishProgress((int) ((i / (float) earthquakes.length()) * 100) + 1);
                        }
                    }
                } catch (final JSONException e) {
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

    /**
     * Buffer JSON into string
     */
    class HttpHandler {

        private final String TAG = HttpHandler.class.getSimpleName();

        HttpHandler() {
        }

        String makeServiceCall(String reqUrl) {
            String response = null;
            try {
                URL url = new URL(reqUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                // read the response
                InputStream in = new BufferedInputStream(conn.getInputStream());
                response = convertStreamToString(in);
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            progressBar.setIndeterminate(true);

            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return sb.toString();
        }
    }
}