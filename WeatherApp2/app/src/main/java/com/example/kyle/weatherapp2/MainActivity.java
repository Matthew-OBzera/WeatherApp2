package com.example.kyle.weatherapp2;

import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import layout.FragmentForecast;

public class MainActivity extends AppCompatActivity
    implements FragmentForecast.OnFragmentInteractionListener, Downloader.DownloadListener<JSONObject>
{
    private String zipCode = "";
    android.app.FragmentManager fragmentManager = getFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    FragmentCurrentWeather fragmentCurrentWeather;
    LinkedList<String> recentZipcodes;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Configuration configInfo = getResources().getConfiguration();*/
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        fragmentCurrentWeather = new FragmentCurrentWeather();
        fragmentTransaction.replace(R.id.fragLayout, fragmentCurrentWeather);
        fragmentTransaction.commit();

        /*if (configInfo.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            FragmentLandscape fragmentLandscape = new FragmentLandscape();
            fragmentTransaction.replace(android.R.id.content, fragmentLandscape);
        } else {
            FragmentPortrait fragmentPortrait = new FragmentPortrait();
            fragmentTransaction.replace(android.R.id.content, fragmentPortrait);
        }*/

        recentZipcodes = new LinkedList<String>();
        sp = getPreferences(Context.MODE_PRIVATE);
        String recentZip = sp.getString("recentZipCodes", null);
        if (recentZip != null){
            try {
                JSONArray jArray = new JSONArray(recentZip);
                for(int i = 0; i < jArray.length(); i ++){
                    recentZipcodes.add(jArray.get(i).toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
                        getLocation(zipCode);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
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
        fragmentCurrentWeather = new FragmentCurrentWeather();
        fragTrans.replace(R.id.fragLayout, fragmentCurrentWeather);
        fragTrans.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void aboutDialog()
    {

    }
    public void getLocation(String zipcode) {
        Downloader<JSONObject> downloadInfo = new Downloader<>(this);
        downloadInfo.execute("http://craiginsdev.com/zipcodes/findzip.php?zip=" + zipcode);
    }

    @Override
    public JSONObject parseResponse(InputStream in) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            JSONObject jsonObject = new JSONObject(reader.readLine());
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void handleResult(JSONObject result) {
        WeatherInfoIO.WeatherListener weatherDownloaded = new WeatherInfoIO.WeatherListener() {
            @Override
            public void handleResult(WeatherInfo result) {
                if (result != null) {
                    fragmentCurrentWeather.setInfo(result, getApplicationContext());
                    alert(result.alerts);
                    if(!recentZipcodes.contains(zipCode)){
                        recentZipcodes.addFirst(zipCode);
                        if (recentZipcodes.size() > 5){
                            recentZipcodes.removeLast();
                        }
                    }
                    JSONArray jsonArray = new JSONArray(recentZipcodes);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("RecentZipCodes", jsonArray.toString());

                } else {
                    //setText(-1);
                    //image.setImageResource(0);
                    //go = false;
                }
            }
        };
        try {
            String latitude = result.getString("latitude");
            String longitude = result.getString("longitude");
            WeatherInfoIO.loadFromUrl("http://forecast.weather.gov/MapClick.php?lat="
                            + latitude +
                            "&lon="
                            + longitude +
                            "&unit=0&lg=english&FcstType=dwml",
                    weatherDownloaded);

        } catch (JSONException e) {

        }
    }

    //Creates notifications with links to Weather.gov alerts
    public void alert(List<String> alerts)
    {
        for (int i = 0; i < alerts.size(); i++)
        {
            Log.v("NumberX", alerts.get(i));
            NotificationCompat.Builder alertBuilder =
                    (NotificationCompat.Builder) new NotificationCompat.Builder(MainActivity.this).
                            setSmallIcon(R.drawable.alert).setContentTitle("Weather Alert").setContentText("Severe weather warning at Weather.gov");
            Intent alertIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(alerts.get(i)));

            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            MainActivity.this,
                            0,
                            alertIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            alertBuilder.setContentIntent(resultPendingIntent);
            int alertNotificationId = i;
            NotificationManager alertNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            alertNotifyMgr.notify(alertNotificationId, alertBuilder.build());
        }
    }

}
