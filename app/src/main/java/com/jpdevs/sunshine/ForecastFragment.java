package com.jpdevs.sunshine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> mListAdapter;

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
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }

        if (id == R.id.action_see_location) {
            SharedPreferences prefs = getActivity().getSharedPreferences(
                    SettingsActivityFragment.SUNSHINE_SETTINGS_PREFS,
                    Context.MODE_PRIVATE);

            String zipCode = prefs.getString(
                    getString(R.string.pref_location_key),
                    getString(R.string.location_pref_default_value));
            Uri uri = Uri.parse("geo:0,0?q=" + zipCode);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        ArrayList<String> data = new ArrayList<>();
        mListAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                data);
        listView.setAdapter(mListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ForecastDetailActivity.class)
                        .putExtra(ForecastDetailActivity.FORECAST, mListAdapter.getItem(position));
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void updateWeather() {
        SharedPreferences prefs = getActivity().getSharedPreferences(
                SettingsActivityFragment.SUNSHINE_SETTINGS_PREFS,
                Context.MODE_PRIVATE);

        new FetchWeatherTask().execute(prefs.getString(
                getString(R.string.pref_location_key),
                getString(R.string.location_pref_default_value)));
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
        private final String TAG = FetchWeatherTask.class.getName();

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
        protected String[] doInBackground(String[] zips) {
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
                Uri.Builder builder = Uri.parse(String.format("http://%s%s", WEATHER_DOMAIN, WEATHER_ENDPOINT))
                        .buildUpon()
                        .appendQueryParameter(QUERY_KEY, zips[0])
                        .appendQueryParameter(MODE_KEY, MODE_VALUE)
                        .appendQueryParameter(UNITS_KEY, UNITS_VALUE)
                        .appendQueryParameter(COUNT_KEY, COUNT_VALUE);

                URL url = new URL(builder.build().toString());

//                Log.v(TAG, "The url created was: " + url.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
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
                    buffer.append(line);
                    buffer.append("\n");
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
            } finally {
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

//            Log.v(TAG, "The data received was: " + forecastJsonStr);

            String[] itemContents = new String[0];
            try {
                itemContents = getWeatherDataFromJSON(forecastJsonStr, 7);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return itemContents;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (mListAdapter != null) {
                mListAdapter.clear();
                mListAdapter.addAll(strings);
            }
        }

        /**
         * The date/time conversion code is going to be moved outside the synctask later,
         * so for convenience we're breaking it out int o its own method now.
         */
        private String getReadableDateString(long time) {
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid data.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EE MMM dd");
            return shortenedDateFormat.format(time);
        }

        /**
         * Prepare the weather high/lows for presentation
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);


            return roundedHigh + "/" + roundedLow;
        }

        /**
         * Take the string representing the complete forecast in JSON format and
         * pull out the data we need to construct the Strings needed for the wireframes
         * <p/>
         * Fortunately parsing is easy: constructor takes the JSON string and converts it
         * into a Object hierarchy for us.
         */
        private String[] getWeatherDataFromJSON(String forecastJSONStr, int numDays)
                throws JSONException {
            // These are the names of the JSON objects tha need to be extracted.
            final String OWN_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJSON = new JSONObject(forecastJSONStr);
            JSONArray weatherArray = forecastJSON.getJSONArray(OWN_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this date
            // properly.

            // Since this data is also sent in-orderand the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            String[] resultsStrs = new String[numDays];
            for (int i = 0; i < weatherArray.length(); ++i) {
                // Fow now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                //The date/time is returned as a long . We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday"
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay + i);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp". Try not to name variables
                // "temp" when working with temperature. It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);


                SharedPreferences prefs = getActivity().getSharedPreferences(
                        SettingsActivityFragment.SUNSHINE_SETTINGS_PREFS,
                        Context.MODE_PRIVATE);

                // if prefs is imperial units conver temperature from Metric/Celsius to Imperial/Fahrenheit
                if (prefs.getString(
                        getString(R.string.pref_unit_key),
                        getString(R.string.location_pref_default_value)).equals(getString(R.string.imperial_value))) {
                    high = high * 1.8 + 32;
                    low = low * 1.8 + 32;
                }

                highAndLow = formatHighLows(high, low);
                resultsStrs[i] = day + " - " + description + " - " + highAndLow;
            }

//            for (String s : resultsStrs) {
//                Log.v(TAG, "Forecast entry: " + s);
//            }

            return resultsStrs;
        }
    }
}
