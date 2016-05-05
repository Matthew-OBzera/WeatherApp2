package com.example.kyle.weatherapp2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

public class FragmentForecast extends android.app.Fragment {

    private TextView location, highVal, lowVal, forecast;
    private ImageView icon;

    private String areaLocation, imageURL, forecastDesc;
    private double highTemp, lowTemp;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate((R.layout.forecast_fragment), container, false);
        getTextView(view);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setText(int id) {
        String locStr, highStr, lowStr, forecastStr;
        locStr = areaLocation;
        forecastStr = forecastDesc;
        switch(id) {
            case R.id.imperial:
                highStr = String.valueOf((int) highTemp) + "° F";
                lowStr = String.valueOf((int) lowTemp) + "° F";
                break;
            case R.id.metric:
                highStr = String.valueOf((int) (highTemp - 32) * (5.0 / 9.0)) + "° C";
                lowStr = String.valueOf((int) (lowTemp - 32) * (5.0 / 9.0)) + "° C";
                break;
            default:
                locStr = "";
                highStr = "";
                lowStr = "";
                forecastStr = "";
                break;
        }
        location.setText(locStr);
        highVal.setText(highStr);
        lowVal.setText(lowStr);
        forecast.setText(forecastStr);

        new DownloadImageTask(icon).execute(imageURL);
    }

    private void getTextView(View view) {

        location = (TextView)view.findViewById(R.id.location);
        highVal = (TextView)view.findViewById(R.id.highVal);
        lowVal = (TextView)view.findViewById(R.id.lowVal);
        forecast = (TextView)view.findViewById(R.id.forecast);

        icon = (ImageView)view.findViewById(R.id.forecastImg);
    }

    public void setInfo(WeatherInfo info, DayForecast dayForecast) {
        areaLocation = info.location.name;
        highTemp = dayForecast.amForecast.temperature;
        lowTemp = dayForecast.amForecast.temperature;
        forecastDesc = dayForecast.amForecast.description;

        imageURL = dayForecast.icon;
    }

    //Stack Overflow
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
