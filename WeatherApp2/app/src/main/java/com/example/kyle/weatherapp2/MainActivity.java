package com.example.kyle.weatherapp2;

import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import layout.FragmentForecast;

public class MainActivity extends AppCompatActivity
    implements FragmentForecast.OnFragmentInteractionListener
{

    android.app.FragmentManager fragmentManager = getFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /*setContentView(R.layout.activity_main);*/

        /*Configuration configInfo = getResources().getConfiguration();*/

        FragmentCurrentWeather fragmentCurrentWeather = new FragmentCurrentWeather();
        fragmentTransaction.replace(android.R.id.content, fragmentCurrentWeather);


        /*if (configInfo.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            FragmentLandscape fragmentLandscape = new FragmentLandscape();
            fragmentTransaction.replace(android.R.id.content, fragmentLandscape);
        } else {
            FragmentPortrait fragmentPortrait = new FragmentPortrait();
            fragmentTransaction.replace(android.R.id.content, fragmentPortrait);
        }*/

          fragmentTransaction.commit();



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.currentWeather:
                currentWeather();
                return true;
            case R.id.forecast:
                forecast();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;


    }

    private void forecast() {
        FragmentTransaction fragTrans = fragmentManager.beginTransaction();
        FragmentForecast fragmentForecast = new FragmentForecast();
        fragTrans.replace(android.R.id.content, fragmentForecast);
        fragTrans.commit();
    }

    private void currentWeather() {
        FragmentTransaction fragTrans = fragmentManager.beginTransaction();
        FragmentCurrentWeather fragmentCurrentWeather = new FragmentCurrentWeather();
        fragTrans.replace(android.R.id.content, fragmentCurrentWeather);
        fragTrans.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
