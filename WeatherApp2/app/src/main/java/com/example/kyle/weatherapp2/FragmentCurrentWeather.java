package com.example.kyle.weatherapp2;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.InputStream;
import java.util.LinkedList;

public class FragmentCurrentWeather extends Fragment {

    private TextView currentTimeText, conditionText, tempText, dewPointText, relHumidityText, pressureText, visibilityText,
            windspeedText, gustsText;
    private RadioGroup radGrp;

    private ImageView image;

    private double temperature, dewPoint, humidity, pressure, visibility, windspeed, gusts;
    private String windDirection, timeStamp, conditions;

    private boolean go = false;

    private LinkedList<String> recentZipcodes;
    private String imageURL;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate((R.layout.currentweather_fragment), container, false);
        getTextView(view);

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        radGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup grp, int id) {
                if (go) {
                    setText(id);
                }
            }
        });
    }

    private void setText(int id) {
        String timeStampStr, conditionsStr, tempStr, dewStr, presStr, visStr, windStr, gustStr, humidStr;
        switch (id) {
            case R.id.imperial:
                System.out.println("got here3");
                timeStampStr = timeStamp;
                conditionsStr = conditions;
                tempStr = String.valueOf((int) temperature) + "° F";
                dewStr = String.valueOf((int) dewPoint) + "° F";
                humidStr = String.valueOf((int) humidity) + "%";
                presStr = String.valueOf((int) pressure) + " in";
                visStr = String.valueOf((int) visibility) + " mi";
                windStr = windDirection + " @ "
                        + String.valueOf((int) windspeed) + " mph";
                if (Double.isNaN(gusts)) {
                    gustStr = "NA";
                } else {
                    gustStr = String.valueOf((int) (gusts)) + " mph";
                }
                break;
            case R.id.metric:
                timeStampStr = timeStamp;
                conditionsStr = conditions;
                tempStr = String.valueOf((int) ((temperature - 32) * (5.0 / 9.0))) + "° C";
                dewStr = String.valueOf((int) ((dewPoint - 32) * (5.0 / 9.0))) + "° C";
                humidStr = String.valueOf((int) humidity) + "%";
                presStr = String.valueOf((int) (pressure * 2.54)) + " cm";
                visStr = String.valueOf((int) (visibility * 1.6093)) + " km";
                windStr = windDirection + " @ "
                        + String.valueOf((int) (windspeed * 1.6093)) + " km/h";
                if (Double.isNaN(gusts)) {
                    gustStr = "NA";
                } else {
                    gustStr = String.valueOf((int) (gusts * 1.6093)) + " km/h";
                }

                break;
            default:
                timeStampStr = "";
                conditionsStr = "";
                tempStr = "";
                dewStr = "";
                humidStr = "";
                presStr = "";
                visStr = "";
                windStr = "";
                gustStr = "";
                break;
        }
        currentTimeText.setText(timeStampStr);
        conditionText.setText(conditionsStr);
        tempText.setText(tempStr);
        dewPointText.setText(dewStr);
        relHumidityText.setText(humidStr);
        pressureText.setText(presStr);
        visibilityText.setText(visStr);
        windspeedText.setText(windStr);
        gustsText.setText(gustStr);

        new DownloadImageTask(image).execute(imageURL);


    }

    private void getTextView(View view) {

        currentTimeText = (TextView) view.findViewById(R.id.timeStampVal);
        conditionText = (TextView) view.findViewById(R.id.conditionsVal);
        tempText = (TextView) view.findViewById(R.id.temperatureVal);
        dewPointText = (TextView) view.findViewById(R.id.dewPointVal);
        relHumidityText = (TextView) view.findViewById(R.id.relativeHumidityVal);
        pressureText = (TextView) view.findViewById(R.id.pressureVal);
        visibilityText = (TextView) view.findViewById(R.id.visibilityVal);
        windspeedText = (TextView) view.findViewById(R.id.windSpeedVal);
        gustsText = (TextView) view.findViewById(R.id.gustsVal);
        radGrp = (RadioGroup) view.findViewById(R.id.radioGroup);
        image = (ImageView) view.findViewById(R.id.imageView);

    }

    public void setInfo(WeatherInfo info, Context context) {

        temperature = info.current.temperature;
        dewPoint = info.current.dewPoint;
        humidity = info.current.humidity;
        pressure = info.current.pressure;
        visibility = info.current.visibility;
        windspeed = info.current.windSpeed;
        gusts = info.current.gusts;

        windDirection = info.current.windDirectionStr();
        timeStamp = info.current.timestamp;
        conditions = info.current.summary;
        imageURL = info.current.imageUrl;

        go = true;
        setText(radGrp.getCheckedRadioButtonId());
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

}

