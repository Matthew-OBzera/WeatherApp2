package com.example.kyle.weatherapp2;

import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements FragmentForecast.OnFragmentInteractionListener, Downloader.DownloadListener<JSONObject> {
    private String zipCode = "";
    android.app.FragmentManager fragmentManager = getFragmentManager();

    FragmentCurrentWeather fragmentCurrentWeather;
    FragmentForecast fragmentForecast;

    DayForecast dayForecast = new DayForecast();

    public static int UNIT = 0;
    public static final int IMPERIAL = 0;
    public static final int METRIC = 1;
    LinkedList<String> recentZipcodes;
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Configuration configInfo = getResources().getConfiguration();*/
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentCurrentWeather = new FragmentCurrentWeather();
        fragmentTransaction.replace(R.id.fragLayout, fragmentCurrentWeather);
        fragmentTransaction.commit();


        recentZipcodes = new LinkedList<>();
        sp = getPreferences(Context.MODE_PRIVATE);
        String recentZip = sp.getString("recentZipCodes", null);
        if (recentZip != null) {
            try {
                JSONArray jArray = new JSONArray(recentZip);
                for (int i = 0; i < jArray.length(); i++) {
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
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_zip1:
                                getLocation(recentZipcodes.get(0));
                                break;
                            case R.id.action_zip2:
                                getLocation(recentZipcodes.get(1));
                                break;
                            case R.id.action_zip3:
                                getLocation(recentZipcodes.get(2));
                                break;
                            case R.id.action_zip4:
                                getLocation(recentZipcodes.get(3));
                                break;
                            case R.id.action_zip5:
                                getLocation(recentZipcodes.get(4));
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.getMenu().add(1, R.id.action_recentZipLabel, 0, "Recent Zip Codes");
                for (int i = 0; i < recentZipcodes.size(); i++) {
                    String idVal = "action_zip" + i;
                    popupMenu.getMenu().add(1, getResources().getIdentifier(idVal, "id", getPackageName()), i + 1, recentZipcodes.get(i));
                }
                popupMenu.show();
                break;
            case R.id.action_7DayForecast:
                forecast();
                break;
            case R.id.action_currentWeather:
                currentWeather();
                break;
            case R.id.action_units:
                if(UNIT == IMPERIAL){
                    UNIT = METRIC;
                }
                else{
                    UNIT = IMPERIAL;
                }
                break;
            case R.id.action_about:
                final AlertDialog.Builder aboutBuilder = new AlertDialog.Builder(this);
                aboutBuilder.setMessage("Written by: Gerardo Paleo, Justin Long, Kyle, Matthew OBzera" +
                                        " \n" + "Data from weather.gov" );
                aboutBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                aboutBuilder.show();
                break;
            default:
                break;
        }
        return true;
    }

    private void forecast() {
        FragmentTransaction fragTrans = fragmentManager.beginTransaction();
        fragmentForecast = new FragmentForecast();
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

    /*private void aboutDialog() {

    }*/

    public void getLocation(String zipcode) {
        if (!isNumeric(zipcode) || zipcode.length() != 5) {
            responseToast("Please enter a 5-digit zip code");
        } else if (!isNetworkAvailable()) {
            responseToast("No internet connection available");
        } else {

            Downloader<JSONObject> downloadInfo = new Downloader<>(this);
            downloadInfo.execute("http://craiginsdev.com/zipcodes/findzip.php?zip=" + zipcode);
        }
    }

    @Override
    public JSONObject parseResponse(InputStream in) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            return new JSONObject(reader.readLine());
        } catch (JSONException | IOException j) {
            j.printStackTrace();
        }
        return null;
    }

    @Override
    public void handleResult(JSONObject result) {
        WeatherInfoIO.WeatherListener weatherDownloaded = new WeatherInfoIO.WeatherListener() {
            @Override
            public void handleResult(WeatherInfo result) {
                if (result != null) {
                    Bitmap bm;
                    File cacheDir = getApplication().getCacheDir();
                    File f = new File(cacheDir, zipCode.toString() + ".png");
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(f);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (fis != null) {
                        bm = BitmapFactory.decodeStream(fis);
                        fragmentCurrentWeather.setImage(bm);
                    } else {
                        new DownloadImageTask().execute(result.current.imageUrl);
                    }


                    fragmentCurrentWeather.setInfo(result);
                    fragmentForecast.setInfo(result, 0);


                    alert(result.alerts);
                    if (!recentZipcodes.contains(zipCode)) {
                        recentZipcodes.addFirst(zipCode);
                        if (recentZipcodes.size() > 5) {
                            recentZipcodes.removeLast();
                        }
                    }
                    JSONArray jsonArray = new JSONArray(recentZipcodes);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("recentZipCodes", jsonArray.toString());
                    editor.apply();
                } else {
                    //go = false;
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Weather not found for this zip code", Toast.LENGTH_LONG).show();
                        }
                    });
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
        } catch (Exception e) {
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Unknown zip code", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    //Creates notifications with links to Weather.gov alerts
    public void alert(List<String> alerts) {
        for (int i = 0; i < alerts.size(); i++) {
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
            NotificationManager alertNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            alertNotifyMgr.notify(i, alertBuilder.build());
        }
    }

    //Stack overflow
    //Testing for internet connexion
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void responseToast(String text) {
        Toast toast = Toast.makeText(this.getApplicationContext(), text, Toast.LENGTH_LONG);
        toast.show();
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    //Stack Overflow
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        //ImageView bmImage;

        //public DownloadImageTask(ImageView bmImage) {
        //    this.bmImage = bmImage;
        //}

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                try {
                    File cacheDir = getApplicationContext().getCacheDir();
                    File f = new File(cacheDir, zipCode.toString() + ".png");
                    FileOutputStream out = new FileOutputStream(f);
                    mIcon11.compress(
                            Bitmap.CompressFormat.PNG,
                            100, out);
                    out.flush();
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            fragmentCurrentWeather.setImage(result);
        }
    }
}

