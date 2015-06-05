package com.jpdevs.sunshine;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ForecastDetailFragment extends Fragment {
    private static final String FORECAST_KEY = "forecast_data";

    public static ForecastDetailFragment newInstance(String forecast) {
        ForecastDetailFragment fragment = new ForecastDetailFragment();

        Bundle args = new Bundle();
        args.putString(FORECAST_KEY, forecast);
        fragment.setArguments(args);

        return fragment;
    }

    public ForecastDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView forecast= (TextView) rootView.findViewById(R.id.forecast_textview);
        forecast.setText(getArguments().getString(FORECAST_KEY));

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
    }
}
