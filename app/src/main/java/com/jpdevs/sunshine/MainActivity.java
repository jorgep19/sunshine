package com.jpdevs.sunshine;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }

        Log.i(TAG, "Hit onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "Hit onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Hit onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "Hit onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "Hit onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Hit onDestroy");
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
