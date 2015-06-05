package com.jpdevs.sunshine;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

public class ForecastDetailActivity extends ActionBarActivity {
    public static final String FORECAST = "forecast_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String forecast = getIntent().getStringExtra(FORECAST);
        if(savedInstanceState == null && forecast != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, ForecastDetailFragment.newInstance(forecast))
                    .commit();
        }
    }
}
