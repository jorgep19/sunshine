package com.jpdevs.sunshine;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
