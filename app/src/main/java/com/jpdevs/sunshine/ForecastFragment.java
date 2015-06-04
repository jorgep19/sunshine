package com.jpdevs.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ForecastFragment extends Fragment {
    private static final String[] WEEK_FORECAST = new String[] {
            "Today - Sunny - 88/63",
            "Tomorrow - Foggy - 70/46",
            "Weds - Cloudy - 72/63",
            "Thurs - Rainy - 64/51",
            "Fri - Foggy - 70/46",
            "Sat - Sunny - 76/68"
    };

    public ForecastFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            new FetchWeatherTask().execute("33178");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                WEEK_FORECAST));

        return rootView;
    }

    public static class FetchWeatherTask extends AsyncTask<String, Void, String> {
        private static final String TAG = FetchWeatherTask.class.getName();

        private static final String WEATHER_DOMAIN = "api.openweathermap.org";
        private static final String WEATHER_ENDPOINT = "/data/2.5/forecast/daily";
        private static final String QUERY_KEY = "q";
        private static final String MODE_KEY = "mode";
        private static final String UNITS_KEY = "units";
        private static final String COUNT_KEY = "cnt";
        private static final String MODE_VALUE = "json";
        private static final String UNITS_VALUE = "metric";
        private static final String COUNT_VALUE = "7";

        @Override
        protected String doInBackground(String[] zips) {
            if (zips == null || zips.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
//                my Code
//                Uri.Builder builder = new Uri.Builder()
//                    .scheme("http")
//                    .authority(WEATHER_DOMAIN)
//                    .path(WEATHER_ENDPOINT)
//                    .appendQueryParameter(QUERY_KEY, zips[0])
//                    .appendQueryParameter(MODE_KEY, MODE_VALUE)
//                    .appendQueryParameter(UNITS_KEY, UNITS_VALUE)
//                    .appendQueryParameter(COUNT_KEY, COUNT_VALUE);

                // their-ish code
                Uri.Builder builder = Uri.parse(String.format("http://%s%s", WEATHER_DOMAIN,WEATHER_ENDPOINT))
                    .buildUpon()
                    .appendQueryParameter(QUERY_KEY, zips[0])
                    .appendQueryParameter(MODE_KEY, MODE_VALUE)
                    .appendQueryParameter(UNITS_KEY, UNITS_VALUE)
                    .appendQueryParameter(COUNT_KEY, COUNT_VALUE);

                URL url = new URL(builder.build().toString());

                Log.v(TAG, "The url created was: " +  url.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            Log.v(TAG, "The data received was: " + forecastJsonStr);

            return forecastJsonStr;
        }
    }
}
