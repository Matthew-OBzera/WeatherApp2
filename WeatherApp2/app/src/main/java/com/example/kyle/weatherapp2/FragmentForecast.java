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

    private TextView location, highVal, lowVal, forecast, amTab, pmTab;
    private ImageView icon;


    private String areaLocation, imageURL, forecastDesc;
    private double highTemp, lowTemp;

    private boolean tabFlag;

    int index;
    WeatherInfo weatherInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate((R.layout.forecast_fragment), container, false);
        getTextView(view);

        amTab.setOnClickListener(handleAM);
        pmTab.setOnClickListener(handlePM);


        // Inflate the layout for this fragment
        return view;
    }

    View.OnClickListener handleAM = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tabFlag = false;
            forecast.setBackgroundColor(getResources().getColor(R.color.amTabColor));
            setInfo(weatherInfo,index);
        }
    };

    View.OnClickListener handlePM = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tabFlag = true;
            forecast.setBackgroundColor(getResources().getColor(R.color.pmTabColor));
            setInfo(weatherInfo, index);
        }
    };



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void setText() {
        String locStr, highStr, lowStr, forecastStr;
        locStr = areaLocation;
        forecastStr = forecastDesc;
        switch(MainActivity.UNIT) {
            case MainActivity.IMPERIAL:
                highStr = String.valueOf((int) highTemp) + "° F";
                lowStr = String.valueOf((int) lowTemp) + "° F";
                break;
            case MainActivity.METRIC:
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
        amTab = (TextView)view.findViewById(R.id.amView);
        pmTab = (TextView)view.findViewById(R.id.pmView);

        icon = (ImageView)view.findViewById(R.id.forecastImg);
    }

    public void setInfo(WeatherInfo info,int index) {
        weatherInfo = info;
        this.index = index;

        areaLocation = info.location.name;

        if(info.forecast.get(index).amForecast != null) {
            highTemp = info.forecast.get(index).amForecast.temperature;
            amTab.setText("AM\n" + info.forecast.get(index).amForecast.description);
        }
        else {
            highTemp = 0.0;
            amTab.setText("AM\nNot Available at this time.");
        }

        if(info.forecast.get(index).pmForecast != null) {
            lowTemp = info.forecast.get(index).pmForecast.temperature;
            pmTab.setText("PM\n" + info.forecast.get(index).pmForecast.description);
        }
        else {
            lowTemp = 0.0;
            pmTab.setText("PM\nNot Available at this time.");
        }

        if(!tabFlag) {
            if(info.forecast.get(index).amForecast != null){
                forecastDesc = info.forecast.get(index).amForecast.details;
            }
            else {
                forecastDesc = "Not Available at this time.";
            }
        }
        else {
            if(info.forecast.get(index).pmForecast != null) {
                forecastDesc = info.forecast.get(index).pmForecast.details;
            }
            else {
                forecastDesc = "Not Available at this time.";
            }
        }


        imageURL = info.forecast.get(index).icon;

        setText();
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
