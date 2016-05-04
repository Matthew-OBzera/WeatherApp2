package com.example.kyle.weatherapp2;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import layout.FragmentForecast;

public class MainActivity extends AppCompatActivity
    implements FragmentForecast.OnFragmentInteractionListener
{
    private String zipCode = "";
    android.app.FragmentManager fragmentManager = getFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Configuration configInfo = getResources().getConfiguration();*/
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        FragmentCurrentWeather fragmentCurrentWeather = new FragmentCurrentWeather();
        fragmentTransaction.replace(R.id.fragLayout, fragmentCurrentWeather);
        fragmentTransaction.commit();

        /*if (configInfo.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            FragmentLandscape fragmentLandscape = new FragmentLandscape();
            fragmentTransaction.replace(android.R.id.content, fragmentLandscape);
        } else {
            FragmentPortrait fragmentPortrait = new FragmentPortrait();
            fragmentTransaction.replace(android.R.id.content, fragmentPortrait);
        }*/




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_lookupZip:
                Context context = getApplication();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Enter Zip Code");
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        zipCode = input.getText().toString();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                //getLocation(zipcode);
                break;
            case R.id.action_recentzips:
                View menuItemView = findViewById(R.id.action_recentzips);
                PopupMenu popupMenu = new PopupMenu(this, menuItemView);
                popupMenu.inflate(R.menu.recent_zip_menu);
                popupMenu.show();
                break;
            case R.id.action_7DayForecast:
                forecast();
                break;
            case R.id.action_currentWeather:
                currentWeather();
                break;
            default:
                break;
        }

        return true;
    }

    private void forecast() {
        FragmentTransaction fragTrans = fragmentManager.beginTransaction();
        FragmentForecast fragmentForecast = new FragmentForecast();
        fragTrans.replace(R.id.fragLayout, fragmentForecast);
        fragTrans.commit();
    }

    private void currentWeather() {
        FragmentTransaction fragTrans = fragmentManager.beginTransaction();
        FragmentCurrentWeather fragmentCurrentWeather = new FragmentCurrentWeather();
        fragTrans.replace(R.id.fragLayout, fragmentCurrentWeather);
        fragTrans.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void aboutDialog()
    {

    }


}
