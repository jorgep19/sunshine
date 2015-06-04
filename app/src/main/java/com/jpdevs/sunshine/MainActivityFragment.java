package com.jpdevs.sunshine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivityFragment extends Fragment {
    private static final String[] WEEK_FORECAST = new String[] {
            "Today - Sunny - 88/63",
            "Tomorrow - Foggy - 70/46",
            "Weds - Cloudy - 72/63",
            "Thurs - Rainy - 64/51",
            "Fri - Foggy - 70/46",
            "Sat - Sunny - 76/68"
    };

    public MainActivityFragment() {}

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
}
